package io.deeplay.qchess.client.service.special;

import static io.deeplay.qchess.clientserverconversation.dto.RequestType.CHAT_MESSAGE;

import io.deeplay.qchess.client.Client;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.handlers.ServerRequestHandler;

/** Обрабатывает текстовые команды */
public class ClientCommandService {

    public static void handleCommand(String command, Client client) {
        if (command.startsWith("msg ")) {
            client.getInputTrafficHandler()
                    .send(
                            ServerRequestHandler.convertToClientToServerDTO(
                                    CHAT_MESSAGE, command.substring(4)));
        }
        if (command.equals("disconnect")) {
            try {
                client.disconnect();
            } catch (ClientException ignore) {
                // Сервис может вызываться только при подключенном клиенте
            }
        }
    }
}
