package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class FigureTest {

    private Board board;

    @Before
    public void setUp() throws Exception {
        board = new Board();
    }

    @Test
    public void testBishop() throws ChessException {
        //--- Слон ---//

        var bishop1 = new Bishop(board, true, Cell.parse("e7"));
        board.setFigure(bishop1);

        Assert.assertEquals(
                toCellsSet("D8", "F8", "D6", "F6", "C5", "G5", "B4", "H4", "A3"),
                bishop1.getAllMovePositions());

        var bishop2 = new Bishop(board, true, Cell.parse("b3"));
        board.setFigure(bishop2);

        Assert.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8"),
                bishop2.getAllMovePositions());
    }

    @Test
    public void testBishopWithEnemyPawn() throws ChessException {
        //--- Слон с вражесткой пешкой ---//

        Board testBoard = new Board();
        var pawn = new Pawn(testBoard, false, Cell.parse("e3"));
        var bishop = new Bishop(testBoard, true, Cell.parse("c1"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(bishop);

        Assert.assertEquals(toCellsSet("A3", "B2", "D2", "E3"),
                bishop.getAllMovePositions());
    }

    @Test
    public void testBishopWithFriendPawn() throws ChessException {
        //--- Слон с дружеской пешкой ---//
        Board testBoard = new Board();
        var pawn = new Pawn(testBoard, true, Cell.parse("e3"));
        var bishop = new Bishop(testBoard, true, Cell.parse("c1"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(bishop);

        Assert.assertEquals(toCellsSet("A3", "B2", "D2"),
                bishop.getAllMovePositions());
    }

    @Test
    public void testRook() throws ChessException {
        //--- Ладья ---//
        var rook = new Rook(board, false, Cell.parse("a6"));
        board.setFigure(rook);
        Assert.assertEquals(
                toCellsSet("A8", "A7", "A5", "A4", "A3", "A2", "A1", "B6", "C6", "D6", "E6", "F6", "G6", "H6"),
                rook.getAllMovePositions());
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
                rook.getAllMovePositions());
    }

    @Test
    public void testQueen() throws ChessException {
        //--- Ферзь ---//
        var queen = new Queen(board, false, Cell.parse("b3"));
        board.setFigure(queen);
        Assert.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8", "B8", "B7", "B6", "B5",
                        "B4", "B2", "B1", "A3", "C3", "D3", "E3", "F3", "G3", "H3"),
                queen.getAllMovePositions());
    }

    @Test
    public void testKing() throws ChessException {
        //--- Король ---//
        var king = new King(board, false, Cell.parse("e1"));
        board.setFigure(king);
        Assert.assertEquals(
                toCellsSet(/*"B1", */"D1", "D2", "E2", "F2", "F1" /*"G1"*/),
                king.getAllMovePositions());
    }

    @Test
    public void testKnight() throws ChessException {
        //--- Конь ---//
        var knight = new Knight(board, false, Cell.parse("f4"));
        board.setFigure(knight);
        Assert.assertEquals(
                toCellsSet("E6", "G6", "D5", "D3", "E2", "G2", "H3", "H5"),
                knight.getAllMovePositions());
    }

    @Test
    public void testKnightWithFriendPawns() throws ChessException {
        //--- Конь с дружественными пешками вокруг коня, но не закрывающие ход ---//
        Board testBoard = new Board();
        var knight = new Knight(testBoard, true, Cell.parse("a1"));
        var pawn1 = new Pawn(testBoard, true, Cell.parse("a2"));
        var pawn2 = new Pawn(testBoard, true, Cell.parse("b2"));
        var pawn3 = new Pawn(testBoard, true, Cell.parse("b1"));

        testBoard.setFigure(knight);
        testBoard.setFigure(pawn1);
        testBoard.setFigure(pawn2);
        testBoard.setFigure(pawn3);
        Assert.assertEquals(
                toCellsSet("b3", "c2"),
                knight.getAllMovePositions());
    }

    @Test
    public void testPawn() throws ChessException {
        //--- Пешка ---//
        var pawn = new Pawn(board, true, Cell.parse("c2"));
        var enemy = new Queen(board, false, Cell.parse("d3"));
        board.setFigure(pawn);
        board.setFigure(enemy);
        Assert.assertEquals(
                toCellsSet("C3", "C4", "D3"),
                pawn.getAllMovePositions());
    }

    @Test
    public void testPawnForEnemyRespawn() throws ChessException {
        //--- Пешка дошедшая до конца поля ---//
        var pawn = new Pawn(board, false, Cell.parse("d1"));
        board.setFigure(pawn);
        Assert.assertEquals(
                new HashSet<Cell>(),
                pawn.getAllMovePositions());
    }

    @Test
    public void testPawnWithXEnemy() throws ChessException {
        //--- Пешка окружённая противниками по диагональным клеткам и с противником на пути ---//
        Board testBoard = new Board();
        var pawn = new Pawn(testBoard, true, Cell.parse("c5"));
        pawn.madeFirstMove();
        var pawn1 = new Pawn(testBoard, false, Cell.parse("b6"));
        var pawn2 = new Pawn(testBoard, false, Cell.parse("d6"));
        var pawn3 = new Pawn(testBoard, false, Cell.parse("b4"));
        var pawn4 = new Pawn(testBoard, false, Cell.parse("d4"));
        var pawn5 = new Pawn(testBoard, false, Cell.parse("c6"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(pawn1);
        testBoard.setFigure(pawn2);
        testBoard.setFigure(pawn3);
        testBoard.setFigure(pawn4);
        testBoard.setFigure(pawn5);
        Assert.assertEquals(
                toCellsSet("B6", "D6"),
                pawn.getAllMovePositions());
    }

    @Test
    public void testPawnWithXEnemy2() throws ChessException {
        //--- Пешка окружённая противниками по диагональным клеткам и с противником на пути ---//
        Board testBoard = new Board();
        var pawn = new Pawn(testBoard, true, Cell.parse("c5"));
        pawn.madeFirstMove();
        var pawn1 = new Pawn(testBoard, false, Cell.parse("b6"));
        var pawn2 = new Pawn(testBoard, false, Cell.parse("d6"));
        var pawn3 = new Pawn(testBoard, false, Cell.parse("b4"));
        var pawn4 = new Pawn(testBoard, false, Cell.parse("d4"));
        testBoard.setFigure(pawn);
        testBoard.setFigure(pawn1);
        testBoard.setFigure(pawn2);
        testBoard.setFigure(pawn3);
        testBoard.setFigure(pawn4);
        Assert.assertEquals(
                toCellsSet("B6", "D6", "c6"),
                pawn.getAllMovePositions());
    }

    private Set<Cell> toCellsSet(String... pos) {
        if (pos == null) {
            throw new NullPointerException("Массив строк не может быть null");
        }
        var result = new HashSet<Cell>();
        for (String p : pos) {
            result.add(Cell.parse(p));
        }
        return result;
    }
}
