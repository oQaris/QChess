package io.deeplay.qchess.client.service;

import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.CHAT_MESSAGE;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.handlers.TrafficRequestHandler;

/** Обрабатывает текстовые команды */
public class ClientCommandService {

    /** @throws ClientException если при выполнении команды возникла ошибка */
    public static void handleCommand(String command) throws ClientException {
        if (command.startsWith("msg ")) {
            ClientController.sendIfNotNull(
                    TrafficRequestHandler.convertToClientToServerDTO(
                            CHAT_MESSAGE, command.substring(4)));
        }
        if (command.equals("disconnect")) {
            ClientController.disconnect("Клиент отключен");
        }
    }
}
