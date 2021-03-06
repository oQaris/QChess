package io.deeplay.qchess.client.handlers;

import io.deeplay.qchess.client.service.ChatService;
import io.deeplay.qchess.client.service.GameService;
import io.deeplay.qchess.client.service.SessionService;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Перенаправляет запрос требуемому сервису */
public class TrafficRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(TrafficRequestHandler.class);
    private static final Map<ServerToClientType, Handler> redirector =
            new EnumMap<>(ServerToClientType.class);

    static {
        redirector.putAll(
                Map.of(
                        ServerToClientType.INCORRECT_REQUEST,
                        (type, json) -> null,
                        ServerToClientType.DISCONNECTED,
                        SessionService::disconnect,
                        ServerToClientType.ACCEPT_CONNECTION,
                        SessionService::acceptConnection,
                        ServerToClientType.GAME_SETTINGS,
                        GameService::setGameSettings,
                        ServerToClientType.START_GAME,
                        GameService::startGame,
                        ServerToClientType.GAME_ACTION,
                        GameService::action,
                        ServerToClientType.END_GAME,
                        GameService::endGame,
                        ServerToClientType.CHAT_MESSAGE,
                        ChatService::incomingMessage,
                        ServerToClientType.RESET_GAME,
                        GameService::resetGame));

        if (redirector.size() != ServerToClientType.values().length) {
            throw new ExceptionInInitializerError("В клиенте не рассмотрены все случаи запросов");
        }
    }

    /**
     * @return json ответ клиента в виде ClientToServerDTO или null, если не нужно ничего отправлять
     */
    public static String process(final String jsonServerRequest) {
        logger.debug("Пришел json: {}", jsonServerRequest);
        try {
            final ServerToClientDTO mainDTO =
                    SerializationService.serverToClientDTOMain(jsonServerRequest);
            final String response = redirector.get(mainDTO.type).handle(mainDTO.type, mainDTO.json);

            if (response != null) logger.debug("Отправлен json серверу: {}", jsonServerRequest);

            return response;
        } catch (final SerializationException e) {
            logger.warn("Пришел некорректный json от сервера: {}", jsonServerRequest);
            return null;
        } catch (final NullPointerException e) {
            logger.warn("Получен неизвестный запрос от сервера: {}", jsonServerRequest);
            return null;
        }
    }

    @FunctionalInterface
    private interface Handler {
        String handle(ServerToClientType type, String json) throws SerializationException;
    }
}
