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

    public GameSettings(final Board.BoardFilling boardType) {
        boardSize = Board.STD_BOARD_SIZE;
        this.boardType = boardType;
        fen = null;
        board = new Board(boardType);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    public GameSettings(final String fen) throws ChessError {
        boardSize = 0;
        boardType = null;
        this.fen = fen;
        board = new Board(fen);
        history = new History(this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    /** Копирует gs */
    public GameSettings(final GameSettings gs) {
        boardSize = gs.boardSize;
        boardType = gs.boardType;
        fen = gs.fen;
        board = new Board(gs.board);
        history = new History(gs.history, this);
        endGameDetector = new EndGameDetector(this);
        moveSystem = new MoveSystem(this);
    }

    /**
     * Используется для сброса игры на кастомную первоначальную расстановку после смены сторон
     * игроков
     */
    public GameSettings newWithTheSameSettings() throws ChessError {
        return boardType != null ? new GameSettings(boardType) : new GameSettings(fen);
    }
}
