package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HistoryTest {
    private Board board;
    private History history;

    @Before
    public void setUp() throws ChessError {
        board = new Board(Board.BoardFilling.STANDARD);
        history = new History(board);
    }

    @Test
    public void testGetConvertingFigurePosition()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = history.getClass().getDeclaredMethod("getConvertingFigurePosition");
        method.setAccessible(true);

        Assert.assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", method.invoke(history));
    }

    @Test
    public void testGetCastlingPossibility()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = history.getClass().getDeclaredMethod("getCastlingPossibility");
        method.setAccessible(true);

        Assert.assertEquals("KQkq", method.invoke(history));
    }

    @Test
    public void testGetPawnEnPassantPossibilityWithoutLongMove()
            throws ChessException, NoSuchMethodException, InvocationTargetException,
                    IllegalAccessException, NoSuchFieldException {
        Move move = new Move(MoveType.QUIET_MOVE, Cell.parse("a2"), Cell.parse("a3"));
        board.moveFigure(move);

        Field prevMove = history.getClass().getDeclaredField("prevMove");
        prevMove.setAccessible(true);
        prevMove.set(history, move);
        Method method = history.getClass().getDeclaredMethod("getPawnEnPassantPossibility");
        method.setAccessible(true);

        Assert.assertEquals("", method.invoke(history));
    }

    @Test
    public void testGetPawnEnPassantPossibilityWithLongMove()
            throws ChessException, NoSuchMethodException, InvocationTargetException,
                    IllegalAccessException, NoSuchFieldException {
        Move move = new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4"));
        board.moveFigure(move);

        Field prevMove = history.getClass().getDeclaredField("prevMove");
        prevMove.setAccessible(true);
        prevMove.set(history, move);
        Method method = history.getClass().getDeclaredMethod("getPawnEnPassantPossibility");
        method.setAccessible(true);

        Assert.assertEquals(" a3", method.invoke(history));
    }
}
