package io.deeplay.qchess.server.service;

import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;

public class ChatService {

    public static String incomingMessage(String json) {
        ServerController.getView().ifPresent(v -> v.print("Пришло сообщение: " + json));
        try {
            // TODO: удалить/изменить. Отправляет сообщение всем, включая писавшего
            ServerController.executeCommand("msg " + json);
        } catch (ServerException ignore) {
            // Сервис может вызываться только при открытом сервере
        }
        return null;
    }
}
