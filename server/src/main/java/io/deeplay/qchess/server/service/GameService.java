package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.EndGameDTO;
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

    public static void putPlayerToRoom(Room room, RemotePlayer player) {
        room.addPlayer(player);
        if (room.isFull()) {
            room.startGame();
            Integer id = ConnectionControlDAO.getID(room.getFirstPlayerToken());
            try {
                if (id != null)
                    ServerController.send(
                            SerializationService.makeMainDTOJsonToClient(new StartGameDTO()), id);
            } catch (ServerException ignore) {
                // Сервис вызывается при открытом сервере
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
    public static void endGameForOpponentOf(String sessionToken) {
        Room room = GameDAO.getRoom(sessionToken);
        if (room == null) return;
        synchronized (room.mutex) {
            // TODO: поток может получить доступ в комнату после ее закрытия оппонентом и открытия
            //  другим игроком, но это бывает ооооочень редко (не бывает). Чтобы это исправить
            //   необходимо создавать комнаты в БД, а не использовать существующие
            if (room.isEmpty()) return;

            String opponentToken = room.getOpponentSessionToken(sessionToken);
            Integer opponentID = ConnectionControlDAO.getID(opponentToken);
            room.resetRoom();

            if (opponentID != null) {
                try {
                    ServerController.send(
                            SerializationService.makeMainDTOJsonToClient(
                                    new EndGameDTO("Оппонент покинул игру, вы победили!")),
                            opponentID);
                    ServerController.send(
                            SerializationService.makeMainDTOJsonToClient(
                                    new DisconnectedDTO("Игра окончена")),
                            opponentID);
                } catch (ServerException ignore) {
                    // Сервис вызывается при открытом сервере
                }
            }
        }
    }

    /** Выполняет игровое действие */
    public static String action(ClientToServerType type, String json, int clientID)
            throws SerializationException {
        assert type.getDTO() == ActionDTO.class;
        ActionDTO dto = SerializationService.clientToServerDTORequest(json, ActionDTO.class);

        Room room = GameDAO.getRoom(dto.sessionToken);
        if (room == null) {
            ConnectionControlService.disconnect(dto.sessionToken, "Вашей комнаты не существует");
            return null;
        }
        synchronized (room.mutex) {
            if (room.isStarted()) {
                boolean correct = room.move(dto.move);
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
                    sendMove(
                            dto.sessionToken,
                            room.getOpponentSessionToken(dto.sessionToken),
                            room,
                            dto.move);
                } catch (ServerException | ChessError e) {
                    logger.error("Возникла ошибка в игровом сервисе: {}", e.getMessage());
                    // Сервис вызывается при открытом сервере
                    // TODO: критическая ошибка в игре (невозможна? но это не точно)
                }
            }
        }
        return null;
    }

    private static void sendMove(String fromToken, String toToken, Room room, Move move)
            throws ServerException, ChessError {
        RemotePlayer player = room.getPlayer(toToken);
        String sendToken = toToken;

        if (player.getPlayerType() != PlayerType.REMOTE_PLAYER) {
            move = player.getNextMove();
            room.move(move);
            sendToken = fromToken;
        }

        Integer clientId = ConnectionControlDAO.getID(sendToken);
        if (clientId != null)
            ServerController.send(
                    SerializationService.makeMainDTOJsonToClient(
                            new io.deeplay.qchess.clientserverconversation.dto.servertoclient
                                    .ActionDTO(move)),
                    clientId);
    }
}
