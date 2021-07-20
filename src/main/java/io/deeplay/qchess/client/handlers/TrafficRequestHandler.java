package io.deeplay.qchess.client.handlers;

import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.CHAT_MESSAGE;
import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.GET;
import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.INCORRECT_REQUEST;
import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.MOVE;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.UNKNOWN_REQUEST;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.service.ChatService;
import io.deeplay.qchess.client.service.GameService;
import io.deeplay.qchess.clientserverconversation.dto.MainRequestType;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import java.io.IOException;
import java.util.Map;
import java.util.function.UnaryOperator;

/** Перенаправляет запрос требуемому сервису */
public class TrafficRequestHandler {
    private static final Map<MainRequestType, UnaryOperator<String>> redirector =
            Map.of(
                    INCORRECT_REQUEST,
                    s -> null,
                    CHAT_MESSAGE,
                    ChatService::incomingMessage,
                    MOVE,
                    GameService::action,
                    GET, // TODO: заменить на POST
                    s -> null); // TODO: убрать заглушку

    /**
     * @return json ответ клиента в виде ClientToServerDTO или null, если не нужно ничего отправлять
     */
    public static String process(String jsonServerRequest) {
        ClientController.getView().ifPresent(v -> v.print("Пришел json: " + jsonServerRequest));
        String response;
        try {
            ServerToClientDTO dtoRequest =
                    SerializationService.deserialize(jsonServerRequest, ServerToClientDTO.class);
            response = redirector.get(dtoRequest.mainRequestType).apply(dtoRequest.request);
            if (response != null) {
                response = convertToClientToServerDTO(dtoRequest.mainRequestType, response);
            }
        } catch (IOException e) {
            response = convertToClientToServerDTO(INCORRECT_REQUEST, UNKNOWN_REQUEST.getMessage());
        }
        return response;
    }

    /**
     * Создает ClientToServerDTO из mainRequestType и json, затем сериализует
     *
     * @return json
     */
    public static String convertToClientToServerDTO(MainRequestType mainRequestType, String json) {
        return SerializationService.serialize(new ClientToServerDTO(mainRequestType, json));
    }
}
