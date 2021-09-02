package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.FindGameDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.GameSettingsDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.QNegamaxBot;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.dao.GameDAO;
import io.deeplay.qchess.server.database.Room;
import io.deeplay.qchess.server.exceptions.ServerException;

/** Управляет подбором игр по предпочитаемым настройкам */
public class MatchMaking {

    private MatchMaking() {}

    public static String findGame(
            final ClientToServerType type, final String json, final int clientId)
            throws SerializationException {
        assert type.getDTO() == FindGameDTO.class;
        final FindGameDTO dto =
                SerializationService.clientToServerDTORequest(json, FindGameDTO.class);

        // Пока не найдется комната или свободных комнат нет
        while (true) {
            final Room room =
                    GameDAO.findSuitableRoom(
                            dto.sessionToken, dto.enemyType, dto.gameCount, dto.myPreferColor);
            if (room == null) {
                return SerializationService.makeMainDTOJsonToClient(
                        new DisconnectedDTO("Нет свободных комнат"));
            }
            synchronized (room.mutex) {
                if (room.contains(dto.sessionToken)) {
                    return SerializationService.makeMainDTOJsonToClient(
                            new GameSettingsDTO(
                                    room.getPlayer(dto.sessionToken).getColor(),
                                    room.getGameSettings().history.getLastMove()));
                }

                if (room.isFull()) continue;

                final GameSettings gs = new GameSettings(BoardFilling.STANDARD);
                room.setGameSettings(gs, dto.gameCount);

                final Color clientColor =
                        dto.myPreferColor == null
                                ? room.isEmpty()
                                        ? Color.WHITE
                                        : room.getFirstPlayer().getColor().inverse()
                                : dto.myPreferColor;

                final RemotePlayer enemyBot =
                        switch (dto.enemyType) {
                            case LOCAL_PLAYER, REMOTE_PLAYER -> null;
                            case RANDOM_BOT -> new RandomBot(gs, clientColor.inverse());
                            case ATTACK_BOT -> new AttackBot(gs, clientColor.inverse());
                            case QBOT -> new QNegamaxBot(gs, clientColor.inverse());
                        };

                if (enemyBot == null && dto.enemyType != PlayerType.REMOTE_PLAYER) {
                    return SerializationService.makeMainDTOJsonToClient(
                            new DisconnectedDTO("Неверный тип противника"));
                }

                try {
                    ServerController.send(
                            SerializationService.makeMainDTOJsonToClient(
                                    new GameSettingsDTO(clientColor, null)),
                            clientId);
                } catch (final ServerException ignore) {
                    // Сервис вызывается при открытом сервере
                }

                GameService.putPlayerToRoom(
                        room, new RemotePlayer(gs, clientColor, dto.sessionToken, "user"));
                if (enemyBot != null) GameService.putPlayerToRoom(room, enemyBot);
            }
            return null;
        }
    }
}
