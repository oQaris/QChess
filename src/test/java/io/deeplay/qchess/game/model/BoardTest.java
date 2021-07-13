package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() throws ChessError {
        board = new Board(Board.BoardFilling.EMPTY);
    }

    @Test
    public void testFindKingCell() throws ChessException, ChessError {
        Figure whiteKing = new King(Color.WHITE, Cell.parse("c1"));
        Figure whitePawn = new Pawn(Color.WHITE, Cell.parse("a1"));
        Figure blackPawn = new Pawn(Color.BLACK, Cell.parse("e7"));
        board.setFigure(whiteKing);
        board.setFigure(whitePawn);
        board.setFigure(blackPawn);
        Assert.assertEquals(Cell.parse("c1"), board.findKingCell(Color.WHITE));
    }

    @Test
    public void testSetGetFigures() throws ChessException, ChessError {
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
                    int x1 = o1.getCurrentPosition().getColumn();
                    int y1 = o1.getCurrentPosition().getRow();
                    int x2 = o2.getCurrentPosition().getColumn();
                    int y2 = o2.getCurrentPosition().getRow();
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
    public void testIsEmptyCell() {
        Assert.assertTrue(board.isEmptyCell(Cell.parse("a1")));
    }

    @Test
    public void testNotEmptyCell() throws ChessException {
        board.setFigure(new Rook(Color.WHITE, Cell.parse("a1")));
        Assert.assertFalse(board.isEmptyCell(Cell.parse("a1")));
    }

    @Test
    public void testIsCorrectCell() {
        Assert.assertTrue(Board.isCorrectCell(0, 0));
        Assert.assertFalse(Board.isCorrectCell(0, -1));
        Assert.assertTrue(Board.isCorrectCell(7, 7));
        Assert.assertFalse(Board.isCorrectCell(100, 100));
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
}
