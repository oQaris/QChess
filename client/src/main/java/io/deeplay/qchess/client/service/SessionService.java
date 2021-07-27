package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.AcceptConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.DisconnectedDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;

public class SessionService {

    public static String acceptConnection(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == AcceptConnectionDTO.class;
        AcceptConnectionDTO dto =
                SerializationService.serverToClientDTORequest(json, AcceptConnectionDTO.class);
        SessionDAO.setSessionToken(dto.sessionToken);
        return null;
    }

    public static String disconnect(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == DisconnectedDTO.class;
        DisconnectedDTO dto =
                SerializationService.serverToClientDTORequest(json, DisconnectedDTO.class);

        try {
            ClientController.closeGame(dto.reason);
            ClientController.disconnect("Сервер разорвал соединение: " + dto.reason);
        } catch (ClientException ignore) {
            // Сервис вызывается только после подключения
        }
        return null;
    }
}
