package io.deeplay.qchess.server.service.special;

import static io.deeplay.qchess.clientserverconversation.dto.RequestType.CHAT_MESSAGE;

import io.deeplay.qchess.server.LocalHost;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.handlers.ClientRequestHandler;

/** Обрабатывает текстовые команды */
public class ServerCommandService {

    public static void handleCommand(String command, LocalHost server) {
        if (command.startsWith("msg ")) {
            server.getClientHandlerManager()
                    .sendAll(
                            ClientRequestHandler.convertToServerToClientDTO(
                                    CHAT_MESSAGE, command.substring(4)));
        }
        if (command.equals("stop")) {
            try {
                server.stopServer();
            } catch (ServerException ignore) {
                // Сервис может вызываться только при открытом сервере
            }
        }
    }
}
