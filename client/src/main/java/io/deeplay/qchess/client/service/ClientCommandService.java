package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ChatMessageDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;

/** Обрабатывает текстовые команды */
public class ClientCommandService {

    /** @throws ClientException если при выполнении команды возникла ошибка */
    public static void handleCommand(final String command) throws ClientException {
        if (command.startsWith("msg ")) {
            ClientController.sendIfNotNull(
                    SerializationService.makeMainDTOJsonToServer(
                            new ChatMessageDTO(
                                    SessionDAO.getSessionToken(), command.substring(4))));
        }
        if (command.equals("disconnect")) {
            ClientController.disconnect("Клиент отключен");
        }
    }
}
