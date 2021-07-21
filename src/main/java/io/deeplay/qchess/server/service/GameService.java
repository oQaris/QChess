package io.deeplay.qchess.server.service;

import static io.deeplay.qchess.clientserverconversation.dto.MainRequestType.MOVE;

import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.handlers.ClientRequestHandler;
import java.io.IOException;

/** Управляет играми */
public class GameService {
    private static final Object mutex = new Object();
    // TODO: вынести все в БД
    private static Selfplay game;
    private static GameSettings gs;
    private static RemotePlayer firstPlayer;
    private static RemotePlayer secondPlayer;

    // TODO: вынести
    public static Color getPlayerColor(int clientID) {
        // TODO: ОСТОРОЖНО: ВОНЯЕТ ЖУТКИМ ГОВНОКОДОМ!!!
        synchronized (mutex) {
            // новая игра
            if (firstPlayer == null && secondPlayer == null) {
                gs = new GameSettings(BoardFilling.STANDARD);
            }
            boolean newGame = false;
            Color response = null;

            // добавление 1 игрока
            if (firstPlayer == null) {
                firstPlayer = new RemotePlayer(gs, Color.WHITE, clientID);
                response = Color.WHITE;
                newGame = true;
            }
            // добавление 2 игрока
            else if (secondPlayer == null) {
                secondPlayer = new RemotePlayer(gs, Color.BLACK, clientID);
                response = Color.BLACK;
                newGame = true;
            }
            // новая игра
            if (newGame && firstPlayer != null && secondPlayer != null) {
                try {
                    game = new Selfplay(gs, firstPlayer, secondPlayer);
                } catch (ChessError chessError) {
                    // Стандартное заполнение доски верно всегда
                }
            }

            return response != null
                    ? response
                    : firstPlayer.getPlayerID() == clientID
                            ? firstPlayer.getColor()
                            : secondPlayer.getColor();
        }
    }

    // TODO: вынести
    public static void removePlayer(int clientID) {
        if (firstPlayer != null && firstPlayer.getPlayerID() == clientID) firstPlayer = null;
        else secondPlayer = null;
    }

    /** Выполняет игровое действие */
    public static String action(String json, int clientID) {
        if (firstPlayer == null || secondPlayer == null) return null;
        // игра, если подключены 2 игрока
        Move move;
        try {
            move = SerializationService.deserialize(json, Move.class);
        } catch (IOException e) {
            // TODO: некорректный запрос
            ServerController.getView()
                    .ifPresent(v -> v.print("Некорректный запрос: " + e.getMessage()));
            return null;
        }
        try {
            boolean complete = game.move(move);
            if (!complete) {
                // TODO: некорректный ход
                ServerController.getView().ifPresent(v -> v.print("Некорректный ход"));
            }
        } catch (ChessError chessError) {
            // TODO: критическая ошибка в игре
            try {
                ServerController.executeCommand("msg " + json);
            } catch (ServerException ignore) {
            }
        }
        // send move to second player
        int sendToClientID =
                game.getCurrentPlayerToMove() == firstPlayer
                        ? firstPlayer.getPlayerID()
                        : secondPlayer.getPlayerID();
        final String finalResponse = ClientRequestHandler.convertToServerToClientDTO(MOVE, json);
        try {
            ServerController.send(finalResponse, sendToClientID);
        } catch (ServerException ignore) {
        }
        ServerController.getView().ifPresent(v -> v.print("Отправлен json: " + finalResponse));
        return null;
    }
}
