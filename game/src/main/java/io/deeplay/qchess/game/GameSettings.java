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
    public final String fen;

    public GameSettings(Board.BoardFilling boardType) {
        this.boardSize = Board.STD_BOARD_SIZE;
        this.boardType = boardType;
        this.fen = null;
        this.board = new Board(boardType);
        this.history = new History(this);
        this.endGameDetector = new EndGameDetector(this);
        this.moveSystem = new MoveSystem(this);
    }

    public GameSettings(String fen) throws ChessError {
        this.boardSize = 0;
        this.boardType = null;
        this.fen = fen;
        this.board = new Board(fen);
        this.history = new History(this);
        this.endGameDetector = new EndGameDetector(this);
        this.moveSystem = new MoveSystem(this);
    }

    /** Копирует gs */
    public GameSettings(GameSettings gs) {
        this.boardSize = gs.boardSize;
        this.boardType = gs.boardType;
        this.fen = gs.fen;
        this.board = new Board(gs.board);
        this.history = new History(gs.history, this);
        this.endGameDetector = new EndGameDetector(this);
        this.moveSystem = new MoveSystem(this);
    }

    public GameSettings newWithTheSameSettings() throws ChessError {
        return boardType != null ? new GameSettings(boardType) : new GameSettings(fen);
    }
}
