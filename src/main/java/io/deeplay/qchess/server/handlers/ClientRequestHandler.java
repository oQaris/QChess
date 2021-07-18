package io.deeplay.qchess.server.handlers;

import static io.deeplay.qchess.server.dto.RequestType.CHAT_MESSAGE;
import static io.deeplay.qchess.server.dto.RequestType.INCORRECT_REQUEST;
import static io.deeplay.qchess.server.dto.RequestType.MOVE;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.UNKNOWN_REQUEST;

import io.deeplay.qchess.server.dto.RequestType;
import io.deeplay.qchess.server.dto.request.ClientToServerDTO;
import io.deeplay.qchess.server.dto.response.ServerToClientDTO;
import io.deeplay.qchess.server.service.ChatService;
import io.deeplay.qchess.server.service.GameService;
import io.deeplay.qchess.server.service.SerializationService;
import java.io.IOException;
import java.util.Map;
import java.util.function.UnaryOperator;

/** Перенаправляет запрос требуемому сервису */
public class ClientRequestHandler {
    private static final Map<RequestType, UnaryOperator<String>> redirector =
            Map.of(MOVE, GameService::action, CHAT_MESSAGE, ChatService::incomingMessage);

    /**
     * @return json ответ сервера в виде ServerToClientDTO или null, если не нужно ничего отправлять
     */
    public static String process(String jsonServerRequest) {
        String response;
        try {
            ClientToServerDTO clientToServerDTO =
                    SerializationService.deserialize(jsonServerRequest, ClientToServerDTO.class);
            response =
                    redirector.get(clientToServerDTO.requestType).apply(clientToServerDTO.request);
            if (response != null) {
                ServerToClientDTO serverToClientDTO =
                        new ServerToClientDTO(clientToServerDTO.requestType, response);
                response = SerializationService.serialize(serverToClientDTO);
            }
        } catch (IOException e) {
            ServerToClientDTO serverToClientDTO =
                    new ServerToClientDTO(INCORRECT_REQUEST, UNKNOWN_REQUEST.getMessage());
            response = SerializationService.serialize(serverToClientDTO);
        }
        return response;
    }
}
