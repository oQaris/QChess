package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.figures.King;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.Rook;
import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

    @Test
    public void testFindKingCell() throws ChessException {
        Board board = new Board();
        Figure whiteKing = new King(board, true, Cell.parse("c1"));
        Figure whitePawn = new Pawn(board, true, Cell.parse("a1"));
        Figure blackPawn = new Pawn(board, false, Cell.parse("e7"));
        board.setFigure(whiteKing);
        board.setFigure(whitePawn);
        board.setFigure(blackPawn);
        Assert.assertEquals(Cell.parse("c1"), board.findKingCell(true));
    }

    @Test
    public void testGetFigures() {
    }

    @Test
    public void testGetFigure() {
    }

    @Test
    public void testSetFigure() {
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