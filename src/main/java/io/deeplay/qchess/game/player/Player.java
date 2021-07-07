package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;

public abstract class Player {
    protected final Board board;
    protected final boolean color;

    public Player(Board board, boolean color) {
        this.board = board;
        this.color = color;
    }

    /**
     * @return возвращает проверенный ход
     */
    public abstract Move getNextMove();
}
