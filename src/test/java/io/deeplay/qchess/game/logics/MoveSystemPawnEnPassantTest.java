package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.Knight;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MoveSystemPawnEnPassantTest {

    private Board board;
    private MoveSystem ms;

    @Before
    public void setUp() {
        board = new Board();
        ms = new MoveSystem(board);
    }

    private Move move1;
    private Move move2;
    private Move move3;
    private Move move4;

    private void setBlackPawns() throws ChessException {
        move1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        move2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        move3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        move4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Pawn(ms, board, false, move1.getFrom());
        Figure figureB2 = new Pawn(ms, board, false, move2.getFrom());
        Figure figureB3 = new Pawn(ms, board, false, move3.getFrom());
        Figure figureB4 = new Pawn(ms, board, false, move4.getFrom());

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
        Figure figureW1 = new Pawn(ms, board, true, move1.getFrom());
        Figure figureW2 = new Pawn(ms, board, true, move2.getFrom());
        Figure figureW3 = new Pawn(ms, board, true, move3.getFrom());
        Figure figureW4 = new Pawn(ms, board, true, move4.getFrom());

        board.setFigure(figureW1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);
        board.setFigure(figureW4);
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_1() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(ms, board, true, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        board.setFigure(figureW1);
        setBlackPawns();

        Assert.assertTrue(ms.isPawnEnPassant(move1.getFrom(), move1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move2.getFrom(), move2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move3.getFrom(), move3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_2() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white2 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c3"));
        Figure figureW2 = new Pawn(ms, board, true, white2.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white2);

        board.setFigure(figureW2);
        setBlackPawns();

        Assert.assertFalse(ms.isPawnEnPassant(move1.getFrom(), move1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move2.getFrom(), move2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move3.getFrom(), move3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_3() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white3 = new Move(MoveType.ATTACK, Cell.parse("c2"), Cell.parse("d3"));
        Figure figureW3 = new Pawn(ms, board, true, white3.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white3);

        board.setFigure(figureW3);
        setBlackPawns();

        Assert.assertFalse(ms.isPawnEnPassant(move1.getFrom(), move1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move2.getFrom(), move2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move3.getFrom(), move3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_notPawnDefense() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Knight(board, true, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        board.setFigure(figureW1);
        setBlackPawns();

        Assert.assertFalse(ms.isPawnEnPassant(move1.getFrom(), move1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move2.getFrom(), move2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move3.getFrom(), move3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_notPawnAttack() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(ms, board, true, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        Move black1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        Move black2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        Move black4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Knight(board, false, black1.getFrom());
        Figure figureB2 = new Knight(board, false, black2.getFrom());
        Figure figureB3 = new Knight(board, false, black3.getFrom());
        Figure figureB4 = new Knight(board, false, black4.getFrom());

        board.setFigure(figureW1);
        board.setFigure(figureB1);
        board.setFigure(figureB2);
        board.setFigure(figureB3);
        board.setFigure(figureB4);

        Assert.assertFalse(ms.isPawnEnPassant(black1.getFrom(), black1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(black2.getFrom(), black2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(black3.getFrom(), black3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(black4.getFrom(), black4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_1() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black1 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c7"), Cell.parse("c5"));
        Figure figureB1 = new Pawn(ms, board, false, black1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black1);

        board.setFigure(figureB1);
        setWhitePawns();

        Assert.assertTrue(ms.isPawnEnPassant(move1.getFrom(), move1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move2.getFrom(), move2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move3.getFrom(), move3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_2() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black2 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c7"), Cell.parse("c6"));
        Figure figureB2 = new Pawn(ms, board, false, black2.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black2);

        board.setFigure(figureB2);
        setWhitePawns();

        Assert.assertFalse(ms.isPawnEnPassant(move1.getFrom(), move1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move2.getFrom(), move2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move3.getFrom(), move3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move4.getFrom(), move4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_3() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("c7"), Cell.parse("d6"));
        Figure figureB3 = new Pawn(ms, board, false, black3.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black3);

        board.setFigure(figureB3);
        setWhitePawns();

        Assert.assertFalse(ms.isPawnEnPassant(move1.getFrom(), move1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move2.getFrom(), move2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move3.getFrom(), move3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(move4.getFrom(), move4.getTo()));
    }
}
