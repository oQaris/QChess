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
    public final int boardSize;
    public final Board.BoardFilling boardType;
    public final String boardFillingForsythEdwards;

    public GameSettings(int boardSize, Board.BoardFilling boardType) {
        this.boardSize = boardSize;
        this.boardType = boardType;
        this.boardFillingForsythEdwards = null;
        board = new Board(boardSize, boardType);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    public GameSettings(Board.BoardFilling boardType) {
        this.boardSize = Board.STD_BOARD_SIZE;
        this.boardType = boardType;
        this.boardFillingForsythEdwards = null;
        board = new Board(boardType);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    public GameSettings(String boardFillingForsythEdwards) throws ChessError {
        this.boardSize = 0;
        this.boardType = null;
        this.boardFillingForsythEdwards = boardFillingForsythEdwards;
        board = new Board(boardFillingForsythEdwards);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    /**
     * Копирует gs
     */
    public GameSettings(GameSettings gs) {
        this.board = new Board(gs.board);
        this.moveSystem = new MoveSystem(this);
        this.endGameDetector = new EndGameDetector(this);
        this.history = new History(gs.history, this);
        this.boardSize = gs.boardSize;
        this.boardType = gs.boardType;
        this.boardFillingForsythEdwards = gs.boardFillingForsythEdwards;
    }

    public GameSettings newWithTheSameSettings() throws ChessError {
        return boardType != null
                ? new GameSettings(boardSize, boardType)
                : new GameSettings(boardFillingForsythEdwards);
    }
}
