package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.dto.servertoclient.ChatMessageDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;

public class ChatService {

    public static String incomingMessage(ServerToClientType type, String json)
            throws SerializationException {
        assert type.getDTO() == ChatMessageDTO.class;
        ChatMessageDTO dto =
                SerializationService.serverToClientDTORequest(json, ChatMessageDTO.class);
        ClientController.print("Пришло сообщение: " + dto.message);
        return null;
    }
}
