package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ActionDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.EndGameDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.dao.ConnectionControlDAO;
import io.deeplay.qchess.server.dao.GameDAO;
import io.deeplay.qchess.server.database.Room;
import io.deeplay.qchess.server.exceptions.ServerException;

/** Управляет играми */
public class GameService {

    public static void addOrReplacePlayer(String sessionToken) {
        Room room = GameDAO.getRoom();
        room.addPlayer(sessionToken);
        if (room.isFull()) room.startGame();
        if (room.isError()) {
            // TODO: критическая ошибка в игре
        }
    }

    public static void removePlayer(String sessionToken) {
        Room room = GameDAO.getRoom();
        Integer opponentID = ConnectionControlDAO.getID(room.getOpponentSessionToken(sessionToken));
        if (room.removePlayer(sessionToken)) {
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

        Room room = GameDAO.getRoom();
        if (room.isStarted()) {
            boolean correct = room.move(dto.move);
            if (room.isError()) {
                // TODO: критическая ошибка в игре
                return null;
            }
            if (!correct) {
                // TODO: некорректный ход
                return null;
            }
            // todo send 2 player
            int player2id =
                    ConnectionControlDAO.getID(room.getOpponentSessionToken(dto.sessionToken));
            try {
                sendMove(dto.move, player2id);
            } catch (ServerException ignore) {
                // Сервис вызывается при открытом сервере
            }
        }
        return null;
    }

    public static void sendMove(Move move, int clientID) throws ServerException {
        ServerController.send(
                SerializationService.makeMainDTOJsonToClient(
                        new io.deeplay.qchess.clientserverconversation.dto.servertoclient.ActionDTO(
                                move)),
                clientID);
    }
}
