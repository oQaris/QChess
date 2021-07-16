package io.deeplay.qchess.gui;

import io.deeplay.qchess.client.IClientController;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;

public class JChess {
    public static void main(String[] args) throws ChessException, ChessError {
        String placement = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Board board = new Board(placement);
        IClientController cc = new GuiController(board);

        Table tableWhite = new Table("onestyle", true, cc);
        Table tableBlack = new Table("onestyle", false, cc);
    }
}
