package io.deeplay.qchess.server.service;

import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ChatMessageDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;

public class ChatService {

    public static String incomingMessage(ClientToServerType type, String json, int clientId)
            throws SerializationException {
        assert type.getDTO() == ChatMessageDTO.class;
        ChatMessageDTO dto =
                SerializationService.clientToServerDTORequest(json, ChatMessageDTO.class);
        ServerController.print(
                String.format("Пришло сообщение от клиента %d: %s", clientId, dto.message));
        // TODO: удалить/изменить. Отправляет сообщение всем, включая писавшего
        try {
            ServerController.executeCommand("msg " + json);
        } catch (ServerException ignore) {
            // Сервис используется при включенном сервере
        }
        return null;
    }
}
