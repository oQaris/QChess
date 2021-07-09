package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;

public class GameSettings {

    public final Board board;
    public final MoveSystem moveSystem;

    public GameSettings(Board.BoardFilling boardType) throws ChessError {
        board = new Board(boardType);
        moveSystem = new MoveSystem(board);
    }
}