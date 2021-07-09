package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FigureTest {

    private Board board;
    private MoveSystem ms;

    @Before
    public void setUp() {
        board = new Board();
        ms = new MoveSystem(board);
    }

    @Test
    public void testBishop() throws ChessException {
        //--- Слон ---//

        var bishop1 = new Bishop(board, true, Cell.parse("e7"));
        board.setFigure(bishop1);

        Assert.assertEquals(
                toCellsSet("D8", "F8", "D6", "F6", "C5", "G5", "B4", "H4", "A3"),
                extractCellTo(bishop1.getAllMoves()));

        var bishop2 = new Bishop(board, true, Cell.parse("b3"));
        board.setFigure(bishop2);

        Assert.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8"),
                extractCellTo(bishop2.getAllMoves()));
    }

    @Test
    public void testBishopWithEnemyPawn() throws ChessException {
        //--- Слон с вражесткой пешкой ---//

        Board testBoard = new Board();
        var pawn = new Pawn(ms, testBoard, false, Cell.parse("e3"));
        var bishop = new Bishop(testBoard, true, Cell.parse("c1"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(bishop);

        Assert.assertEquals(toCellsSet("A3", "B2", "D2", "E3"),
                extractCellTo(bishop.getAllMoves()));
    }

    @Test
    public void testBishopWithFriendPawn() throws ChessException {
        //--- Слон с дружеской пешкой ---//
        Board testBoard = new Board();
        var pawn = new Pawn(ms, testBoard, true, Cell.parse("e3"));
        var bishop = new Bishop(testBoard, true, Cell.parse("c1"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(bishop);

        Assert.assertEquals(toCellsSet("A3", "B2", "D2"),
                extractCellTo(bishop.getAllMoves()));
    }

    @Test
    public void testRook() throws ChessException {
        //--- Ладья ---//
        var rook = new Rook(board, false, Cell.parse("a6"));
        board.setFigure(rook);
        Assert.assertEquals(
                toCellsSet("A8", "A7", "A5", "A4", "A3", "A2", "A1", "B6", "C6", "D6", "E6", "F6", "G6", "H6"),
                extractCellTo(rook.getAllMoves()));
    }

    @Test
    public void testCornerBlockedRook() throws ChessException {
        //--- Ладья в углу с противниками на пути---//
        Board testBoard = new Board();
        var rook = new Rook(testBoard, true, Cell.parse("a8"));
        var rook1 = new Rook(testBoard, false, Cell.parse("a6"));
        var rook2 = new Rook(testBoard, false, Cell.parse("c8"));
        testBoard.setFigure(rook);
        testBoard.setFigure(rook1);
        testBoard.setFigure(rook2);
        Assert.assertEquals(
                toCellsSet("b8", "a7", "a6", "c8"),
                extractCellTo(rook.getAllMoves()));
    }

    @Test
    public void testQueen() throws ChessException {
        //--- Ферзь ---//
        var queen = new Queen(board, false, Cell.parse("b3"));
        board.setFigure(queen);
        Assert.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8", "B8", "B7", "B6", "B5",
                        "B4", "B2", "B1", "A3", "C3", "D3", "E3", "F3", "G3", "H3"),
                extractCellTo(queen.getAllMoves()));
    }

    @Test
    public void testQueen_jumpBlack() throws ChessException {
        //--- Ферзь ---//
        var queen = new Queen(board, true, Cell.parse("c6"));
        board.setFigure(queen);
        board.setFigure(new Queen(board, false, Cell.parse("d7")));
        board.setFigure(new King(ms, board, false, Cell.parse("e8")));
        board.setFigure(new Pawn(ms, board, true, Cell.parse("c7")));
        board.setFigure(new Pawn(ms, board, true, Cell.parse("c5")));
        board.setFigure(new Pawn(ms, board, true, Cell.parse("b6")));
        board.setFigure(new Pawn(ms, board, true, Cell.parse("d6")));
        Assert.assertEquals(
                toCellsSet("D7", "B7", "A8", "B5", "A4", "D5", "E4", "F3", "G2", "H1"),
                extractCellTo(queen.getAllMoves()));
    }

    @Test
    public void testKing() throws ChessException {
        //--- Король ---//
        var king1 = new King(ms, board, true, Cell.parse("e1"));
        var king2 = new King(ms, board, false, Cell.parse("e8"));
        board.setFigure(king1);
        board.setFigure(king2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));

        var rookW1 = new Rook(board, true, Cell.parse("h1"));
        var rookW2 = new Rook(board, true, Cell.parse("a1"));
        var rookB1 = new Rook(board, false, Cell.parse("h8"));
        var rookB2 = new Rook(board, false, Cell.parse("a8"));
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var pawnB1 = new Pawn(ms, board, false, Cell.parse("b1"));
        var pawnB2 = new Pawn(ms, board, false, Cell.parse("g1"));
        var pawnW1 = new Pawn(ms, board, true, Cell.parse("b8"));
        var pawnW2 = new Pawn(ms, board, true, Cell.parse("g8"));
        board.setFigure(pawnB1);
        board.setFigure(pawnB2);
        board.setFigure(pawnW1);
        board.setFigure(pawnW2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));
    }

    @Test
    public void testKingCastling_falseRook() throws ChessException {
        //--- Король ---//
        var king1 = new King(ms, board, true, Cell.parse("e1"));
        var king2 = new King(ms, board, false, Cell.parse("e8"));
        var knightW1 = new Knight(board, true, Cell.parse("h1"));
        var knightW2 = new Knight(board, true, Cell.parse("a1"));
        var knightB1 = new Knight(board, false, Cell.parse("h8"));
        var knightB2 = new Knight(board, false, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(knightW1);
        board.setFigure(knightW2);
        board.setFigure(knightB1);
        board.setFigure(knightB2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));
    }

    @Test
    public void testKingCastling_1() throws ChessException {
        //--- Король ---//
        var king1 = new King(ms, board, true, Cell.parse("e1"));
        var king2 = new King(ms, board, false, Cell.parse("e8"));
        var rookW1 = new Rook(board, true, Cell.parse("h1"));
        var rookW2 = new Rook(board, true, Cell.parse("a1"));
        var rookB1 = new Rook(board, false, Cell.parse("h8"));
        var rookB2 = new Rook(board, false, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        Assert.assertEquals(
                toCellsSet("C1", "D1", "D2", "E2", "F2", "F1", "G1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("C8", "D8", "D7", "E7", "F7", "F8", "G8"),
                extractCellTo(king2.getAllMoves()));

        rookW1.setWasMoved(true);
        rookW2.setWasMoved(true);
        rookB1.setWasMoved(true);
        rookB2.setWasMoved(true);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));
    }

    @Test
    public void testKingCastling_2() throws ChessException {
        //--- Король ---//
        var king1 = new King(ms, board, true, Cell.parse("e1"));
        var king2 = new King(ms, board, false, Cell.parse("e8"));
        var rookW1 = new Rook(board, true, Cell.parse("h1"));
        var rookW2 = new Rook(board, true, Cell.parse("a1"));
        var rookB1 = new Rook(board, false, Cell.parse("h8"));
        var rookB2 = new Rook(board, false, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        king1.setWasMoved(true);
        king2.setWasMoved(true);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));
    }

    @Test
    public void testKingCastling_3() throws ChessException {
        //--- Король ---//
        var king1 = new King(ms, board, true, Cell.parse("e1"));
        var king2 = new King(ms, board, false, Cell.parse("e8"));
        var rookW1 = new Rook(board, true, Cell.parse("h1"));
        var rookW2 = new Rook(board, true, Cell.parse("a1"));
        var rookB1 = new Rook(board, false, Cell.parse("h8"));
        var rookB2 = new Rook(board, false, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var FrookB = new Rook(board, false, Cell.parse("e4"));
        var FrookW = new Rook(board, true, Cell.parse("e5"));
        board.setFigure(FrookB);
        board.setFigure(FrookW);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));
    }

    @Test
    public void testKingCastling_4() throws ChessException {
        //--- Король ---//
        var king1 = new King(ms, board, true, Cell.parse("e1"));
        var king2 = new King(ms, board, false, Cell.parse("e8"));
        var rookW1 = new Rook(board, true, Cell.parse("h1"));
        var rookW2 = new Rook(board, true, Cell.parse("a1"));
        var rookB1 = new Rook(board, false, Cell.parse("h8"));
        var rookB2 = new Rook(board, false, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var FrookB1 = new Rook(board, false, Cell.parse("f4"));
        var FrookB2 = new Rook(board, false, Cell.parse("d4"));
        var FrookW1 = new Rook(board, true, Cell.parse("f5"));
        var FrookW2 = new Rook(board, true, Cell.parse("d5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));
    }

    @Test
    public void testKingCastling_5() throws ChessException {
        //--- Король ---//
        var king1 = new King(ms, board, true, Cell.parse("e1"));
        var king2 = new King(ms, board, false, Cell.parse("e8"));
        var rookW1 = new Rook(board, true, Cell.parse("h1"));
        var rookW2 = new Rook(board, true, Cell.parse("a1"));
        var rookB1 = new Rook(board, false, Cell.parse("h8"));
        var rookB2 = new Rook(board, false, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var FrookB1 = new Queen(board, false, Cell.parse("g4"));
        var FrookB2 = new Queen(board, false, Cell.parse("c4"));
        var FrookW1 = new Queen(board, true, Cell.parse("g5"));
        var FrookW2 = new Queen(board, true, Cell.parse("c5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves()));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves()));
    }

    @Test
    public void testKnight() throws ChessException {
        //--- Конь ---//
        var knight = new Knight(board, false, Cell.parse("f4"));
        board.setFigure(knight);
        Assert.assertEquals(
                toCellsSet("E6", "G6", "D5", "D3", "E2", "G2", "H3", "H5"),
                extractCellTo(knight.getAllMoves()));
    }

    @Test
    public void testKnightWithFriendPawns() throws ChessException {
        //--- Конь с дружественными пешками вокруг коня, но не закрывающие ход ---//
        Board testBoard = new Board();
        var knight = new Knight(testBoard, true, Cell.parse("a1"));
        var pawn1 = new Pawn(ms, testBoard, true, Cell.parse("a2"));
        var pawn2 = new Pawn(ms, testBoard, true, Cell.parse("b2"));
        var pawn3 = new Pawn(ms, testBoard, true, Cell.parse("b1"));

        testBoard.setFigure(knight);
        testBoard.setFigure(pawn1);
        testBoard.setFigure(pawn2);
        testBoard.setFigure(pawn3);
        Assert.assertEquals(
                toCellsSet("b3", "c2"),
                extractCellTo(knight.getAllMoves()));
    }

    @Test
    public void testPawn() throws ChessException {
        //--- Пешка ---//
        var pawn = new Pawn(ms, board, true, Cell.parse("c2"));
        var enemy = new Queen(board, false, Cell.parse("d3"));
        board.setFigure(pawn);
        board.setFigure(enemy);
        Assert.assertEquals(
                toCellsSet("C3", "C4", "D3"),
                extractCellTo(pawn.getAllMoves()));
    }

    @Test
    public void testPawnForEnemyRespawn() throws ChessException {
        //--- Пешка дошедшая до конца поля ---//
        var pawn = new Pawn(ms, board, false, Cell.parse("d1"));
        board.setFigure(pawn);
        Assert.assertEquals(
                new HashSet<Cell>(),
                extractCellTo(pawn.getAllMoves()));
    }

    @Test
    public void testPawnWithXEnemy() throws ChessException {
        //--- Пешка окружённая противниками по диагональным клеткам и с противником на пути ---//
        Board testBoard = new Board();
        var pawn = new Pawn(ms, testBoard, true, Cell.parse("c5"));
        var pawn1 = new Pawn(ms, testBoard, false, Cell.parse("b6"));
        var pawn2 = new Pawn(ms, testBoard, false, Cell.parse("d6"));
        var pawn3 = new Pawn(ms, testBoard, false, Cell.parse("b4"));
        var pawn4 = new Pawn(ms, testBoard, false, Cell.parse("d4"));
        var pawn5 = new Pawn(ms, testBoard, false, Cell.parse("c6"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(pawn1);
        testBoard.setFigure(pawn2);
        testBoard.setFigure(pawn3);
        testBoard.setFigure(pawn4);
        testBoard.setFigure(pawn5);
        Assert.assertEquals(
                toCellsSet("B6", "D6"),
                extractCellTo(pawn.getAllMoves()));
    }

    @Test
    public void testPawnWithXEnemy2() throws ChessException {
        //--- Пешка окружённая противниками по диагональным клеткам и с противником на пути ---//
        Board testBoard = new Board();
        var pawn = new Pawn(ms, testBoard, true, Cell.parse("c5"));
        var pawn1 = new Pawn(ms, testBoard, false, Cell.parse("b6"));
        var pawn2 = new Pawn(ms, testBoard, false, Cell.parse("d6"));
        var pawn3 = new Pawn(ms, testBoard, false, Cell.parse("b4"));
        var pawn4 = new Pawn(ms, testBoard, false, Cell.parse("d4"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(pawn1);
        testBoard.setFigure(pawn2);
        testBoard.setFigure(pawn3);
        testBoard.setFigure(pawn4);
        Assert.assertEquals(
                toCellsSet("B6", "D6", "c6"),
                extractCellTo(pawn.getAllMoves()));
    }

    @Test
    public void testPawnEnPassant() throws ChessException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(ms, board, true, white1.getTo());
        Field field = MoveSystem.class.getDeclaredField("prevMove");
        field.setAccessible(true);
        field.set(ms, white1);

        Figure figureB1 = new Pawn(ms, board, false, Cell.parse("b4"));
        Figure figureW2 = new Pawn(ms, board, true, Cell.parse("a4"));
        Figure figureW3 = new Pawn(ms, board, true, Cell.parse("b2"));

        board.setFigure(figureW1);
        board.setFigure(figureB1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);

        Assert.assertEquals(toCellsSet("b3", "c3"),
                extractCellTo(figureB1.getAllMoves()));
        Assert.assertEquals(toCellsSet("b3"),
                extractCellTo(figureW3.getAllMoves()));
    }

    private Set<Cell> toCellsSet(String... pos) {
        Objects.requireNonNull(pos, "Массив строк не может быть null");
        var result = new HashSet<Cell>();
        for (String p : pos) {
            result.add(Cell.parse(p));
        }
        return result;
    }

    private Set<Cell> extractCellTo(Set<Move> moves) {
        return moves.stream().map(Move::getTo).collect(Collectors.toSet());
    }
}
