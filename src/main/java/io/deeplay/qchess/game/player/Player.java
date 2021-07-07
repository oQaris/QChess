package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;

public abstract class Player {

    protected final MoveSystem ms;
    protected final Board board;
    protected final boolean color;

    public Player(MoveSystem ms, Board board, boolean color) {
        this.ms = ms;
        this.board = board;
        this.color = color;
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
}
