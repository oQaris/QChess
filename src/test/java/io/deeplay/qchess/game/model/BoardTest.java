package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.King;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.logics.MoveSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {

    private Board board;
    private MoveSystem ms;

    @Before
    public void setUp() {
        board = new Board();
        ms = new MoveSystem(board);
    }

    @Test
    public void testFindKingCell() throws ChessException, ChessError {
        Figure whiteKing = new King(ms, board, true, Cell.parse("c1"));
        Figure whitePawn = new Pawn(ms, board, true, Cell.parse("a1"));
        Figure blackPawn = new Pawn(ms, board, false, Cell.parse("e7"));
        board.setFigure(whiteKing);
        board.setFigure(whitePawn);
        board.setFigure(blackPawn);
        Assert.assertEquals(Cell.parse("c1"), board.findKingCell(true));
    }

    @Test
    public void testSetGetFigures() throws ChessException, ChessError {
        // нет фигур
        Assert.assertEquals(List.of(), board.getFigures(true));
        Assert.assertEquals(List.of(), board.getFigures(false));

        board.initBoard(ms, Board.BoardFilling.STANDARD);

        List<Figure> black = new ArrayList<>();
        List<Figure> white = new ArrayList<>();

        for (Character first : "abcdefgh".toCharArray()) {
            black.add(board.getFigure(Cell.parse(first.toString() + "7")));
            black.add(board.getFigure(Cell.parse(first.toString() + "8")));
            white.add(board.getFigure(Cell.parse(first.toString() + "1")));
            white.add(board.getFigure(Cell.parse(first.toString() + "2")));
        }

        Comparator<Figure> figureComparator = (o1, o2) -> {
            int x1 = o1.getCurrentPosition().getCol();
            int y1 = o1.getCurrentPosition().getRow();
            int x2 = o2.getCurrentPosition().getCol();
            int y2 = o2.getCurrentPosition().getRow();
            return x1 != x2 ? x1 - x2 : y1 - y2;
        };

        black.sort(figureComparator);
        white.sort(figureComparator);

        List<Figure> ansBlack = board.getFigures(false);
        List<Figure> ansWhite = board.getFigures(true);

        ansBlack.sort(figureComparator);
        ansWhite.sort(figureComparator);

        Assert.assertEquals(black, ansBlack);
        Assert.assertEquals(white, ansWhite);
    }

    @Test
    public void testRemoveFigure() {
    }

    @Test
    public void testIsEmptyCell() {
    }

    @Test
    public void testIsNotMakeMoves() {
    }

    @Test
    public void testIsCorrectCell() {
    }

    @Test
    public void testTestIsCorrectCell() {
    }

    @Test
    public void testMoveFigure() {
    }
}
