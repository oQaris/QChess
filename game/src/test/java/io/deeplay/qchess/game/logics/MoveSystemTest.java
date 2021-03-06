package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.Rook;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MoveSystemTest {
    private GameSettings gameSettings;
    private Board board;
    private MoveSystem ms;
    private Cell cell1;
    private Cell cell2;
    private Cell cell3;
    private Cell cell4;
    private PawnTest fB1;
    private PawnTest fB2;
    private PawnTest fB3;
    private PawnTest fB4;
    private PawnTest fW1;
    private PawnTest fW2;
    private PawnTest fW3;
    private PawnTest fW4;

    @Before
    public void setUp() {
        gameSettings = new GameSettings(Board.BoardFilling.EMPTY);
        board = gameSettings.board;
        ms = new MoveSystem(gameSettings);
    }

    @Test
    public void testGetAllCorrectMoves() throws ChessException, ChessError {
        // превращение пешки
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("a7")));
        board.setFigure(new Pawn(Color.BLACK, Cell.parse("a2")));

        final List<Move> expectedForWhite =
                List.of(new Move(MoveType.TURN_INTO, Cell.parse("a7"), Cell.parse("a8")));

        Assert.assertEquals(expectedForWhite, ms.getAllCorrectMoves(Color.WHITE));
        Assert.assertEquals(expectedForWhite, ms.getAllCorrectMoves(Cell.parse("a7")));

        final List<Move> expectedForBlack =
                List.of(new Move(MoveType.TURN_INTO, Cell.parse("a2"), Cell.parse("a1")));

        Assert.assertEquals(expectedForBlack, ms.getAllCorrectMoves(Color.BLACK));
        Assert.assertEquals(expectedForBlack, ms.getAllCorrectMoves(Cell.parse("a2")));
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_1()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        final Figure figureW1 = new Pawn(Color.WHITE, white1.getTo());

        board.setFigure(figureW1);
        setBlackPawns();

        Assert.assertFalse(fB1.isPawnEnPassant(gameSettings, cell1));

        setPrevMove(white1);

        Assert.assertTrue(fB1.isPawnEnPassant(gameSettings, cell1));
        Assert.assertFalse(fB2.isPawnEnPassant(gameSettings, cell2));
        Assert.assertFalse(fB3.isPawnEnPassant(gameSettings, cell3));
        Assert.assertFalse(fB4.isPawnEnPassant(gameSettings, cell4));
    }

    private void setBlackPawns() throws ChessException {
        cell1 = Cell.parse("c3");
        cell2 = Cell.parse("c4");
        cell3 = Cell.parse("c2");
        cell4 = Cell.parse("d2");
        fB1 = new PawnTest(Color.BLACK, Cell.parse("b4"));
        fB2 = new PawnTest(Color.BLACK, Cell.parse("b5"));
        fB3 = new PawnTest(Color.BLACK, Cell.parse("b3"));
        fB4 = new PawnTest(Color.BLACK, Cell.parse("c3"));
        board.setFigure(fB1);
        board.setFigure(fB2);
        board.setFigure(fB3);
        board.setFigure(fB4);
    }

    private void setPrevMove(final Move move) throws NoSuchFieldException, IllegalAccessException {
        final Field prevMove = gameSettings.history.getClass().getDeclaredField("lastMove");
        prevMove.setAccessible(true);
        prevMove.set(gameSettings.history, move);
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_2()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move white2 = new Move(MoveType.QUIET_MOVE, Cell.parse("c2"), Cell.parse("c3"));
        final Figure figureW2 = new Pawn(Color.WHITE, white2.getTo());

        setPrevMove(white2);

        board.setFigure(figureW2);
        setBlackPawns();

        Assert.assertFalse(fB1.isPawnEnPassant(gameSettings, cell1));
        Assert.assertFalse(fB2.isPawnEnPassant(gameSettings, cell2));
        Assert.assertFalse(fB3.isPawnEnPassant(gameSettings, cell3));
        Assert.assertFalse(fB4.isPawnEnPassant(gameSettings, cell4));
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_3()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move white3 = new Move(MoveType.ATTACK, Cell.parse("c2"), Cell.parse("d3"));
        final Figure figureW3 = new Pawn(Color.WHITE, white3.getTo());

        setPrevMove(white3);

        board.setFigure(figureW3);
        setBlackPawns();

        Assert.assertFalse(fB1.isPawnEnPassant(gameSettings, cell1));
        Assert.assertFalse(fB2.isPawnEnPassant(gameSettings, cell2));
        Assert.assertFalse(fB3.isPawnEnPassant(gameSettings, cell3));
        Assert.assertFalse(fB4.isPawnEnPassant(gameSettings, cell4));
    }

    @Test
    public void testIsCorrectPawnEnPassant_notPawnDefense()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        final Figure figureW1 = new Knight(Color.WHITE, white1.getTo());

        setPrevMove(white1);

        board.setFigure(figureW1);
        setBlackPawns();

        Assert.assertFalse(fB1.isPawnEnPassant(gameSettings, cell1));
        Assert.assertFalse(fB2.isPawnEnPassant(gameSettings, cell2));
        Assert.assertFalse(fB3.isPawnEnPassant(gameSettings, cell3));
        Assert.assertFalse(fB4.isPawnEnPassant(gameSettings, cell4));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_1()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move black1 = new Move(MoveType.LONG_MOVE, Cell.parse("c7"), Cell.parse("c5"));
        final Figure figureB1 = new Pawn(Color.BLACK, black1.getTo());

        board.setFigure(figureB1);
        setWhitePawns();

        Assert.assertFalse(fW1.isPawnEnPassant(gameSettings, cell1));

        setPrevMove(black1);

        Assert.assertTrue(fW1.isPawnEnPassant(gameSettings, cell1));
        Assert.assertFalse(fW2.isPawnEnPassant(gameSettings, cell2));
        Assert.assertFalse(fW3.isPawnEnPassant(gameSettings, cell3));
        Assert.assertFalse(fW4.isPawnEnPassant(gameSettings, cell4));
    }

    private void setWhitePawns() throws ChessException {
        cell1 = Cell.parse("c6");
        cell2 = Cell.parse("c5");
        cell3 = Cell.parse("c7");
        cell4 = Cell.parse("d7");
        fW1 = new PawnTest(Color.WHITE, Cell.parse("b5"));
        fW2 = new PawnTest(Color.WHITE, Cell.parse("b4"));
        fW3 = new PawnTest(Color.WHITE, Cell.parse("b6"));
        fW4 = new PawnTest(Color.WHITE, Cell.parse("c6"));
        board.setFigure(fW1);
        board.setFigure(fW2);
        board.setFigure(fW3);
        board.setFigure(fW4);
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_2()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move black2 = new Move(MoveType.QUIET_MOVE, Cell.parse("c7"), Cell.parse("c6"));
        final Figure figureB2 = new Pawn(Color.BLACK, black2.getTo());

        setPrevMove(black2);

        board.setFigure(figureB2);
        setWhitePawns();

        Assert.assertFalse(fW1.isPawnEnPassant(gameSettings, cell1));
        Assert.assertFalse(fW2.isPawnEnPassant(gameSettings, cell2));
        Assert.assertFalse(fW3.isPawnEnPassant(gameSettings, cell3));
        Assert.assertFalse(fW4.isPawnEnPassant(gameSettings, cell4));
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_3()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move black3 = new Move(MoveType.ATTACK, Cell.parse("c7"), Cell.parse("d6"));
        final Figure figureB3 = new Pawn(Color.BLACK, black3.getTo());

        setPrevMove(black3);

        board.setFigure(figureB3);
        setWhitePawns();

        Assert.assertFalse(fW1.isPawnEnPassant(gameSettings, cell1));
        Assert.assertFalse(fW2.isPawnEnPassant(gameSettings, cell2));
        Assert.assertFalse(fW3.isPawnEnPassant(gameSettings, cell3));
        Assert.assertFalse(fW4.isPawnEnPassant(gameSettings, cell4));
    }

    @Test
    public void testIsCorrectPawnEnPassant_sameColors()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        final Move white2 = new Move(MoveType.ATTACK, Cell.parse("b2"), Cell.parse("c3"));
        final Pawn figure1 = new Pawn(Color.WHITE, white1.getTo());
        final PawnTest figure2 = new PawnTest(Color.WHITE, white2.getFrom());

        setPrevMove(white1);

        board.setFigure(figure1);
        board.setFigure(figure2);

        Assert.assertFalse(figure2.isPawnEnPassant(gameSettings, white2.getTo()));
    }

    @Test
    public void testIsCorrectMove_1() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("a1")));
        board.setFigure(new Rook(Color.WHITE, Cell.parse("e4")));

        final Move move1 = new Move(MoveType.QUIET_MOVE, Cell.parse("e4"), Cell.parse("e7"));
        final Move move2 = new Move(MoveType.QUIET_MOVE, Cell.parse("e4"), Cell.parse("d3"));
        final Move move3 = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));

        Assert.assertTrue(ms.isCorrectMove(move1));
        Assert.assertFalse(ms.isCorrectMove(move2));
        Assert.assertFalse(ms.isCorrectMove(move3));

        board.setFigure(new Queen(Color.BLACK, Cell.parse("e8")));
        final Move move4 = new Move(MoveType.QUIET_MOVE, Cell.parse("e4"), Cell.parse("c4"));

        Assert.assertTrue(ms.isCorrectMove(move1));
        Assert.assertFalse(ms.isCorrectMove(move2));
        Assert.assertFalse(ms.isCorrectMove(move3));
        Assert.assertFalse(ms.isCorrectMove(move4));
    }

    @Test(expected = ChessError.class)
    public void testIsCorrectMove_2() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        board.setFigure(new Rook(Color.BLACK, Cell.parse("e4")));

        final Move move = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));

        ms.isCorrectMove(move);
    }

    @Test
    public void testIsCorrectMove_3() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("e1")));
        final Move move = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));
        Assert.assertFalse(ms.isCorrectMove(move));
    }

    @Test
    public void testIsCorrectMove_4() throws ChessException, ChessError {
        board.setFigure(new Rook(Color.BLACK, Cell.parse("e4")));
        final Move move = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));
        Assert.assertFalse(ms.isCorrectMove(move));
    }

    @Test
    public void testIsCorrectPawnTurnInto_1() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("a1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        final Move move1 = new Move(MoveType.TURN_INTO, Cell.parse("c7"), Cell.parse("c8"));

        // в разные фигуры
        move1.turnInto = FigureType.BISHOP;
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.turnInto = FigureType.KING;
        Assert.assertFalse(ms.isCorrectMove(move1));

        move1.turnInto = FigureType.KNIGHT;
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.turnInto = FigureType.PAWN;
        Assert.assertFalse(ms.isCorrectMove(move1));

        move1.turnInto = FigureType.QUEEN;
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.turnInto = FigureType.ROOK;
        Assert.assertTrue(ms.isCorrectMove(move1));
    }

    @Test
    public void testIsCorrectPawnTurnInto_4() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        // из другой фигуры
        board.setFigure(new Rook(Color.WHITE, Cell.parse("g7")));
        final Move move3 = new Move(MoveType.TURN_INTO, Cell.parse("g7"), Cell.parse("g8"));
        move3.turnInto = FigureType.QUEEN;
        Assert.assertFalse(ms.isCorrectMove(move3));
    }

    @Test
    public void testIsCorrectPawnTurnInto_5() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("a1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        // атакующим перемещением
        final Move move4 = new Move(MoveType.TURN_INTO_ATTACK, Cell.parse("c7"), Cell.parse("d8"));
        move4.turnInto = FigureType.QUEEN;
        Assert.assertFalse(ms.isCorrectMove(move4));

        board.setFigure(new Rook(Color.BLACK, Cell.parse("d8")));

        Assert.assertTrue(ms.isCorrectMove(move4));
    }

    @Test
    public void testMove_QUIET_MOVE() throws ChessException, ChessError {
        final Figure figure = new King(Color.WHITE, Cell.parse("d4"));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        board.setFigure(figure);

        final Figure removed =
                ms.move(new Move(MoveType.QUIET_MOVE, Cell.parse("d4"), Cell.parse("c5")));

        Assert.assertEquals(figure, board.getFigure(Cell.parse("c5")));
        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("d4")));
    }

    @Test
    public void testMove_ATTACK() throws ChessException, ChessError {
        final Figure white = new Pawn(Color.WHITE, Cell.parse("c3"));
        final Figure black = new Pawn(Color.BLACK, Cell.parse("d4"));
        board.setFigure(white);
        board.setFigure(black);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        final Figure removed =
                ms.move(new Move(MoveType.ATTACK, Cell.parse("d4"), Cell.parse("c3")));

        Assert.assertEquals(white, removed);
        Assert.assertNull(board.getFigure(Cell.parse("d4")));
        Assert.assertEquals(black, board.getFigure(Cell.parse("c3")));
    }

    @Test
    public void testMove_LONG_MOVE() throws ChessException, ChessError {
        final Figure white = new Pawn(Color.WHITE, Cell.parse("c2"));
        board.setFigure(white);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        final Figure removed =
                ms.move(new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4")));

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("c2")));
        Assert.assertNull(board.getFigure(Cell.parse("c3")));
        Assert.assertEquals(white, board.getFigure(Cell.parse("c4")));
    }

    @Test
    public void testMove_EN_PASSANT()
            throws ChessException, ChessError, NoSuchFieldException, IllegalAccessException {
        final Figure white = new Pawn(Color.WHITE, Cell.parse("c4"));
        final Figure black = new Pawn(Color.BLACK, Cell.parse("b4"));
        board.setFigure(white);
        board.setFigure(black);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        setPrevMove(new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4")));

        final Figure removed =
                ms.move(new Move(MoveType.EN_PASSANT, Cell.parse("b4"), Cell.parse("c3")));

        Assert.assertEquals(white, removed);
        Assert.assertEquals(black, board.getFigure(Cell.parse("c3")));
        Assert.assertNull(board.getFigure(Cell.parse("b4")));
        Assert.assertNull(board.getFigure(Cell.parse("c4")));
    }

    @Test
    public void testMove_TURN_INTO() throws ChessException, ChessError {
        final Figure white = new Pawn(Color.WHITE, Cell.parse("d7"));
        final Figure black = new Pawn(Color.BLACK, Cell.parse("c2"));
        final Figure whiteQueen = new Queen(Color.WHITE, Cell.parse("d8"));
        final Figure blackQueen = new Queen(Color.BLACK, Cell.parse("c1"));
        board.setFigure(white);
        board.setFigure(black);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        Move move = new Move(MoveType.TURN_INTO, Cell.parse("d7"), Cell.parse("d8"));
        move.turnInto = FigureType.QUEEN;
        Figure removed = ms.move(move);

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("d7")));
        Assert.assertEquals(whiteQueen, board.getFigure(Cell.parse("d8")));

        move = new Move(MoveType.TURN_INTO, Cell.parse("c2"), Cell.parse("c1"));
        move.turnInto = FigureType.QUEEN;
        removed = ms.move(move);

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("c2")));
        Assert.assertEquals(blackQueen, board.getFigure(Cell.parse("c1")));
    }

    @Test
    public void testMove_SHORT_CASTLING() throws ChessException, ChessError {
        final Figure king = new King(Color.WHITE, Cell.parse("e1"));
        final Figure rook = new Rook(Color.WHITE, Cell.parse("h1"));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        board.setFigure(king);
        board.setFigure(rook);

        final Figure removed =
                ms.move(new Move(MoveType.SHORT_CASTLING, Cell.parse("e1"), Cell.parse("g1")));

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("e1")));
        Assert.assertNull(board.getFigure(Cell.parse("h1")));
        Assert.assertEquals(king, board.getFigure(Cell.parse("g1")));
        Assert.assertEquals(rook, board.getFigure(Cell.parse("f1")));
    }

    @Test
    public void testMove_LONG_CASTLING() throws ChessException, ChessError {
        final Figure king = new King(Color.WHITE, Cell.parse("e1"));
        final Figure rook = new Rook(Color.WHITE, Cell.parse("a1"));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        board.setFigure(king);
        board.setFigure(rook);

        final Figure removed =
                ms.move(new Move(MoveType.LONG_CASTLING, Cell.parse("e1"), Cell.parse("c1")));

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("e1")));
        Assert.assertNull(board.getFigure(Cell.parse("a1")));
        Assert.assertEquals(king, board.getFigure(Cell.parse("c1")));
        Assert.assertEquals(rook, board.getFigure(Cell.parse("d1")));
    }

    @Test
    public void testMove_EN_PASSANT_illegal() throws ChessException, ChessError {
        final Figure king = new King(Color.BLACK, Cell.parse("a4"));
        final Figure pawn = new Pawn(Color.BLACK, Cell.parse("d4"));
        pawn.wasMoved = true;
        final Figure king2 = new King(Color.WHITE, Cell.parse("h8"));
        final Figure pawn2 = new Pawn(Color.WHITE, Cell.parse("e4"));
        final Figure rook = new Rook(Color.WHITE, Cell.parse("h4"));
        board.setFigure(king);
        board.setFigure(pawn);
        board.setFigure(king2);
        board.setFigure(pawn2);
        board.setFigure(rook);

        final Move move = new Move(MoveType.LONG_MOVE, Cell.parse("e2"), Cell.parse("e4"));
        gameSettings.history.addRecord(move);

        final List<Move> list = ms.getAllCorrectMoves(Cell.parse("d4"));
        final List<Move> expected =
                List.of(new Move(MoveType.QUIET_MOVE, Cell.parse("d4"), Cell.parse("d3")));

        Assert.assertEquals(expected, list);
    }

    private static class PawnTest extends Pawn {

        private static final Method method;

        static {
            try {
                method =
                        Pawn.class.getDeclaredMethod(
                                "isPawnEnPassant", GameSettings.class, Cell.class);
                method.setAccessible(true);
            } catch (final NoSuchMethodException e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        public PawnTest(final Color color, final Cell position) {
            super(color, position);
        }

        public boolean isPawnEnPassant(final GameSettings gs, final Cell cell) {
            try {
                return (boolean) method.invoke(this, gs, cell);
            } catch (final InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
