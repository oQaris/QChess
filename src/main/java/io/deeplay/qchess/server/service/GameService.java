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
import io.deeplay.qchess.server.handlers.ClientRequestHandler;
import java.io.IOException;

/** Управляет играми */
public class GameService {
    // TODO: вынести все в БД
    private static Selfplay game;
    private static GameSettings gs;
    private static RemotePlayer firstPlayer;
    private static RemotePlayer secondPlayer;

    public static Color getPlayerColor(int clientID) {
        // TODO: ОСТОРОЖНО: ВОНЯЕТ ЖУТКИМ ГОВНОКОДОМ!!!

        // добавление 1 игрока
        if (firstPlayer == null) {
            gs = new GameSettings(BoardFilling.STANDARD);
            firstPlayer = new RemotePlayer(gs, Color.WHITE, clientID);
            return Color.WHITE;
        }
        // добавление 2 игрока
        else if (secondPlayer == null) {
            secondPlayer = new RemotePlayer(gs, Color.BLACK, clientID);
            try {
                game = new Selfplay(gs, firstPlayer, secondPlayer);
            } catch (ChessError chessError) {
                // Стандартное заполнение доски верно всегда
            }
            return Color.BLACK;
        }

        return firstPlayer.getPlayerID() == clientID
                ? firstPlayer.getColor()
                : secondPlayer.getColor();
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
            ServerController.getView().ifPresent(v -> v.print("Некорректный запрос"));
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
            ServerController.executeCommand("msg " + json);
        }
        // send move to second player
        int sendToClientID =
                game.getCurrentPlayerToMove() == firstPlayer
                        ? firstPlayer.getPlayerID()
                        : secondPlayer.getPlayerID();
        final String finalResponse = ClientRequestHandler.convertToServerToClientDTO(MOVE, json);
        ServerController.send(finalResponse, sendToClientID);
        ServerController.getView().ifPresent(v -> v.print("Отправлен json: " + finalResponse));
        return null;
    }
}
