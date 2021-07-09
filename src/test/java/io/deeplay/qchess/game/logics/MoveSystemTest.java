package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.*;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

public class MoveSystemTest {
    private Board board;
    private MoveSystem ms;
    private Move move1;
    private Move move2;
    private Move move3;
    private Move move4;

    @Before
    public void setUp() throws ChessError {
        board = new Board(Board.BoardFilling.EMPTY);
        ms = new MoveSystem(board);
    }

    private void setBlackPawns() throws ChessException {
        move1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        move2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        move3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        move4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Pawn(Color.BLACK, move1.getFrom());
        Figure figureB2 = new Pawn(Color.BLACK, move2.getFrom());
        Figure figureB3 = new Pawn(Color.BLACK, move3.getFrom());
        Figure figureB4 = new Pawn(Color.BLACK, move4.getFrom());

        board.setFigure(figureB1);
        board.setFigure(figureB2);
        board.setFigure(figureB3);
        board.setFigure(figureB4);
    }

    private void setWhitePawns() throws ChessException {
        move1 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c6"));
        move2 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c5"));
        move3 = new Move(MoveType.ATTACK, Cell.parse("b6"), Cell.parse("c7"));
        move4 = new Move(MoveType.ATTACK, Cell.parse("c6"), Cell.parse("d7"));
        Figure figureW1 = new Pawn(Color.WHITE, move1.getFrom());
        Figure figureW2 = new Pawn(Color.WHITE, move2.getFrom());
        Figure figureW3 = new Pawn(Color.WHITE, move3.getFrom());
        Figure figureW4 = new Pawn(Color.WHITE, move4.getFrom());

        board.setFigure(figureW1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);
        board.setFigure(figureW4);
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_1() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(Color.WHITE, white1.getTo());

        board.setFigure(figureW1);
        setBlackPawns();

        Assert.assertFalse(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));

        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        Assert.assertTrue(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move2.getFrom(), move2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move3.getFrom(), move3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_2() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white2 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c3"));
        Figure figureW2 = new Pawn(Color.WHITE, white2.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white2);

        board.setFigure(figureW2);
        setBlackPawns();

        Assert.assertFalse(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move2.getFrom(), move2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move3.getFrom(), move3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_3() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white3 = new Move(MoveType.ATTACK, Cell.parse("c2"), Cell.parse("d3"));
        Figure figureW3 = new Pawn(Color.WHITE, white3.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white3);

        board.setFigure(figureW3);
        setBlackPawns();

        Assert.assertFalse(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move2.getFrom(), move2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move3.getFrom(), move3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_notPawnDefense() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Knight(Color.WHITE, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        board.setFigure(figureW1);
        setBlackPawns();

        Assert.assertFalse(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move2.getFrom(), move2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move3.getFrom(), move3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_notPawnAttack() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(Color.WHITE, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        Move black1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        Move black2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        Move black4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Knight(Color.BLACK, black1.getFrom());
        Figure figureB2 = new Knight(Color.BLACK, black2.getFrom());
        Figure figureB3 = new Knight(Color.BLACK, black3.getFrom());
        Figure figureB4 = new Knight(Color.BLACK, black4.getFrom());

        board.setFigure(figureW1);
        board.setFigure(figureB1);
        board.setFigure(figureB2);
        board.setFigure(figureB3);
        board.setFigure(figureB4);

        Assert.assertFalse(Pawn.isPawnEnPassant(board, black1.getFrom(), black1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, black2.getFrom(), black2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, black3.getFrom(), black3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, black4.getFrom(), black4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_1() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black1 = new Move(MoveType.LONG_MOVE, Cell.parse("c7"), Cell.parse("c5"));
        Figure figureB1 = new Pawn(Color.BLACK, black1.getTo());

        board.setFigure(figureB1);
        setWhitePawns();

        Assert.assertFalse(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));

        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black1);

        Assert.assertTrue(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move2.getFrom(), move2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move3.getFrom(), move3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_2() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black2 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c7"), Cell.parse("c6"));
        Figure figureB2 = new Pawn(Color.BLACK, black2.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black2);

        board.setFigure(figureB2);
        setWhitePawns();

        Assert.assertFalse(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move2.getFrom(), move2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move3.getFrom(), move3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_3() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("c7"), Cell.parse("d6"));
        Figure figureB3 = new Pawn(Color.BLACK, black3.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black3);

        board.setFigure(figureB3);
        setWhitePawns();

        Assert.assertFalse(Pawn.isPawnEnPassant(board, move1.getFrom(), move1.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move2.getFrom(), move2.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move3.getFrom(), move3.getTo()));
        Assert.assertFalse(Pawn.isPawnEnPassant(board, move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_sameColors() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Move white2 = new Move(MoveType.ATTACK, Cell.parse("b2"), Cell.parse("c3"));
        Figure figure1 = new Pawn(Color.WHITE, white1.getTo());
        Figure figure2 = new Pawn(Color.WHITE, white2.getFrom());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        board.setFigure(figure1);
        board.setFigure(figure2);

        Assert.assertFalse(Pawn.isPawnEnPassant(board, white2.getFrom(), white2.getTo()));
    }

    @Test(expected = ChessError.class)
    public void testIsCheck_zeroFigures_1() throws ChessError {
        ms.isCheck(Color.WHITE);
    }

    @Test(expected = ChessError.class)
    public void testIsCheck_zeroFigures_2() throws ChessError {
        ms.isCheck(Color.BLACK);
    }

    @Test
    public void testIsCheck() throws ChessError, ChessException {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("e8")));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("e2")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("e7")));

        ms.isCheck(Color.WHITE);
        Assert.assertFalse(ms.isCheck(Color.WHITE));
        Assert.assertFalse(ms.isCheck(Color.BLACK));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("f2")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("f7")));

        Assert.assertTrue(ms.isCheck(Color.WHITE));
        Assert.assertTrue(ms.isCheck(Color.BLACK));
    }

    @Test
    public void testIsStalemate_black() throws ChessException, ChessError {
        board.setFigure(new Pawn(Color.BLACK, Cell.parse("b5")));
        board.setFigure(new Pawn(Color.BLACK, Cell.parse("c6")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        board.setFigure(new Pawn(Color.WHITE, Cell.parse("b4")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c5")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("h7")));
        board.setFigure(new King(Color.WHITE, Cell.parse("f6")));
        board.setFigure(new Bishop(Color.WHITE, Cell.parse("c2")));

        Assert.assertTrue(ms.isStalemate(Color.BLACK));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("g3")));

        Assert.assertFalse(ms.isStalemate(Color.BLACK));
    }

    @Test
    public void testIsStalemate_white() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h3")));
        board.setFigure(new Rook(Color.BLACK, Cell.parse("g7")));

        Assert.assertTrue(ms.isStalemate(Color.WHITE));
        Assert.assertFalse(ms.isCheckmate(Color.WHITE));

        board.setFigure(new Pawn(Color.WHITE, Cell.parse("g3")));

        Assert.assertFalse(ms.isStalemate(Color.WHITE));
    }

    @Test
    public void testIsCheckmate() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("f8")));
        board.setFigure(new King(Color.BLACK, Cell.parse("e6")));

        Assert.assertFalse(ms.isCheckmate(Color.WHITE));
        Assert.assertFalse(ms.isCheckmate(Color.BLACK));

        board.setFigure(new Queen(Color.BLACK, Cell.parse("f7")));

        Assert.assertTrue(ms.isCheckmate(Color.WHITE));

        board.setFigure(new Rook(Color.WHITE, Cell.parse("h7")));

        Assert.assertFalse(ms.isCheckmate(Color.WHITE));
    }

