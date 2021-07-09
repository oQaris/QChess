package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;

public abstract class Player {

    protected MoveSystem ms;
    protected Board board;
    protected boolean color;

    public Player(GameSettings roomSettings, boolean color) {
        ms = roomSettings.moveSystem;
        board = roomSettings.board;
        this.color = color;
    }

    public Board getBoard() {
        return board;
    }

    public MoveSystem getMs() {
        return ms;
    }

    public boolean isWhite() {
        return color;
    }

    /**
     * @return возвращает проверенный ход
     */
    public abstract Move getNextMove() throws ChessError;

    /**
     * @return true - белый, false - черный
     */
    public boolean getColor() {
        return color;
    }

    @Override
    public String toString() {
        return (color ? "White player" : "Black player");
    }
}
