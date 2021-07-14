package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import org.junit.Assert;
import org.junit.Test;

public class NotationServiceTest {
    @Test
    public void testGetFigureByChar1() throws ChessException {
        Figure blackKing = new King(Color.BLACK, new Cell(4, 7));
        Assert.assertEquals(NotationService.getFigureByChar('k', 4, 7), blackKing);
    }

    @Test
    public void testGetFigureByChar2() throws ChessException {
        Figure blackKing = new King(Color.WHITE, Cell.parse("e1"));
        Assert.assertEquals(NotationService.getFigureByChar('K', 4, 7), blackKing);
    }

    @Test(expected = ChessException.class)
    public void testGetFigureByChar3() throws ChessException {
        NotationService.getFigureByChar('A', 4, 7);
        Assert.fail();
    }

    @Test
    public void testCheckValidityPlacement1() {
        String str = "8/7k/8/8/8/8/K7/8";
        Assert.assertTrue(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement2() {
        String str = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertTrue(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement3() {
        String str = "rnbqkbnr/pppppppp/4p3/8/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement4() {
        String str = "rnbqkbar/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement5() {
        String str = "rnbqkbnr/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement6() {
        String str = "rnbqkbnr/pppppppp/8/7/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement7() {
        String str = "rnbqkpnr/pppppppp/8/7/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement8() {
        String str = "rnbq1bnr/pppppppp/8/7/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement9() {
        String str = "rnbqkbnr/pppppppp/8/7/8/8/PPPPPPPP/RNBQKBNR/8";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }

    @Test
    public void testCheckValidityPlacement10() {
        String str = "rnbqkbqr/pppppppp/8/7/8/8/PPPPPPPP/RNBQKBNR";
        Assert.assertFalse(NotationService.checkValidityPlacement(str));
    }
}
