package io.deeplay.qchess.server.service;

import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;

public class ChatService {

    public static String incomingMessage(String json, int clientID) {
        ServerController.getView()
                .ifPresent(
                        v ->
                                v.print(
                                        String.format(
                                                "Пришло сообщение от клиента %d: %s",
                                                clientID, json)));
        // TODO: удалить/изменить. Отправляет сообщение всем, включая писавшего
        try {
            ServerController.executeCommand("msg " + json);
        } catch (ServerException ignore) {
        }
        return null;
    }
}
