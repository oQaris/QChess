package io.deeplay.qchess.client.service;

import static io.deeplay.qchess.server.dto.RequestType.CHAT_MESSAGE;

import io.deeplay.qchess.server.dto.request.ClientToServerDTO;
import io.deeplay.qchess.server.service.SerializationService;

public class ChatService {

    public static String convertToClientToServerDTO(String message) {
        ClientToServerDTO dto = new ClientToServerDTO(CHAT_MESSAGE, message);
        return SerializationService.serialize(dto);
    }
}
