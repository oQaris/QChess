package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;

public abstract class Player {
<<<<<<< HEAD
    protected MoveSystem ms;
=======
>>>>>>> 5172b3e (Слияние с мега фичей)
    protected Board board;
    protected Color color;
    MoveSystem ms;

    protected Player(GameSettings roomSettings, Color color) {
        ms = roomSettings.moveSystem;
        board = roomSettings.board;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    /** @return возвращает проверенный ход */
    public abstract Move getNextMove() throws ChessError;

    @Override
    public String toString() {
        return color + " PLAYER";
    }
}
