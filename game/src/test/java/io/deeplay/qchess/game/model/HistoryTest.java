package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board.BoardFilling;
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

    @Test
    public void testAddRecord1() throws ChessException, ChessError {
        String expected = "rnbqkbnr/p1pppppp/8/1p6/P7/R7/1PPPPPPP/1NBQKBNR w Kkq";
        Move[] moveList = {
            new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4")),
            new Move(MoveType.LONG_MOVE, Cell.parse("b7"), Cell.parse("b5")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a1"), Cell.parse("a3")),
        };
        for (Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getLastRecord());
    }

    @Test
    public void testAddRecord2() throws ChessException, ChessError {
        String expected = "rnbqkb1r/1ppppnpp/8/5p2/1p6/P1N5/2PPPPPP/R1BQKBNR b KQkq";
        Move[] moveList = {
            new Move(MoveType.QUIET_MOVE, Cell.parse("b1"), Cell.parse("c3")),
            new Move(MoveType.LONG_MOVE, Cell.parse("f7"), Cell.parse("f5")),
            new Move(MoveType.LONG_MOVE, Cell.parse("b2"), Cell.parse("b4")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("g8"), Cell.parse("h6")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a2"), Cell.parse("a3")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("h6"), Cell.parse("f7")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("c3"), Cell.parse("a4")),
            new Move(MoveType.LONG_MOVE, Cell.parse("a7"), Cell.parse("a5")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a4"), Cell.parse("c3")),
            new Move(MoveType.ATTACK, Cell.parse("a5"), Cell.parse("b4"))
        };

        for (Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getLastRecord());
    }

    @Test
    public void testAddRecord3() throws ChessException, ChessError {
        String expected = "rnbqkbnr/p2pppp1/p6p/2p5/8/8/1PPPPPPP/RNBQKBNR b KQkq";
        Move[] moveList = {
            new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("h7"), Cell.parse("h6")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a4"), Cell.parse("a5")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("c7"), Cell.parse("c5")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a5"), Cell.parse("a6")),
            new Move(MoveType.ATTACK, Cell.parse("b7"), Cell.parse("a6"))
        };

        for (Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getLastRecord());
    }

    @Test
    public void testAddRecord4() throws ChessException, ChessError {
        String expected = "rnbqkbnr/2pp1ppp/pp6/4p1N1/8/5P2/PPPPP1PP/RNBQKB1R b KQkq e6";
        Move[] moveList = {
            new Move(MoveType.QUIET_MOVE, Cell.parse("f2"), Cell.parse("f3")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("b7"), Cell.parse("b6")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("g1"), Cell.parse("h3")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a7"), Cell.parse("a6")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("h3"), Cell.parse("g5")),
            new Move(MoveType.LONG_MOVE, Cell.parse("e7"), Cell.parse("e5"))
        };

        for (Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getLastRecord());
    }
}
