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

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_1() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(ms, board, true, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        Move black1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        Move black2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        Move black4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Pawn(ms, board, false, black1.getFrom());
        Figure figureB2 = new Pawn(ms, board, false, black2.getFrom());
        Figure figureB3 = new Pawn(ms, board, false, black3.getFrom());
        Figure figureB4 = new Pawn(ms, board, false, black4.getFrom());

        board.setFigure(figureW1);
        board.setFigure(figureB1);
        board.setFigure(figureB2);
        board.setFigure(figureB3);
        board.setFigure(figureB4);

        Assert.assertTrue(ms.isPawnEnPassant(black1.getFrom(), black1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(black2.getFrom(), black2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(black3.getFrom(), black3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(black4.getFrom(), black4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_2() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white2 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c3"));
        Figure figureW2 = new Pawn(ms, board, true, white2.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white2);

        Move black1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        Move black2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        Move black4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Pawn(ms, board, false, black1.getFrom());
        Figure figureB2 = new Pawn(ms, board, false, black2.getFrom());
        Figure figureB3 = new Pawn(ms, board, false, black3.getFrom());
        Figure figureB4 = new Pawn(ms, board, false, black4.getFrom());

        board.setFigure(figureW2);
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
    public void testIsCorrectPawnEnPassant_blackPawnAttack_3() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white3 = new Move(MoveType.ATTACK, Cell.parse("c2"), Cell.parse("d3"));
        Figure figureW3 = new Pawn(ms, board, true, white3.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white3);

        Move black1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        Move black2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        Move black4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Pawn(ms, board, false, black1.getFrom());
        Figure figureB2 = new Pawn(ms, board, false, black2.getFrom());
        Figure figureB3 = new Pawn(ms, board, false, black3.getFrom());
        Figure figureB4 = new Pawn(ms, board, false, black4.getFrom());

        board.setFigure(figureW3);
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
    public void testIsCorrectPawnEnPassant_notPawnDefense() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Knight(board, true, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        Move black1 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c3"));
        Move black2 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c4"));
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("b3"), Cell.parse("c2"));
        Move black4 = new Move(MoveType.ATTACK, Cell.parse("c3"), Cell.parse("d2"));
        Figure figureB1 = new Pawn(ms, board, false, black1.getFrom());
        Figure figureB2 = new Pawn(ms, board, false, black2.getFrom());
        Figure figureB3 = new Pawn(ms, board, false, black3.getFrom());
        Figure figureB4 = new Pawn(ms, board, false, black4.getFrom());

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

        Move white1 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c6"));
        Move white2 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c5"));
        Move white3 = new Move(MoveType.ATTACK, Cell.parse("b6"), Cell.parse("c7"));
        Move white4 = new Move(MoveType.ATTACK, Cell.parse("c6"), Cell.parse("d7"));
        Figure figureW1 = new Pawn(ms, board, true, white1.getFrom());
        Figure figureW2 = new Pawn(ms, board, true, white2.getFrom());
        Figure figureW3 = new Pawn(ms, board, true, white3.getFrom());
        Figure figureW4 = new Pawn(ms, board, true, white4.getFrom());

        board.setFigure(figureB1);
        board.setFigure(figureW1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);
        board.setFigure(figureW4);

        Assert.assertTrue(ms.isPawnEnPassant(white1.getFrom(), white1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white2.getFrom(), white2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white3.getFrom(), white3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white4.getFrom(), white4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_2() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black2 = new Move(MoveType.SIMPLE_STEP, Cell.parse("c7"), Cell.parse("c6"));
        Figure figureB2 = new Pawn(ms, board, false, black2.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black2);

        Move white1 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c6"));
        Move white2 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c5"));
        Move white3 = new Move(MoveType.ATTACK, Cell.parse("b6"), Cell.parse("c7"));
        Move white4 = new Move(MoveType.ATTACK, Cell.parse("c6"), Cell.parse("d7"));
        Figure figureW1 = new Pawn(ms, board, true, white1.getFrom());
        Figure figureW2 = new Pawn(ms, board, true, white2.getFrom());
        Figure figureW3 = new Pawn(ms, board, true, white3.getFrom());
        Figure figureW4 = new Pawn(ms, board, true, white4.getFrom());

        board.setFigure(figureB2);
        board.setFigure(figureW1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);
        board.setFigure(figureW4);

        Assert.assertFalse(ms.isPawnEnPassant(white1.getFrom(), white1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white2.getFrom(), white2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white3.getFrom(), white3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white4.getFrom(), white4.getTo()));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_3() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("c7"), Cell.parse("d6"));
        Figure figureB3 = new Pawn(ms, board, false, black3.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, black3);

        Move white1 = new Move(MoveType.ATTACK, Cell.parse("b5"), Cell.parse("c6"));
        Move white2 = new Move(MoveType.ATTACK, Cell.parse("b4"), Cell.parse("c5"));
        Move white3 = new Move(MoveType.ATTACK, Cell.parse("b6"), Cell.parse("c7"));
        Move white4 = new Move(MoveType.ATTACK, Cell.parse("c6"), Cell.parse("d7"));
        Figure figureW1 = new Pawn(ms, board, true, white1.getFrom());
        Figure figureW2 = new Pawn(ms, board, true, white2.getFrom());
        Figure figureW3 = new Pawn(ms, board, true, white3.getFrom());
        Figure figureW4 = new Pawn(ms, board, true, white4.getFrom());

        board.setFigure(figureB3);
        board.setFigure(figureW1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);
        board.setFigure(figureW4);

        Assert.assertFalse(ms.isPawnEnPassant(white1.getFrom(), white1.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white2.getFrom(), white2.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white3.getFrom(), white3.getTo()));
        Assert.assertFalse(ms.isPawnEnPassant(white4.getFrom(), white4.getTo()));
    }
}
