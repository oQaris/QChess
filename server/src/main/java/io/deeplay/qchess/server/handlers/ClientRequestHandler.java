package io.deeplay.qchess.server.handlers;

import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ClientToServerType;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.server.service.ChatService;
import io.deeplay.qchess.server.service.ConnectionControlService;
import io.deeplay.qchess.server.service.GameService;
import io.deeplay.qchess.server.service.MatchMaking;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Перенаправляет запрос требуемому сервису и запаковывает ответ от него */
public class ClientRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequestHandler.class);
    private static final Map<ClientToServerType, Handler> redirector =
            new EnumMap<>(ClientToServerType.class);

    static {
        redirector.putAll(
                Map.of(
                        ClientToServerType.SET_CONNECTION,
                        ConnectionControlService::setConnection,
                        ClientToServerType.FIND_GAME,
                        MatchMaking::findGame,
                        ClientToServerType.GAME_ACTION,
                        GameService::action,
                        ClientToServerType.CHAT_MESSAGE,
                        ChatService::incomingMessage));
        assert redirector.size() == ClientToServerType.values().length;
    }

    /**
     * @return json ответ сервера в виде ServerToClientDTO или null, если не нужно ничего отправлять
     */
    public static String process(String jsonClientRequest, int clientID) {
        logger.debug("От клиента <{}> пришел json: {}", clientID, jsonClientRequest);
        try {
            ClientToServerDTO mainDTO =
                    SerializationService.clientToServerDTOMain(jsonClientRequest);
            String response =
                    redirector.get(mainDTO.type).handle(mainDTO.type, mainDTO.json, clientID);

            if (response != null)
                logger.debug("Отправлен json клиенту <{}>: {}", clientID, jsonClientRequest);

            return response;
        } catch (SerializationException e) {
            logger.warn(
                    "Пришел некорректный json от клиента <{}>: {}", clientID, jsonClientRequest);
            return null;
        } catch (NullPointerException e) {
            logger.warn(
                    "Получен неизвестный запрос от клиента <{}>: {}", clientID, jsonClientRequest);
            return null;
        }
    }

    @FunctionalInterface
    private interface Handler {
        String handle(ClientToServerType type, String json, int clientID)
                throws SerializationException;
    }
}
