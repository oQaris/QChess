package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EndGameDetectorTest {
    private Board board;
    private EndGameDetector endGameDetector;

    @Before
    public void setUp() throws ChessError {
        GameSettings gameSettings = new GameSettings(Board.BoardFilling.EMPTY);
        board = gameSettings.board;
        endGameDetector = gameSettings.endGameDetector;
    }

    @Test(expected = ChessError.class)
    public void testIsCheck_zeroFigures_1() throws ChessError {
        endGameDetector.isCheck(Color.WHITE);
    }

    @Test(expected = ChessError.class)
    public void testIsCheck_zeroFigures_2() throws ChessError {
        endGameDetector.isCheck(Color.BLACK);
    }

    @Test
    public void testIsCheck() throws ChessError, ChessException {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("e8")));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("e2")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("e7")));

        endGameDetector.isCheck(Color.WHITE);
        Assert.assertFalse(endGameDetector.isCheck(Color.WHITE));
        Assert.assertFalse(endGameDetector.isCheck(Color.BLACK));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("f2")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("f7")));

        Assert.assertTrue(endGameDetector.isCheck(Color.WHITE));
        Assert.assertTrue(endGameDetector.isCheck(Color.BLACK));
    }

    @Test
    public void testIsStalemate_black() throws ChessException, ChessError {
        board.setFigure(new Pawn(Color.BLACK, Cell.parse("b5")));
        board.setFigure(new Pawn(Color.BLACK, Cell.parse("c6")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        board.setFigure(new Pawn(Color.WHITE, Cell.parse("b4")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c5")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("h7")));
        board.setFigure(new King(Color.WHITE, Cell.parse("f6")));
        board.setFigure(new Bishop(Color.WHITE, Cell.parse("c2")));

        Assert.assertTrue(endGameDetector.isStalemate(Color.BLACK));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("g3")));

        Assert.assertFalse(endGameDetector.isStalemate(Color.BLACK));
    }

    @Test
    public void testIsStalemate_white() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h3")));
        board.setFigure(new Rook(Color.BLACK, Cell.parse("g7")));

        Assert.assertTrue(endGameDetector.isStalemate(Color.WHITE));
        Assert.assertFalse(endGameDetector.isCheckmate(Color.WHITE));

        board.setFigure(new Pawn(Color.WHITE, Cell.parse("g3")));

        Assert.assertFalse(endGameDetector.isStalemate(Color.WHITE));
    }

    @Test
    public void testIsCheckmate() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("f8")));
        board.setFigure(new King(Color.BLACK, Cell.parse("e6")));

        Assert.assertFalse(endGameDetector.isCheckmate(Color.WHITE));
        Assert.assertFalse(endGameDetector.isCheckmate(Color.BLACK));

        board.setFigure(new Queen(Color.BLACK, Cell.parse("f7")));

        Assert.assertTrue(endGameDetector.isCheckmate(Color.WHITE));

        board.setFigure(new Rook(Color.WHITE, Cell.parse("h7")));

        Assert.assertFalse(endGameDetector.isCheckmate(Color.WHITE));
    }
}
