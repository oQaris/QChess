package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;

public class ChatService {

    public static String incomingMessage(String json) {
        ClientController.getView().ifPresent(v -> v.print("Пришло сообщение: " + json));
        return null;
    }
}
