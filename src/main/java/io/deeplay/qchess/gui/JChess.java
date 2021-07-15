package io.deeplay.qchess.gui;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;

public class JChess {
    public static void main(String[] args) throws ChessException, ChessError {
        String placement = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Board board = new Board(placement);
        Table table = new Table(board);
    }
}
