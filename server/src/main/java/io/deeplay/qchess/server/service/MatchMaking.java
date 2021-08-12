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
import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.dao.GameDAO;
import io.deeplay.qchess.server.database.Room;
import io.deeplay.qchess.server.exceptions.ServerException;

/** Управляет подбором игр по предпочитаемым настройкам */
public class MatchMaking {

    private MatchMaking() {}

    public static String findGame(ClientToServerType type, String json, int clientId)
            throws SerializationException {
        assert type.getDTO() == FindGameDTO.class;
        FindGameDTO dto = SerializationService.clientToServerDTORequest(json, FindGameDTO.class);

        // Пока не найдется комната или свободных комнат нет
        while (true) {
            Room room = GameDAO.findSuitableRoom(dto.sessionToken, dto.enemyType, dto.gameCount);
            if (room == null) {
                return SerializationService.makeMainDTOJsonToClient(
                        new DisconnectedDTO("Нет свободных комнат"));
            }
            synchronized (room.mutex) {
                if (room.contains(dto.sessionToken)) {
                    return SerializationService.makeMainDTOJsonToClient(
                            new GameSettingsDTO(
                                    room.getPlayer(dto.sessionToken).getColor(),
                                    // TODO: проверки на null
                                    room.getGameSettings().history.getLastMove()));
                }

                if (room.isFull()) continue;

                GameSettings gs = new GameSettings(BoardFilling.STANDARD);
                room.setGameSettings(gs, dto.gameCount);

                RemotePlayer enemyBot =
                        switch (dto.enemyType) {
                            case CONSOLE_PLAYER, GUI_PLAYER -> null;
                            case RANDOM_BOT -> new RandomBot(gs, Color.BLACK);
                                // TODO: вставить своего бота
                            case ATTACK_BOT -> new RandomBot(gs, Color.BLACK);
                        };

                if (enemyBot == null && dto.enemyType != PlayerType.GUI_PLAYER) {
                    return SerializationService.makeMainDTOJsonToClient(
                            new DisconnectedDTO("Неверный тип противника"));
                }

                Color clientColor = room.isEmpty() ? Color.WHITE : Color.BLACK;

                try {
                    ServerController.send(
                            SerializationService.makeMainDTOJsonToClient(
                                    new GameSettingsDTO(clientColor, null)),
                            clientId);
                } catch (ServerException ignore) {
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
