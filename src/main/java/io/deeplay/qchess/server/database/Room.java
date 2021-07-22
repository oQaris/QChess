package io.deeplay.qchess.server.database;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;

public class Room {
    private final BoardFilling bf;
    private RemotePlayer player1;
    private RemotePlayer player2;
    private Selfplay game;
    private GameSettings gs;
    private boolean error;

    public Room(BoardFilling bf) {
        this.bf = bf;
        gs = new GameSettings(bf);
    }

    public synchronized boolean isStarted() {
        return game != null;
    }

    /** Изменяет флаг error = true, если при создании игры возникла критическая ошибка */
    public synchronized void startGame() {
        try {
            game = new Selfplay(gs, player1, player2);
        } catch (ChessError chessError) {
            error = true;
        }
    }

    /** @return true, если возникли критические ошибки при игре */
    public synchronized boolean isError() {
        return error;
    }

    public synchronized void addPlayer(String sessionToken) {
        if (player1 == null || player1.getSessionToken() == null)
            player1 = new RemotePlayer(gs, Color.WHITE, sessionToken);
        else if (player2 == null || player2.getSessionToken() == null)
            player2 = new RemotePlayer(gs, Color.BLACK, sessionToken);
    }

    /** @return true, если комната заполнена */
    public synchronized boolean isFull() {
        return player1 != null && player2 != null;
    }

    /**
     * Изменяет флаг error = true, если при ходе возникла критическая ошибка
     *
     * @return true, если ход корректный, иначе false
     */
    public synchronized boolean move(Move move) {
        try {
            return game.move(move);
        } catch (ChessError chessError) {
            error = true;
            return false;
        }
    }

    /** @return true, если игрок был удален, иначе false */
    public synchronized boolean removePlayer(String sessionToken) {
        boolean removed = false;
        if (player1 != null && player1.getSessionToken().equals(sessionToken)) {
            player1 = null;
            removed = true;
        }
        if (player2 != null && player2.getSessionToken().equals(sessionToken)) {
            player2 = null;
            removed = true;
        }
        if (removed) {
            gs = new GameSettings(bf);
            game = null;
        }
        return removed;
    }

    /**
     * @return токен сессии клиента противника для клиента с sessionToken. Вернет null, если его нет
     */
    public synchronized String getOpponentSessionToken(String sessionToken) {
        if (player1 != null && player1.getSessionToken().equals(sessionToken)) {
            if (player2 != null) return player2.getSessionToken();
            else return null;
        } else if (player2 != null && player2.getSessionToken().equals(sessionToken)) {
            if (player1 != null) return player1.getSessionToken();
            else return null;
        }
        return null;
    }

    /** @return цвет игрока или null, если игрок не найден */
    public synchronized Color getColor(String sessionToken) {
        if (player1 != null && player1.getSessionToken().equals(sessionToken))
            return player1.getColor();
        if (player2 != null && player2.getSessionToken().equals(sessionToken))
            return player2.getColor();
        return null;
    }
}
