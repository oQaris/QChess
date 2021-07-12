package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;

public class GameSettings {
    public final Board board;
    public final MoveSystem moveSystem;
    public final EndGameDetector endGameDetector;

    public GameSettings(Board.BoardFilling boardType) throws ChessError {
        board = new Board(boardType);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(board, endGameDetector);
    }
}
