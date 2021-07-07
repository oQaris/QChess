package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;

public abstract class Player {

    protected MoveSystem ms;
    protected Board board;
    protected boolean color;

    public Player() {
    }

    public Player(MoveSystem ms, Board board, boolean color) {
        this.ms = ms;
        this.board = board;
        this.color = color;
    }

    public MoveSystem getMoveSystem() {
        return ms;
    }

    public void setMoveSystem(MoveSystem ms) {
        this.ms = ms;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isWhite() {
        return color;
    }

    public void setColor(boolean isWhite) {
        this.color = isWhite;
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
