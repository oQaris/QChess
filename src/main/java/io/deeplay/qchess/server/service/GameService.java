package io.deeplay.qchess.server.service;

import io.deeplay.qchess.server.controller.ServerController;

/** Управляет играми */
public class GameService {

    /** Выполняет игровое действие */
    public static String action(String json) {
        ServerController.getView().ifPresent(v -> v.print("Пришел json: " + json));
        return json;
    }
}
