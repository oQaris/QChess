package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.AcceptConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.dao.ConnectionControlDAO;
import io.deeplay.qchess.server.exceptions.ServerException;
import java.util.UUID;

public class ConnectionControlService {

    public static String getJsonToDisconnect(String reason) {
        return SerializationService.makeMainDTOJsonToClient(new DisconnectedDTO(reason));
    }

    public static String setConnection(ClientToServerType type, String json, int clientId)
            throws SerializationException {
        assert type.getDTO() == ConnectionDTO.class;
        ConnectionDTO dto =
                SerializationService.clientToServerDTORequest(json, ConnectionDTO.class);

        if (dto.connection) {
            if (dto.sessionToken != null && ConnectionControlDAO.contains(dto.sessionToken)) {
                disconnect(dto.sessionToken, "Уже подключен");
                return null;
            } else {
                String newSessionToken = UUID.randomUUID().toString();
                ConnectionControlDAO.addPlayer(newSessionToken, clientId);
                return SerializationService.makeMainDTOJsonToClient(
                        new AcceptConnectionDTO(newSessionToken));
            }
        } else {
            disconnect(dto.sessionToken, "Отключен");
        }

        return null;
    }

    public static void disconnect(String sessionToken, String reason) {
        Integer clientId = ConnectionControlDAO.getId(sessionToken);
        if (clientId == null) return;
        GameService.endGameForOpponentOf(sessionToken);
        ConnectionControlDAO.removePlayer(sessionToken);
        try {
            ServerController.send(
                    SerializationService.makeMainDTOJsonToClient(new DisconnectedDTO(reason)),
                    clientId);
            // TODO: убрать костыль (перенести id клиентов в БД), возвращать Json
            ServerController.closeConnection(clientId);
        } catch (ServerException ignore) {
            // Сервис вызывается при открытом сервере
        }
    }
}
