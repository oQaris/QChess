package io.deeplay.qchess.server.handlers;

import static io.deeplay.qchess.clientserverconversation.dto.RequestType.CHAT_MESSAGE;
import static io.deeplay.qchess.clientserverconversation.dto.RequestType.INCORRECT_REQUEST;
import static io.deeplay.qchess.clientserverconversation.dto.RequestType.MOVE;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.UNKNOWN_REQUEST;

import io.deeplay.qchess.clientserverconversation.dto.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.RequestType;
import io.deeplay.qchess.clientserverconversation.dto.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.service.ChatService;
import io.deeplay.qchess.server.service.GameService;
import java.io.IOException;
import java.util.Map;
import java.util.function.UnaryOperator;

/** Перенаправляет запрос требуемому сервису и запаковывает ответ от него */
public class ClientRequestHandler {
    private static final Map<RequestType, UnaryOperator<String>> redirector =
            Map.of(
                    INCORRECT_REQUEST,
                    s -> null,
                    CHAT_MESSAGE,
                    ChatService::incomingMessage,
                    MOVE,
                    GameService::action);

    /**
     * @return json ответ сервера в виде ServerToClientDTO или null, если не нужно ничего отправлять
     */
    public static String process(String jsonClientRequest) {
        ServerController.getView().ifPresent(v -> v.print("Пришел json: " + jsonClientRequest));
        String response;
        try {
            ClientToServerDTO dtoRequest =
                    SerializationService.deserialize(jsonClientRequest, ClientToServerDTO.class);
            response = redirector.get(dtoRequest.requestType).apply(dtoRequest.request);
            if (response != null) {
                response = convertToServerToClientDTO(dtoRequest.requestType, response);
            }
        } catch (IOException e) {
            response = convertToServerToClientDTO(INCORRECT_REQUEST, UNKNOWN_REQUEST.getMessage());
        }
        return response;
    }

    /**
     * Создает ServerToClientDTO из requestType и json, затем сериализует
     *
     * @return json
     */
    public static String convertToServerToClientDTO(RequestType requestType, String json) {
        return SerializationService.serialize(new ServerToClientDTO(requestType, json));
    }
}
