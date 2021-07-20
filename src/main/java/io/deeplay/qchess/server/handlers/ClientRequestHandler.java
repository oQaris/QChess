package io.deeplay.qchess.server.handlers;

import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.CHAT_MESSAGE;
import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.GET;
import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.INCORRECT_REQUEST;
import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.MOVE;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.UNKNOWN_REQUEST;

import io.deeplay.qchess.clientserverconversation.dto.MainRequestType;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.service.ChatService;
import io.deeplay.qchess.server.service.GameService;
import io.deeplay.qchess.server.service.GetRequestService;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;

/** Перенаправляет запрос требуемому сервису и запаковывает ответ от него */
public class ClientRequestHandler {
    private static final Map<MainRequestType, BiFunction<String, Integer, String>> redirector =
            Map.of(
                    INCORRECT_REQUEST,
                    (json, id) -> null,
                    CHAT_MESSAGE,
                    ChatService::incomingMessage,
                    MOVE,
                    GameService::action,
                    GET,
                    GetRequestService::process);

    /**
     * @return json ответ сервера в виде ServerToClientDTO или null, если не нужно ничего отправлять
     */
    public static String process(String jsonClientRequest, int clientID) {
        ServerController.getView().ifPresent(v -> v.print("Пришел json: " + jsonClientRequest));
        String response;
        try {
            ClientToServerDTO dtoRequest =
                    SerializationService.deserialize(jsonClientRequest, ClientToServerDTO.class);
            response =
                    redirector.get(dtoRequest.mainRequestType).apply(dtoRequest.request, clientID);
            if (response != null) {
                response = convertToServerToClientDTO(dtoRequest.mainRequestType, response);
            }
        } catch (IOException e) {
            response = convertToServerToClientDTO(INCORRECT_REQUEST, UNKNOWN_REQUEST.getMessage());
        }
        final String finalResponse = response;
        ServerController.getView().ifPresent(v -> v.print("Отправлен json: " + finalResponse));
        return response;
    }

    /**
     * Создает ServerToClientDTO из mainRequestType и json, затем сериализует
     *
     * @return json
     */
    public static String convertToServerToClientDTO(MainRequestType mainRequestType, String json) {
        return SerializationService.serialize(new ServerToClientDTO(mainRequestType, json));
    }
}
