package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.History;

public class GameSettings {
    public final Board board;
    public final MoveSystem moveSystem;
    public final EndGameDetector endGameDetector;
    public final History history;

    public GameSettings(int boardSize, Board.BoardFilling boardType) {
        board = new Board(boardSize, boardType);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    public GameSettings(Board.BoardFilling boardType) {
        board = new Board(boardType);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    public GameSettings(String boardFillingForsythEdwards) throws ChessError {
        board = new Board(boardFillingForsythEdwards);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }
}
