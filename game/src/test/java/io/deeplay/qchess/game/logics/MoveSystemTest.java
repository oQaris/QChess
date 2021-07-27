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
    private Pawn fB1;
    private Pawn fB2;
    private Pawn fB3;
    private Pawn fB4;
    private Pawn fW1;
    private Pawn fW2;
    private Pawn fW3;
    private Pawn fW4;

    @Before
    public void setUp() {
        gameSettings = new GameSettings(Board.BoardFilling.EMPTY);
        board = gameSettings.board;
        ms = new MoveSystem(gameSettings);
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_1()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(Color.WHITE, white1.getTo());

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
        fB1 = new Pawn(Color.BLACK, Cell.parse("b4"));
        fB2 = new Pawn(Color.BLACK, Cell.parse("b5"));
        fB3 = new Pawn(Color.BLACK, Cell.parse("b3"));
        fB4 = new Pawn(Color.BLACK, Cell.parse("c3"));
        board.setFigure(fB1);
        board.setFigure(fB2);
        board.setFigure(fB3);
        board.setFigure(fB4);
    }

    private void setPrevMove(Move move) throws NoSuchFieldException, IllegalAccessException {
        Field prevMove = gameSettings.history.getClass().getDeclaredField("lastMove");
        prevMove.setAccessible(true);
        prevMove.set(gameSettings.history, move);
    }

    @Test
    public void testIsCorrectPawnEnPassant_blackPawnAttack_2()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        Move white2 = new Move(MoveType.QUIET_MOVE, Cell.parse("c2"), Cell.parse("c3"));
        Figure figureW2 = new Pawn(Color.WHITE, white2.getTo());

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
        Move white3 = new Move(MoveType.ATTACK, Cell.parse("c2"), Cell.parse("d3"));
        Figure figureW3 = new Pawn(Color.WHITE, white3.getTo());

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
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Knight(Color.WHITE, white1.getTo());

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
        Move black1 = new Move(MoveType.LONG_MOVE, Cell.parse("c7"), Cell.parse("c5"));
        Figure figureB1 = new Pawn(Color.BLACK, black1.getTo());

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
        fW1 = new Pawn(Color.WHITE, Cell.parse("b5"));
        fW2 = new Pawn(Color.WHITE, Cell.parse("b4"));
        fW3 = new Pawn(Color.WHITE, Cell.parse("b6"));
        fW4 = new Pawn(Color.WHITE, Cell.parse("c6"));
        board.setFigure(fW1);
        board.setFigure(fW2);
        board.setFigure(fW3);
        board.setFigure(fW4);
    }

    @Test
    public void testIsCorrectPawnEnPassant_whitePawnAttack_2()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        Move black2 = new Move(MoveType.QUIET_MOVE, Cell.parse("c7"), Cell.parse("c6"));
        Figure figureB2 = new Pawn(Color.BLACK, black2.getTo());

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
        Move black3 = new Move(MoveType.ATTACK, Cell.parse("c7"), Cell.parse("d6"));
        Figure figureB3 = new Pawn(Color.BLACK, black3.getTo());

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
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Move white2 = new Move(MoveType.ATTACK, Cell.parse("b2"), Cell.parse("c3"));
        Pawn figure1 = new Pawn(Color.WHITE, white1.getTo());
        Pawn figure2 = new Pawn(Color.WHITE, white2.getFrom());

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

        Move move1 = new Move(MoveType.QUIET_MOVE, Cell.parse("e4"), Cell.parse("e7"));
        Move move2 = new Move(MoveType.QUIET_MOVE, Cell.parse("e4"), Cell.parse("d3"));
        Move move3 = new Move(MoveType.ATTACK, Cell.parse("e4"), Cell.parse("e1"));

        Assert.assertTrue(ms.isCorrectMove(move1));
        Assert.assertFalse(ms.isCorrectMove(move2));
        Assert.assertFalse(ms.isCorrectMove(move3));

        board.setFigure(new Queen(Color.BLACK, Cell.parse("e8")));
        Move move4 = new Move(MoveType.QUIET_MOVE, Cell.parse("e4"), Cell.parse("c4"));

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
        board.setFigure(new King(Color.BLACK, Cell.parse("a1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        Move move1 = new Move(MoveType.TURN_INTO, Cell.parse("c7"), Cell.parse("c8"));

        // в разные фигуры
        move1.setTurnInto(FigureType.BISHOP);
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.setTurnInto(FigureType.KING);
        Assert.assertFalse(ms.isCorrectMove(move1));

        move1.setTurnInto(FigureType.KNIGHT);
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.setTurnInto(FigureType.PAWN);
        Assert.assertFalse(ms.isCorrectMove(move1));

        move1.setTurnInto(FigureType.QUEEN);
        Assert.assertTrue(ms.isCorrectMove(move1));

        move1.setTurnInto(FigureType.ROOK);
        Assert.assertTrue(ms.isCorrectMove(move1));
    }

    @Test
    public void testIsCorrectPawnTurnInto_4() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        // из другой фигуры
        board.setFigure(new Rook(Color.WHITE, Cell.parse("g7")));
        Move move3 = new Move(MoveType.TURN_INTO, Cell.parse("g7"), Cell.parse("g8"));
        move3.setTurnInto(FigureType.QUEEN);
        Assert.assertFalse(ms.isCorrectMove(move3));
    }

    @Test
    public void testIsCorrectPawnTurnInto_5() throws ChessException, ChessError {
        board.setFigure(new King(Color.WHITE, Cell.parse("h1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("a1")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));

        // атакующим перемещением
        Move move4 = new Move(MoveType.TURN_INTO_ATTACK, Cell.parse("c7"), Cell.parse("d8"));
        move4.setTurnInto(FigureType.QUEEN);
        Assert.assertFalse(ms.isCorrectMove(move4));

        board.setFigure(new Rook(Color.BLACK, Cell.parse("d8")));

        Assert.assertTrue(ms.isCorrectMove(move4));
    }

    @Test
    public void testMove_QUIET_MOVE() throws ChessException, ChessError {
        Figure figure = new King(Color.WHITE, Cell.parse("d4"));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        board.setFigure(figure);

        Figure removed = ms.move(new Move(MoveType.QUIET_MOVE, Cell.parse("d4"), Cell.parse("c5")));

        Assert.assertEquals(figure, board.getFigure(Cell.parse("c5")));
        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("d4")));
    }

    @Test
    public void testMove_ATTACK() throws ChessException, ChessError {
        Figure white = new Pawn(Color.WHITE, Cell.parse("c3"));
        Figure black = new Pawn(Color.BLACK, Cell.parse("d4"));
        board.setFigure(white);
        board.setFigure(black);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        Figure removed = ms.move(new Move(MoveType.ATTACK, Cell.parse("d4"), Cell.parse("c3")));

        Assert.assertEquals(white, removed);
        Assert.assertNull(board.getFigure(Cell.parse("d4")));
        Assert.assertEquals(black, board.getFigure(Cell.parse("c3")));
    }

    @Test
    public void testMove_LONG_MOVE() throws ChessException, ChessError {
        Figure white = new Pawn(Color.WHITE, Cell.parse("c2"));
        board.setFigure(white);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        Figure removed = ms.move(new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4")));

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("c2")));
        Assert.assertNull(board.getFigure(Cell.parse("c3")));
        Assert.assertEquals(white, board.getFigure(Cell.parse("c4")));
    }

    @Test
    public void testMove_EN_PASSANT()
            throws ChessException, ChessError, NoSuchFieldException, IllegalAccessException {
        Figure white = new Pawn(Color.WHITE, Cell.parse("c4"));
        Figure black = new Pawn(Color.BLACK, Cell.parse("b4"));
        board.setFigure(white);
        board.setFigure(black);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        setPrevMove(new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4")));

        Figure removed = ms.move(new Move(MoveType.EN_PASSANT, Cell.parse("b4"), Cell.parse("c3")));

        Assert.assertEquals(white, removed);
        Assert.assertEquals(black, board.getFigure(Cell.parse("c3")));
        Assert.assertNull(board.getFigure(Cell.parse("b4")));
        Assert.assertNull(board.getFigure(Cell.parse("c4")));
    }

    @Test
    public void testMove_TURN_INTO() throws ChessException, ChessError {
        Figure white = new Pawn(Color.WHITE, Cell.parse("d7"));
        Figure black = new Pawn(Color.BLACK, Cell.parse("c2"));
        Figure whiteQueen = new Queen(Color.WHITE, Cell.parse("d8"));
        Figure blackQueen = new Queen(Color.BLACK, Cell.parse("c1"));
        board.setFigure(white);
        board.setFigure(black);
        board.setFigure(new King(Color.WHITE, Cell.parse("a1")));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));

        Move move = new Move(MoveType.TURN_INTO, Cell.parse("d7"), Cell.parse("d8"));
        move.setTurnInto(FigureType.QUEEN);
        Figure removed = ms.move(move);

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("d7")));
        Assert.assertEquals(whiteQueen, board.getFigure(Cell.parse("d8")));

        move = new Move(MoveType.TURN_INTO, Cell.parse("c2"), Cell.parse("c1"));
        move.setTurnInto(FigureType.QUEEN);
        removed = ms.move(move);

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("c2")));
        Assert.assertEquals(blackQueen, board.getFigure(Cell.parse("c1")));
    }

    @Test
    public void testMove_SHORT_CASTLING() throws ChessException, ChessError {
        Figure king = new King(Color.WHITE, Cell.parse("e1"));
        Figure rook = new Rook(Color.WHITE, Cell.parse("h1"));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        board.setFigure(king);
        board.setFigure(rook);

        Figure removed =
                ms.move(new Move(MoveType.SHORT_CASTLING, Cell.parse("e1"), Cell.parse("g1")));

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("e1")));
        Assert.assertNull(board.getFigure(Cell.parse("h1")));
        Assert.assertEquals(king, board.getFigure(Cell.parse("g1")));
        Assert.assertEquals(rook, board.getFigure(Cell.parse("f1")));
    }

    @Test
    public void testMove_LONG_CASTLING() throws ChessException, ChessError {
        Figure king = new King(Color.WHITE, Cell.parse("e1"));
        Figure rook = new Rook(Color.WHITE, Cell.parse("a1"));
        board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        board.setFigure(king);
        board.setFigure(rook);

        Figure removed =
                ms.move(new Move(MoveType.LONG_CASTLING, Cell.parse("e1"), Cell.parse("c1")));

        Assert.assertNull(removed);
        Assert.assertNull(board.getFigure(Cell.parse("e1")));
        Assert.assertNull(board.getFigure(Cell.parse("a1")));
        Assert.assertEquals(king, board.getFigure(Cell.parse("c1")));
        Assert.assertEquals(rook, board.getFigure(Cell.parse("d1")));
    }

    @Test
    public void testMove_EN_PASSANT_illegal() throws ChessException, ChessError {
        Figure king = new King(Color.BLACK, Cell.parse("a4"));
        Figure pawn = new Pawn(Color.BLACK, Cell.parse("d4"));
        pawn.setWasMoved(true);
        Figure king2 = new King(Color.WHITE, Cell.parse("h8"));
        Figure pawn2 = new Pawn(Color.WHITE, Cell.parse("e4"));
        Figure rook = new Rook(Color.WHITE, Cell.parse("h4"));
        board.setFigure(king);
        board.setFigure(pawn);
        board.setFigure(king2);
        board.setFigure(pawn2);
        board.setFigure(rook);

        Move move = new Move(MoveType.LONG_MOVE, Cell.parse("e2"), Cell.parse("e4"));
        gameSettings.history.addRecord(move);

        List<Move> list = ms.getAllCorrectMoves(Cell.parse("d4"));
        List<Move> expected =
                List.of(new Move(MoveType.QUIET_MOVE, Cell.parse("d4"), Cell.parse("d3")));

        Assert.assertEquals(expected, list);
    }
}
