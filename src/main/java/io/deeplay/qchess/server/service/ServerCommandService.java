package io.deeplay.qchess.server.service;

import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.CHAT_MESSAGE;

import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.handlers.ClientRequestHandler;

/** Обрабатывает текстовые команды */
public class ServerCommandService {

    public static void handleCommand(String command) throws ServerException {
        if (command.startsWith("msg ")) {
            ServerController.sendAll(
                    ClientRequestHandler.convertToServerToClientDTO(
                            CHAT_MESSAGE, command.substring(4)));
        }
        if (command.equals("stop")) {
            ServerController.stopServer();
        }
    }
}
