package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ChatMessageDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;

/** Обрабатывает текстовые команды */
public class ServerCommandService {

    private ServerCommandService(){}

    public static void handleCommand(String command) throws ServerException {
        if (command.startsWith("msg ")) {
            ServerController.sendAll(
                    SerializationService.makeMainDTOJsonToClient(
                            new ChatMessageDTO(command.substring(4))));
        }
        if (command.equals("stop")) {
            ServerController.stopServer();
        }
    }
}
