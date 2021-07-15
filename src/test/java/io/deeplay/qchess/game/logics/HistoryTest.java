package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.History;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HistoryTest {
    private History history;
    private Board board;

    @Before
    public void setUp() {
        GameSettings gameSettings = new GameSettings(BoardFilling.STANDARD);
        history = new History(gameSettings);
        board = gameSettings.board;
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

        Field prevMove = history.getClass().getDeclaredField("lastMove");
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

        Field prevMove = history.getClass().getDeclaredField("lastMove");
        prevMove.setAccessible(true);
        prevMove.set(history, move);
        Method method = history.getClass().getDeclaredMethod("getPawnEnPassantPossibility");
        method.setAccessible(true);

        Assert.assertEquals(" a3", method.invoke(history));
    }

    @Test
    public void testAddRecord() throws ChessException, ChessError {
        Assert.assertEquals(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq", history.addRecord(null));
    }
}
