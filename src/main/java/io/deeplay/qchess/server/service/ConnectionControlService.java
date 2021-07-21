package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.MainRequestType;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.handlers.ClientRequestHandler;

public class ConnectionControlService {

    public static String getJsonToDisconnect() {
        return ClientRequestHandler.convertToServerToClientDTO(MainRequestType.DISCONNECT, null);
    }

    public static String disconnect(String json, int clientID) {
        GameService.removePlayer(clientID);
        try {
            ServerController.closeConnection(clientID);
        } catch (ServerException ignore) {
        }
        return null;
    }
}
