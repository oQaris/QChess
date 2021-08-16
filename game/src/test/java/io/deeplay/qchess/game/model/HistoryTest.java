package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.player.RemotePlayer;
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
        final GameSettings gameSettings = new GameSettings(BoardFilling.STANDARD);
        history = new History(gameSettings);
        board = gameSettings.board;
    }

    @Test
    public void testGetConvertingFigurePosition()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = history.getClass().getDeclaredMethod("getConvertingFigurePosition");
        method.setAccessible(true);

        Assert.assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", method.invoke(history));
    }

    @Test
    public void testGetCastlingPossibility()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = history.getClass().getDeclaredMethod("getCastlingPossibility");
        method.setAccessible(true);

        Assert.assertEquals("KQkq", method.invoke(history));
    }

    @Test
    public void testGetPawnEnPassantPossibilityWithoutLongMove()
            throws ChessException, NoSuchMethodException, InvocationTargetException,
                    IllegalAccessException, NoSuchFieldException {
        final Move move = new Move(MoveType.QUIET_MOVE, Cell.parse("a2"), Cell.parse("a3"));
        board.moveFigure(move);

        final Field prevMove = history.getClass().getDeclaredField("lastMove");
        prevMove.setAccessible(true);
        prevMove.set(history, move);
        final Method method = history.getClass().getDeclaredMethod("getPawnEnPassantPossibility");
        method.setAccessible(true);

        Assert.assertEquals("", method.invoke(history));
    }

    @Test
    public void testGetPawnEnPassantPossibilityWithLongMove()
            throws ChessException, NoSuchMethodException, InvocationTargetException,
                    IllegalAccessException, NoSuchFieldException {
        final Move move = new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4"));
        board.moveFigure(move);

        final Field prevMove = history.getClass().getDeclaredField("lastMove");
        prevMove.setAccessible(true);
        prevMove.set(history, move);
        final Method method = history.getClass().getDeclaredMethod("getPawnEnPassantPossibility");
        method.setAccessible(true);

        Assert.assertEquals(" a3", method.invoke(history));
    }

    @Test
    public void testAddRecord() throws ChessError {
        Assert.assertEquals(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq",
                history.getBoardToStringForsythEdwards());
    }

    @Test
    public void testAddRecord1() throws ChessException, ChessError {
        final String expected = "rnbqkbnr/p1pppppp/8/1p6/P7/R7/1PPPPPPP/1NBQKBNR b Kkq";
        final Move[] moveList = {
            new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4")),
            new Move(MoveType.LONG_MOVE, Cell.parse("b7"), Cell.parse("b5")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a1"), Cell.parse("a3")),
        };
        for (final Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getBoardToStringForsythEdwards());
    }

    @Test
    public void testAddRecord2() throws ChessException, ChessError {
        final String expected = "rnbqkb1r/1ppppnpp/8/5p2/1p6/P1N5/2PPPPPP/R1BQKBNR w KQkq";
        final Move[] moveList = {
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

        for (final Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getBoardToStringForsythEdwards());
    }

    @Test
    public void testAddRecord3() throws ChessException, ChessError {
        final String expected = "rnbqkbnr/p2pppp1/p6p/2p5/8/8/1PPPPPPP/RNBQKBNR w KQkq";
        final Move[] moveList = {
            new Move(MoveType.LONG_MOVE, Cell.parse("a2"), Cell.parse("a4")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("h7"), Cell.parse("h6")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a4"), Cell.parse("a5")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("c7"), Cell.parse("c5")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a5"), Cell.parse("a6")),
            new Move(MoveType.ATTACK, Cell.parse("b7"), Cell.parse("a6"))
        };

        for (final Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getBoardToStringForsythEdwards());
    }

    @Test
    public void testAddRecord4() throws ChessException, ChessError {
        final String expected = "rnbqkbnr/2pp1ppp/pp6/4p1N1/8/5P2/PPPPP1PP/RNBQKB1R w KQkq e6";
        final Move[] moveList = {
            new Move(MoveType.QUIET_MOVE, Cell.parse("f2"), Cell.parse("f3")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("b7"), Cell.parse("b6")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("g1"), Cell.parse("h3")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("a7"), Cell.parse("a6")),
            new Move(MoveType.QUIET_MOVE, Cell.parse("h3"), Cell.parse("g5")),
            new Move(MoveType.LONG_MOVE, Cell.parse("e7"), Cell.parse("e5"))
        };

        for (final Move move : moveList) {
            board.moveFigure(move);
            history.addRecord(move);
        }

        Assert.assertEquals(expected, history.getBoardToStringForsythEdwards());
    }

    @Test
    public void test() throws ChessError {
        final GameSettings gs = new GameSettings(BoardFilling.STANDARD);
        final Selfplay game =
                new Selfplay(
                        gs,
                        new RemotePlayer(gs, Color.WHITE, "", ""),
                        new RemotePlayer(gs, Color.BLACK, "", ""));
        int i = 0;
        final String[] strs = {
            "a2",
            "a4",
            "LONG_MOVE",
            "d7",
            "d5",
            "LONG_MOVE",
            "a4",
            "a5",
            "QUIET_MOVE",
            "b8",
            "a6",
            "QUIET_MOVE",
            "b2",
            "b4",
            "LONG_MOVE",
            "c8",
            "e6",
            "QUIET_MOVE",
            "b4",
            "b5",
            "QUIET_MOVE",
            "f7",
            "f6",
            "QUIET_MOVE",
            "b5",
            "a6",
            "ATTACK",
            "e6",
            "f5",
            "QUIET_MOVE",
            "a6",
            "b7",
            "ATTACK",
            "a7",
            "a6",
            "QUIET_MOVE",
            "e2",
            "e3",
            "QUIET_MOVE",
            "f5",
            "c8",
            "QUIET_MOVE",
            "d1",
            "h5",
            "QUIET_MOVE",
            "g7",
            "g6",
            "QUIET_MOVE",
            "h5",
            "g6",
            "ATTACK",
            "h7",
            "g6",
            "ATTACK",
            "c2",
            "c4",
            "LONG_MOVE",
            "c8",
            "g4",
            "QUIET_MOVE",
            "c4",
            "c5",
            "QUIET_MOVE",
            "g8",
            "h6",
            "QUIET_MOVE",
            "f2",
            "f3",
            "QUIET_MOVE",
            "a8",
            "a7",
            "QUIET_MOVE",
            "b1",
            "c3",
            "QUIET_MOVE",
            "h8",
            "g8",
            "QUIET_MOVE",
            "c5",
            "c6",
            "QUIET_MOVE",
            "a7",
            "a8",
            "QUIET_MOVE",
            "c3",
            "a2",
            "QUIET_MOVE",
            "a8",
            "a7",
            "QUIET_MOVE",
            "a2",
            "b4",
            "QUIET_MOVE",
            "g4",
            "h5",
            "QUIET_MOVE",
            "b4",
            "a6",
            "ATTACK",
            "h5",
            "f3",
            "ATTACK",
            "g2",
            "f3",
            "ATTACK",
            "a7",
            "a8",
            "QUIET_MOVE",
            "c1",
            "a3",
            "QUIET_MOVE",
            "e7",
            "e5",
            "LONG_MOVE",
            "a3",
            "f8",
            "ATTACK",
            "d5",
            "d4",
            "QUIET_MOVE",
            "f8",
            "h6",
            "ATTACK",
            "a8",
            "a7",
            "QUIET_MOVE",
            "e3",
            "d4",
            "ATTACK",
            "a7",
            "a8",
            "QUIET_MOVE",
            "a6",
            "c7",
            "ATTACK",
            "e8",
            "f7",
            "QUIET_MOVE",
            "f1",
            "c4",
            "QUIET_MOVE",
            "d8",
            "d5",
            "QUIET_MOVE",
            "c4",
            "d5",
            "ATTACK",
            "f7",
            "e7",
            "QUIET_MOVE",
            "c7",
            "a8",
            "ATTACK",
            "g8",
            "a8",
            "ATTACK",
            "d4",
            "e5",
            "ATTACK",
            "a8",
            "c8",
            "QUIET_MOVE",
            "h6",
            "g5",
            "QUIET_MOVE",
            "c8",
            "c7",
            "QUIET_MOVE",
            "e5",
            "f6",
            "ATTACK",
            "e7",
            "f8",
            "QUIET_MOVE",
            "a5",
            "a6",
            "QUIET_MOVE",
            "c7",
            "f7",
            "QUIET_MOVE",
            "a6",
            "a7",
            "QUIET_MOVE",
            "f8",
            "g8",
            "QUIET_MOVE",
            "c6",
            "c7",
            "QUIET_MOVE",
            "g8",
            "f8",
            "QUIET_MOVE",
            "d5",
            "f7",
            "ATTACK",
            "f8",
            "f7",
            "ATTACK",
            "g5",
            "h4",
            "QUIET_MOVE",
            "f7",
            "e6",
            "QUIET_MOVE",
            "h4",
            "g5",
            "QUIET_MOVE",
            "e6",
            "d7",
            "QUIET_MOVE",
            "a1",
            "c1",
            "QUIET_MOVE",
            "d7",
            "d6",
            "QUIET_MOVE",
            "g1",
            "h3",
            "QUIET_MOVE",
            "d6",
            "e5",
            "QUIET_MOVE",
            "h3",
            "f4",
            "QUIET_MOVE",
            "e5",
            "d4",
            "QUIET_MOVE",
            "f6",
            "f7",
            "QUIET_MOVE",
            "d4",
            "e5",
            "QUIET_MOVE",
            "h2",
            "h4",
            "LONG_MOVE",
            "e5",
            "d4",
            "QUIET_MOVE",
            "g5",
            "d8",
            "QUIET_MOVE",
            "d4",
            "e5",
            "QUIET_MOVE",
            "c1",
            "c5",
            "QUIET_MOVE",
            "e5",
            "d6",
            "QUIET_MOVE",
            "c5",
            "b5",
            "QUIET_MOVE",
            "d6",
            "c6",
            "QUIET_MOVE",
            "b5",
            "b1",
            "QUIET_MOVE",
            "g6",
            "g5",
            "QUIET_MOVE",
            "h4",
            "g5",
            "ATTACK",
            "c6",
            "d7",
            "QUIET_MOVE",
            "b1",
            "b4",
            "QUIET_MOVE",
            "d7",
            "d6",
            "QUIET_MOVE",
            "b4",
            "c4",
            "QUIET_MOVE",
            "d6",
            "d7",
            "QUIET_MOVE",
            "d8",
            "f6",
            "QUIET_MOVE",
            "d7",
            "d6",
            "QUIET_MOVE",
            "f6",
            "d8",
            "QUIET_MOVE",
            "d6",
            "d7",
            "QUIET_MOVE",
            "g5",
            "g6",
            "QUIET_MOVE",
            "d7",
            "d6",
            "QUIET_MOVE",
            "d8",
            "h4",
            "QUIET_MOVE",
            "d6",
            "d7",
            "QUIET_MOVE",
            "h4",
            "d8",
            "QUIET_MOVE",
            "d7",
            "d6",
            "QUIET_MOVE",
            "d8",
            "h4",
            "QUIET_MOVE",
            "d6",
            "d7",
            "QUIET_MOVE",
            "h4",
            "d8",
            "QUIET_MOVE",
            "END"
        };
        while (gs.endGameDetector.updateEndGameStatus(game.getCurrentPlayerToMove().getColor())
                == EndGameType.NOTHING) {
            if (!strs[i].equals("END")) {
                final Move move = Selfplay.createMove(strs[i++], strs[i++], strs[i++]);
                final boolean moveT = game.move(move);
                if (!moveT) throw new RuntimeException("Некорректная проверка истории");
            } else return;
        }
        throw new RuntimeException("Некорректная проверка истории");
    }
}
