package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.EndGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ResetGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.StartGameDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.dao.ConnectionControlDAO;
import io.deeplay.qchess.server.dao.GameDAO;
import io.deeplay.qchess.server.database.Room;
import io.deeplay.qchess.server.exceptions.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Управляет играми */
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private GameService() {}

    public static void putPlayerToRoom(final Room room, final RemotePlayer player) {
        room.addPlayer(player);
        if (room.isFull()) {
            room.startGame();
            final Integer id1 = ConnectionControlDAO.getId(room.getFirstPlayerToken());
            final Integer id2 = ConnectionControlDAO.getId(room.getSecondPlayerToken());
            try {
                if (id1 != null)
                    ServerController.send(
                            SerializationService.makeMainDTOJsonToClient(new StartGameDTO()), id1);
                if (id2 != null)
                    ServerController.send(
                            SerializationService.makeMainDTOJsonToClient(new StartGameDTO()), id2);

                // Если первый игрок (белый) - это бот
                if (id1 == null) {
                    final Move move = player.getNextMove();
                    room.move(move);
                    StatisticService.writeMoveStats(room.id, room.getGameCount(), move);
                    sendMove(room.getFirstPlayerToken(), room.getSecondPlayerToken(), room, move);
                }
            } catch (final ServerException | ChessError ignore) {
                // Сервис вызывается при открытом сервере
                // Ошибок в боте быть не может
            }
        }
        if (room.isError()) {
            // TODO: критическая ошибка в игре (невозможна? но это не точно)
        }
    }

    /**
     * Закрывает игру и отправляет все необходимые запросы только ОППОНЕНТУ для заданного игрока по
     * причине выхода последнего
     */
    public static void endGameForOpponentOf(final String sessionToken) {
        final Room room = GameDAO.getRoom(sessionToken);
        if (room == null) return;
        synchronized (room.mutex) {
            // TODO: поток может получить доступ в комнату после ее закрытия оппонентом и открытия
            //  другим игроком, но это бывает ооооочень редко (не бывает). Чтобы это исправить
            //   необходимо создавать комнаты в БД, а не использовать существующие
            if (room.isEmpty()) return;

            final String opponentToken = room.getOpponentSessionToken(sessionToken);
            final Integer opponentID = ConnectionControlDAO.getId(opponentToken);

            if (opponentID != null && !room.isFinished()) {
                sendEndGameAndDisconnect("Оппонент покинул игру, вы победили!", opponentID);
            }
            room.resetRoom();
        }
    }

    /** Выполняет игровое действие */
    public static String action(
            final ClientToServerType type, final String json, final int clientId)
            throws SerializationException {
        assert type.getDTO() == ActionDTO.class;
        final ActionDTO dto = SerializationService.clientToServerDTORequest(json, ActionDTO.class);

        final Room room = GameDAO.getRoom(dto.sessionToken);
        if (room == null) {
            ConnectionControlService.disconnect(dto.sessionToken, "Вашей комнаты не существует");
            return null;
        }
        synchronized (room.mutex) {
            if (room.isStarted()) {
                final boolean correct = room.move(dto.move);
                if (room.isError()) {
                    // TODO: критическая ошибка в игре (невозможна? но это не точно)
                    return null;
                }
                if (!correct) {
                    // TODO: некорректный ход (не убирать туду)
                    //  сейчас некорректный ход будет считаться как проигрыш, но, возможно, это
                    //   ошибка передачи данных
                    ConnectionControlService.disconnect(dto.sessionToken, "Некорректный ход");
                    return null;
                }
                try {
                    StatisticService.writeMoveStats(room.id, room.getGameCount(), dto.move);
                    sendMove(
                            dto.sessionToken,
                            room.getOpponentSessionToken(dto.sessionToken),
                            room,
                            dto.move);
                } catch (final ServerException | ChessError e) {
                    logger.error("Возникла ошибка в игровом сервисе: {}", e.getMessage());
                    // Сервис вызывается при открытом сервере
                    // TODO: критическая ошибка в игре (невозможна? но это не точно)
                }
            }
        }
        return null;
    }

    private static void sendMove(
            final String fromToken, final String toToken, final Room room, Move move)
            throws ServerException, ChessError {
        final RemotePlayer player = room.getPlayer(toToken);
        String sendToken = toToken;

        String status = room.getEndGameStatus();
        if (status == null && player.getPlayerType() != PlayerType.REMOTE_PLAYER) {
            move = player.getNextMove();
            room.move(move);
            sendToken = fromToken;
            StatisticService.writeMoveStats(room.id, room.getGameCount(), move);
        }
        status = room.getEndGameStatus();

        final Integer clientId = ConnectionControlDAO.getId(sendToken);
        if (clientId != null)
            ServerController.send(
                    SerializationService.makeMainDTOJsonToClient(
                            new io.deeplay.qchess.clientserverconversation.dto.servertoclient
                                    .ActionDTO(move)),
                    clientId);

        if (status != null) {
            final RemotePlayer player1 = room.getFirstPlayer();
            final RemotePlayer player2 = room.getSecondPlayer();
            room.addGameCount(1);
            StatisticService.writeEndGameStats(
                    room.id, room.getGameCount(), room.getEndGameStatus());

            if (room.getGameCount() >= room.getMaxGames()) {
                if (player1.getPlayerType() == PlayerType.REMOTE_PLAYER)
                    sendEndGameAndDisconnect(
                            room.getEndGameStatus(),
                            ConnectionControlDAO.getId(player1.getSessionToken()));
                if (player2.getPlayerType() == PlayerType.REMOTE_PLAYER)
                    sendEndGameAndDisconnect(
                            room.getEndGameStatus(),
                            ConnectionControlDAO.getId(player2.getSessionToken()));

                room.resetRoom();
            } else {
                status = room.getEndGameStatus();

                room.resetGame();

                if (player2.getPlayerType() != PlayerType.REMOTE_PLAYER) {
                    move = player2.getNextMove();
                    room.move(move);
                    StatisticService.writeMoveStats(room.id, room.getGameCount(), move);
                }

                if (player1.getPlayerType() == PlayerType.REMOTE_PLAYER)
                    sendResetRoom(status, ConnectionControlDAO.getId(player1.getSessionToken()));
                if (player2.getPlayerType() == PlayerType.REMOTE_PLAYER)
                    sendResetRoom(status, ConnectionControlDAO.getId(player2.getSessionToken()));
            }
        }
    }

    private static void sendResetRoom(final String reason, final Integer clientId) {
        if (clientId == null) return;
        try {
            ServerController.send(
                    SerializationService.makeMainDTOJsonToClient(new EndGameDTO(reason)), clientId);
            ServerController.send(
                    SerializationService.makeMainDTOJsonToClient(new ResetGameDTO()), clientId);
        } catch (final ServerException ignore) {
            // Сервис вызывается при открытом сервере
        }
    }

    private static void sendEndGameAndDisconnect(final String reason, final Integer clientId) {
        if (clientId == null) return;
        try {
            ServerController.send(
                    SerializationService.makeMainDTOJsonToClient(new EndGameDTO(reason)), clientId);
            ServerController.send(
                    SerializationService.makeMainDTOJsonToClient(
                            new DisconnectedDTO("Игра окончена")),
                    clientId);
        } catch (final ServerException ignore) {
            // Сервис вызывается при открытом сервере
        }
    }
}
