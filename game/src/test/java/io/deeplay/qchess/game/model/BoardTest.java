package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Rook;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {
    private Board board;
    private GameSettings gameSettings;

    @Before
    public void setUp() {
        gameSettings = new GameSettings(Board.BoardFilling.EMPTY);
        board = gameSettings.board;
    }

    @Test
    public void testFindKingCell() throws ChessException {
        Figure whiteKing = new King(Color.WHITE, Cell.parse("c1"));
        Figure whitePawn = new Pawn(Color.WHITE, Cell.parse("a1"));
        Figure blackPawn = new Pawn(Color.BLACK, Cell.parse("e7"));
        board.setFigure(whiteKing);
        board.setFigure(whitePawn);
        board.setFigure(blackPawn);
        Assert.assertEquals(Cell.parse("c1"), board.findKing(Color.WHITE).getCurrentPosition());
    }

    @Test
    public void testSetGetFigures() throws ChessException {
        // нет фигур
        Assert.assertEquals(List.of(), board.getFigures(Color.WHITE));
        Assert.assertEquals(List.of(), board.getFigures(Color.BLACK));

        Board testBoard = new Board(Board.BoardFilling.STANDARD);

        List<Figure> black = new ArrayList<>();
        List<Figure> white = new ArrayList<>();

        for (Character first : "abcdefgh".toCharArray()) {
            black.add(testBoard.getFigure(Cell.parse(first + "7")));
            black.add(testBoard.getFigure(Cell.parse(first + "8")));
            white.add(testBoard.getFigure(Cell.parse(first + "1")));
            white.add(testBoard.getFigure(Cell.parse(first + "2")));
        }

        Comparator<Figure> figureComparator =
                (o1, o2) -> {
                    int x1 = o1.getCurrentPosition().column;
                    int y1 = o1.getCurrentPosition().row;
                    int x2 = o2.getCurrentPosition().column;
                    int y2 = o2.getCurrentPosition().row;
                    return x1 != x2 ? x1 - x2 : y1 - y2;
                };

        black.sort(figureComparator);
        white.sort(figureComparator);

        List<Figure> ansBlack = testBoard.getFigures(Color.BLACK);
        List<Figure> ansWhite = testBoard.getFigures(Color.WHITE);

        ansBlack.sort(figureComparator);
        ansWhite.sort(figureComparator);

        Assert.assertEquals(black, ansBlack);
        Assert.assertEquals(white, ansWhite);
    }

    @Test
    public void testRemoveFigure() throws ChessException {
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("a2")));
        board.removeFigure(Cell.parse("a2"));
        Assert.assertNull(board.getFigure(Cell.parse("a2")));
    }

    @Test
    public void testIsEmptyCell() throws ChessException {
        Assert.assertTrue(board.isEmptyCell(Cell.parse("a1")));
    }

    @Test
    public void testNotEmptyCell() throws ChessException {
        board.setFigure(new Rook(Color.WHITE, Cell.parse("a1")));
        Assert.assertFalse(board.isEmptyCell(Cell.parse("a1")));
    }

    @Test
    public void testIsCorrectCell() {
        Board board = new Board(BoardFilling.STANDARD);
        Assert.assertTrue(board.isCorrectCell(0, 0));
        Assert.assertFalse(board.isCorrectCell(0, -1));
        Assert.assertTrue(board.isCorrectCell(7, 7));
        Assert.assertFalse(board.isCorrectCell(100, 100));
    }

    @Test
    public void testMoveFigureEmpty() throws ChessException {
        Figure rook = new Rook(Color.WHITE, Cell.parse("a1"));
        board.setFigure(rook);
        Assert.assertNull(
                board.moveFigure(
                        new Move(MoveType.QUIET_MOVE, Cell.parse("a1"), Cell.parse("a5"))));
        Assert.assertNull(board.getFigure(Cell.parse("a1")));
        Assert.assertNotNull(board.getFigure(Cell.parse("a5")));
        Assert.assertEquals(rook, board.getFigure(Cell.parse("a5")));
    }

    @Test
    public void testMoveFigureAttack() throws ChessException {
        board.setFigure(new Rook(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new Rook(Color.BLACK, Cell.parse("a5")));
        Assert.assertNotNull(
                board.moveFigure(
                        new Move(MoveType.QUIET_MOVE, Cell.parse("a1"), Cell.parse("a5"))));
    }

    @Test
    public void testBoardStringConstructor1() throws ChessError {
        String placement = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        String expected = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq";
        gameSettings = new GameSettings(placement);

        History history = new History(gameSettings);
        history.addRecord(null);
        Assert.assertEquals(expected, history.getBoardToStringForsythEdwards());
    }

    @Test
    public void testBoardStringConstructor2() throws ChessError, ChessException {
        String placement = "4k3/ppp2ppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        String expected = "4k3/ppp2ppp/8/8/P7/8/1PPPPPPP/RNBQKBNR b KQ a3";
        gameSettings = new GameSettings(placement);

        History history = new History(gameSettings);

        Move move = new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4"));
        gameSettings.board.moveFigure(move);

        history.addRecord(move);
        Assert.assertEquals(expected, history.getBoardToStringForsythEdwards());
    }
}
