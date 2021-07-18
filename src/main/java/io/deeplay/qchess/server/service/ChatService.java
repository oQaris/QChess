package io.deeplay.qchess.server.service;

import static io.deeplay.qchess.server.dto.RequestType.CHAT_MESSAGE;

import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.dto.response.ServerToClientDTO;

public class ChatService {

    /** @return json */
    public static String convertMessageToServerToClientDTO(String message) {
        ServerToClientDTO dto = new ServerToClientDTO(CHAT_MESSAGE, message);
        return SerializationService.serialize(dto);
    }

    public static String incomingMessage(String message) {
        ServerController.getView().ifPresent(v -> v.print("Пришел json: " + message));
        ServerController.sendMessageAll(message);
        return null;
    }
}
