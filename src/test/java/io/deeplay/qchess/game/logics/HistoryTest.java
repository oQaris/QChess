package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HistoryTest {

    @Test
    public void testGetConvertingFigurePosition() throws ChessError, ChessException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Board board = new Board();
        MoveSystem ms = new MoveSystem(board);
        board.initBoard(ms, Board.BoardFilling.STANDARD);
        History history = new History(board);
        Method method = history.getClass().getDeclaredMethod("getConvertingFigurePosition");
        method.setAccessible(true);
        Assert.assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
                method.invoke(history));
    }

    @Test
    public void testGetCastlingPossibility() throws ChessError, ChessException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Board board = new Board();
        MoveSystem ms = new MoveSystem(board);
        board.initBoard(ms, Board.BoardFilling.STANDARD);
        History history = new History(board);
        Method method = history.getClass().getDeclaredMethod("getCastlingPossibility");
        method.setAccessible(true);
        Assert.assertEquals("KQkq",
                method.invoke(history));
    }

    @Test
    public void testGetPawnEnPassantPossibilityWithoutLongMove() throws ChessError, ChessException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Board board = new Board();
        MoveSystem ms = new MoveSystem(board);
        board.initBoard(ms, Board.BoardFilling.STANDARD);
        History history = new History(board);

        Move move = new Move(MoveType.SIMPLE_STEP, Cell.parse("a2"), Cell.parse("a3"));
        board.moveFigure(move);
        history.setPrevMove(move);

        Method method = history.getClass().getDeclaredMethod("getPawnEnPassantPossibility");
        method.setAccessible(true);
        Assert.assertEquals("",
                method.invoke(history));
    }

    @Test
    public void testGetPawnEnPassantPossibilityWithLongMove() throws ChessError, ChessException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Board board = new Board();
        MoveSystem ms = new MoveSystem(board);
        board.initBoard(ms, Board.BoardFilling.STANDARD);
        History history = new History(board);

        Move move = new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4"));
        board.moveFigure(move);
        history.setPrevMove(move);

        Method method = history.getClass().getDeclaredMethod("getPawnEnPassantPossibility");
        method.setAccessible(true);
        Assert.assertEquals(" a3",
                method.invoke(history));
    }
}
