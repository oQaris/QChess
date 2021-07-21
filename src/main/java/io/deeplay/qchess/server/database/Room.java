package io.deeplay.qchess.server.database;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.player.RemotePlayer;

public class Room {
    private int roomID;
    private RemotePlayer player1;
    private RemotePlayer player2;
    private Selfplay game;
    private GameSettings gs;

    public Room(int roomID, RemotePlayer player1, RemotePlayer player2, GameSettings gs) {
        this.roomID = roomID;
        this.player1 = player1;
        this.player2 = player2;
        this.gs = gs;
    }

    /** @throws ChessError если при создании игры возникла ошибка */
    public void startGame() throws ChessError {
        game = new Selfplay(gs, player1, player2);
    }
}
