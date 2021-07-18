package io.deeplay.qchess.client.handlers;

import io.deeplay.qchess.client.view.IClientView;
import java.util.Optional;
import java.util.function.Supplier;

/** Перенаправляет запрос требуемому сервису */
public class ServerRequestHandler {

    /**
     * @return json ответ клиента в виде ClientToServerDTO или null, если не нужно ничего отправлять
     */
    public static String process(String jsonServerRequest, Supplier<Optional<IClientView>> view) {
        view.get().ifPresent(v -> v.print("Пришел json: " + jsonServerRequest));
        return null;
    }
}