    @Test
    public void testIsCorrectMove_1() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        board.setFigure(new Rook(Color.WHITE, Cell.parse("e4")));

        Move move1 = new Move(MoveType.SIMPLE_STEP, Cell.parse("e4"), Cell.parse("e7"));
        Move move2 = new Move(MoveType.SIMPLE_STEP, Cell.parse("e4"), Cell.parse("d3"));
        Move move3 = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));

        Assert.assertTrue(ms.isCorrectMove(move1));
        Assert.assertFalse(ms.isCorrectMove(move2));
        Assert.assertFalse(ms.isCorrectMove(move3));

        board.setFigure(new Queen(Color.BLACK, Cell.parse("e8")));
        Move move4 = new Move(MoveType.SIMPLE_STEP, Cell.parse("e4"), Cell.parse("c4"));

        Assert.assertTrue(ms.isCorrectMove(move1));
        Assert.assertFalse(ms.isCorrectMove(move2));
        Assert.assertFalse(ms.isCorrectMove(move3));
        Assert.assertFalse(ms.isCorrectMove(move4));
    }

    @Test(expected = ChessError.class)
    public void testIsCorrectMove_2() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        board.setFigure(new Rook(Color.BLACK, Cell.parse("e4")));

        Move move = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));

        ms.isCorrectMove(move);
    }

    @Test
    public void testIsCorrectMove_3() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        Move move = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));
        Assert.assertFalse(ms.isCorrectMove(move));
    }

    @Test
    public void testIsCorrectMove_4() throws ChessException, ChessError {
        board.setFigure(new Rook(Color.BLACK, Cell.parse("e4")));
        Move move = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));
        Assert.assertFalse(ms.isCorrectMove(move));
    }

    @Test
    public void testIsCorrectPawnTurnInto_1() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        Move move1 = new Move(MoveType.TURN_INTO, Cell.parse("c7"), Cell.parse("c8"));

        // в разные фигуры
        move1.setTurnInto(new Bishop(Color.WHITE, Cell.parse("c8")));
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.setTurnInto(new King(Color.WHITE, Cell.parse("c8")));
        Assert.assertFalse(ms.isCorrectMove(move1));

        move1.setTurnInto(new Knight(Color.WHITE, Cell.parse("c8")));
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.setTurnInto(new Pawn(Color.WHITE, Cell.parse("c8")));
        Assert.assertFalse(ms.isCorrectMove(move1));

        move1.setTurnInto(new Queen(Color.WHITE, Cell.parse("c8")));
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.setTurnInto(new Rook(Color.WHITE, Cell.parse("c8")));
        Assert.assertTrue(ms.isCorrectMove(move1));
    }

    @Test
    public void testIsCorrectPawnTurnInto_2() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        Move move1 = new Move(MoveType.TURN_INTO, Cell.parse("c7"), Cell.parse("c8"));

        // в другой цвет
        move1.setTurnInto(new Queen(Color.BLACK, Cell.parse("c8")));
        Assert.assertFalse(ms.isCorrectMove(move1));
    }

    @Test
    public void testIsCorrectPawnTurnInto_3() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        Move move1 = new Move(MoveType.TURN_INTO, Cell.parse("c7"), Cell.parse("c8"));

        // в другую клетку
        move1.setTurnInto(new Queen(Color.WHITE, Cell.parse("d8")));
        Assert.assertFalse(ms.isCorrectMove(move1));

        Move move2 = new Move(MoveType.TURN_INTO, Cell.parse("c7"), Cell.parse("d8"));

        move2.setTurnInto(new Queen(Color.WHITE, Cell.parse("c8")));
        Assert.assertFalse(ms.isCorrectMove(move2));

        move2.setTurnInto(new Queen(Color.WHITE, Cell.parse("d8")));
        Assert.assertFalse(ms.isCorrectMove(move2));
    }

    @Test
    public void testIsCorrectPawnTurnInto_4() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        // из другой фигуры
        board.setFigure(new Rook(Color.WHITE, Cell.parse("g7")));
        Move move3 = new Move(MoveType.TURN_INTO, Cell.parse("g7"), Cell.parse("g8"));
        move3.setTurnInto(new Queen(Color.WHITE, Cell.parse("g8")));
        Assert.assertFalse(ms.isCorrectMove(move3));
    }

    @Test
    public void testIsCorrectPawnTurnInto_5() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        // атакующим перемещением
        Move move4 = new Move(MoveType.TURN_INTO, Cell.parse("c7"), Cell.parse("d8"));
        move4.setTurnInto(new Queen(Color.WHITE, Cell.parse("d8")));
        Assert.assertFalse(ms.isCorrectMove(move4));

        board.setFigure(new Rook(Color.BLACK, Cell.parse("d8")));

        Assert.assertTrue(ms.isCorrectMove(move4));
    }
}
