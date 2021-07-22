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

    public static String setConnection(ClientToServerType type, String json, int clientID)
            throws SerializationException {
        assert type.getDTO() == ConnectionDTO.class;
        ConnectionDTO dto =
                SerializationService.clientToServerDTORequest(json, ConnectionDTO.class);

        if (dto.connection) {
            String newSessionToken;
            if (dto.sessionToken != null && ConnectionControlDAO.contains(dto.sessionToken)) {
                newSessionToken = dto.sessionToken;
            } else {
                newSessionToken = UUID.randomUUID().toString();
                ConnectionControlDAO.addPlayer(newSessionToken, clientID);
            }
            GameService.addOrReplacePlayer(newSessionToken);
            return SerializationService.makeMainDTOJsonToClient(
                    new AcceptConnectionDTO(newSessionToken));
        } else {
            ConnectionControlDAO.removePlayer(dto.sessionToken);
            GameService.removePlayer(dto.sessionToken);
            try {
                ServerController.closeConnection(clientID);
            } catch (ServerException ignore) {
                // Сервис вызывается при открытом сервере
            }
        }

        return null;
    }
}
