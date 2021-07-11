package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FigureTest {

    private Board board;

    @Before
    public void setUp() throws ChessError {
        board = new Board(Board.BoardFilling.EMPTY);
    }

    @Test
    public void testBishop() throws ChessException {
        //--- Слон ---//

        var bishop1 = new Bishop(Color.WHITE, Cell.parse("e7"));
        board.setFigure(bishop1);

        Assert.assertEquals(
                toCellsSet("D8", "F8", "D6", "F6", "C5", "G5", "B4", "H4", "A3"),
                extractCellTo(bishop1.getAllMoves(board)));

        var bishop2 = new Bishop(Color.WHITE, Cell.parse("b3"));
        board.setFigure(bishop2);

        Assert.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8"),
                extractCellTo(bishop2.getAllMoves(board)));
    }

    @Test
    public void testBishopWithEnemyPawn() throws ChessException {
        //--- Слон с вражесткой пешкой ---//

        var pawn = new Pawn(Color.BLACK, Cell.parse("e3"));
        var bishop = new Bishop(Color.WHITE, Cell.parse("c1"));
        board.setFigure(pawn);
        board.setFigure(bishop);

        Assert.assertEquals(toCellsSet("A3", "B2", "D2", "E3"),
                extractCellTo(bishop.getAllMoves(board)));
    }

    @Test
    public void testBishopWithFriendPawn() throws ChessException {
        //--- Слон с дружеской пешкой ---//
        var pawn = new Pawn(Color.WHITE, Cell.parse("e3"));
        var bishop = new Bishop(Color.WHITE, Cell.parse("c1"));
        board.setFigure(pawn);
        board.setFigure(bishop);

        Assert.assertEquals(toCellsSet("A3", "B2", "D2"),
                extractCellTo(bishop.getAllMoves(board)));
    }

    @Test
    public void testRook() throws ChessException {
        //--- Ладья ---//
        var rook = new Rook(Color.BLACK, Cell.parse("a6"));
        board.setFigure(rook);
        Assert.assertEquals(
                toCellsSet("A8", "A7", "A5", "A4", "A3", "A2", "A1", "B6", "C6", "D6", "E6", "F6", "G6", "H6"),
                extractCellTo(rook.getAllMoves(board)));
    }

    @Test
    public void testCornerBlockedRook() throws ChessException {
        //--- Ладья в углу с противниками на пути---//
        var rook = new Rook(Color.WHITE, Cell.parse("a8"));
        var rook1 = new Rook(Color.BLACK, Cell.parse("a6"));
        var rook2 = new Rook(Color.BLACK, Cell.parse("c8"));
        board.setFigure(rook);
        board.setFigure(rook1);
        board.setFigure(rook2);
        Assert.assertEquals(
                toCellsSet("b8", "a7", "a6", "c8"),
                extractCellTo(rook.getAllMoves(board)));
    }

    @Test
    public void testQueen() throws ChessException {
        //--- Ферзь ---//
        var queen = new Queen(Color.BLACK, Cell.parse("b3"));
        board.setFigure(queen);
        Assert.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8", "B8", "B7", "B6", "B5",
                        "B4", "B2", "B1", "A3", "C3", "D3", "E3", "F3", "G3", "H3"),
                extractCellTo(queen.getAllMoves(board)));
    }

    @Test
    public void testQueen_jumpBlack() throws ChessException {
        //--- Ферзь ---//
        var queen = new Queen(Color.WHITE, Cell.parse("c6"));
        board.setFigure(queen);
        board.setFigure(new Queen(Color.BLACK, Cell.parse("d7")));
        board.setFigure(new King(Color.BLACK, Cell.parse("e8")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c5")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("b6")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("d6")));
        Assert.assertEquals(
                toCellsSet("D7", "B7", "A8", "B5", "A4", "D5", "E4", "F3", "G2", "H1"),
                extractCellTo(queen.getAllMoves(board)));
    }

    @Test
    public void testKing() throws ChessException {
        //--- Король ---//
        var king1 = new King(Color.WHITE, Cell.parse("e1"));
        var king2 = new King(Color.BLACK, Cell.parse("e8"));
        board.setFigure(king1);
        board.setFigure(king2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));

        var rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        var rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        var rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        var rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var pawnB1 = new Pawn(Color.BLACK, Cell.parse("b1"));
        var pawnB2 = new Pawn(Color.BLACK, Cell.parse("g1"));
        var pawnW1 = new Pawn(Color.WHITE, Cell.parse("b8"));
        var pawnW2 = new Pawn(Color.WHITE, Cell.parse("g8"));
        board.setFigure(pawnB1);
        board.setFigure(pawnB2);
        board.setFigure(pawnW1);
        board.setFigure(pawnW2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));
    }

    @Test
    public void testKingCastling_falseRook() throws ChessException {
        //--- Король ---//
        var king1 = new King(Color.WHITE, Cell.parse("e1"));
        var king2 = new King(Color.BLACK, Cell.parse("e8"));
        var knightW1 = new Knight(Color.WHITE, Cell.parse("h1"));
        var knightW2 = new Knight(Color.WHITE, Cell.parse("a1"));
        var knightB1 = new Knight(Color.BLACK, Cell.parse("h8"));
        var knightB2 = new Knight(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(knightW1);
        board.setFigure(knightW2);
        board.setFigure(knightB1);
        board.setFigure(knightB2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));
    }

    @Test
    public void testKingCastling_1() throws ChessException {
        //--- Король ---//
        var king1 = new King(Color.WHITE, Cell.parse("e1"));
        var king2 = new King(Color.BLACK, Cell.parse("e8"));
        var rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        var rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        var rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        var rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        Assert.assertEquals(
                toCellsSet("C1", "D1", "D2", "E2", "F2", "F1", "G1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("C8", "D8", "D7", "E7", "F7", "F8", "G8"),
                extractCellTo(king2.getAllMoves(board)));

        rookW1.setWasMoved(true);
        rookW2.setWasMoved(true);
        rookB1.setWasMoved(true);
        rookB2.setWasMoved(true);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));
    }

    @Test
    public void testKingCastling_2() throws ChessException {
        //--- Король ---//
        var king1 = new King(Color.WHITE, Cell.parse("e1"));
        var king2 = new King(Color.BLACK, Cell.parse("e8"));
        var rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        var rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        var rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        var rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
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
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));
    }

    @Test
    public void testKingCastling_3() throws ChessException {
        //--- Король ---//
        var king1 = new King(Color.WHITE, Cell.parse("e1"));
        var king2 = new King(Color.BLACK, Cell.parse("e8"));
        var rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        var rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        var rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        var rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var FrookB = new Rook(Color.BLACK, Cell.parse("e4"));
        var FrookW = new Rook(Color.WHITE, Cell.parse("e5"));
        board.setFigure(FrookB);
        board.setFigure(FrookW);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));
    }

    @Test
    public void testKingCastling_4() throws ChessException {
        //--- Король ---//
        var king1 = new King(Color.WHITE, Cell.parse("e1"));
        var king2 = new King(Color.BLACK, Cell.parse("e8"));
        var rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        var rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        var rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        var rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var FrookB1 = new Rook(Color.BLACK, Cell.parse("f4"));
        var FrookB2 = new Rook(Color.BLACK, Cell.parse("d4"));
        var FrookW1 = new Rook(Color.WHITE, Cell.parse("f5"));
        var FrookW2 = new Rook(Color.WHITE, Cell.parse("d5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));
    }

    @Test
    public void testKingCastling_5() throws ChessException {
        //--- Король ---//
        var king1 = new King(Color.WHITE, Cell.parse("e1"));
        var king2 = new King(Color.BLACK, Cell.parse("e8"));
        var rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        var rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        var rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        var rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        var FrookB1 = new Queen(Color.BLACK, Cell.parse("g4"));
        var FrookB2 = new Queen(Color.BLACK, Cell.parse("c4"));
        var FrookW1 = new Queen(Color.WHITE, Cell.parse("g5"));
        var FrookW2 = new Queen(Color.WHITE, Cell.parse("c5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toCellsSet("D1", "D2", "E2", "F2", "F1"),
                extractCellTo(king1.getAllMoves(board)));
        Assert.assertEquals(
                toCellsSet("D8", "D7", "E7", "F7", "F8"),
                extractCellTo(king2.getAllMoves(board)));
    }

    @Test
    public void testKnight() throws ChessException {
        //--- Конь ---//
        var knight = new Knight(Color.BLACK, Cell.parse("f4"));
        board.setFigure(knight);
        Assert.assertEquals(
                toCellsSet("E6", "G6", "D5", "D3", "E2", "G2", "H3", "H5"),
                extractCellTo(knight.getAllMoves(board)));
    }

    @Test
    public void testKnightWithFriendPawns() throws ChessException {
        //--- Конь с дружественными пешками вокруг коня, но не закрывающие ход ---//
        var knight = new Knight(Color.WHITE, Cell.parse("a1"));
        var pawn1 = new Pawn(Color.WHITE, Cell.parse("a2"));
        var pawn2 = new Pawn(Color.WHITE, Cell.parse("b2"));
        var pawn3 = new Pawn(Color.WHITE, Cell.parse("b1"));

        board.setFigure(knight);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        Assert.assertEquals(
                toCellsSet("b3", "c2"),
                extractCellTo(knight.getAllMoves(board)));
    }

    @Test
    public void testPawn() throws ChessException {
        //--- Пешка ---//
        var pawn = new Pawn(Color.WHITE, Cell.parse("c2"));
        var enemy = new Queen(Color.BLACK, Cell.parse("d3"));
        board.setFigure(pawn);
        board.setFigure(enemy);

        Assert.assertEquals(
                toCellsSet("C3", "C4", "D3"),
                extractCellTo(pawn.getAllMoves(board)));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("c3")));

        Assert.assertEquals(
                toCellsSet("D3"),
                extractCellTo(pawn.getAllMoves(board)));
    }

    @Test
    public void testPawnForEnemyRespawn() throws ChessException {
        //--- Пешка дошедшая до конца поля ---//
        var pawn = new Pawn(Color.BLACK, Cell.parse("d1"));
        board.setFigure(pawn);
        Assert.assertEquals(
                new HashSet<Cell>(),
                extractCellTo(pawn.getAllMoves(board)));
    }

    @Test
    public void testPawnWithXEnemy() throws ChessException {
        //--- Пешка окружённая противниками по диагональным клеткам и с противником на пути ---//
        var pawn = new Pawn(Color.WHITE, Cell.parse("c5"));
        var pawn1 = new Pawn(Color.BLACK, Cell.parse("b6"));
        var pawn2 = new Pawn(Color.BLACK, Cell.parse("d6"));
        var pawn3 = new Pawn(Color.BLACK, Cell.parse("b4"));
        var pawn4 = new Pawn(Color.BLACK, Cell.parse("d4"));
        var pawn5 = new Pawn(Color.BLACK, Cell.parse("c6"));
        board.setFigure(pawn);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);
        board.setFigure(pawn5);
        Assert.assertEquals(
                toCellsSet("B6", "D6"),
                extractCellTo(pawn.getAllMoves(board)));
    }

    @Test
    public void testPawnWithXEnemy2() throws ChessException {
        //--- Пешка окружённая противниками по диагональным клеткам и с противником на пути ---//
        var pawn = new Pawn(Color.WHITE, Cell.parse("c5"));
        var pawn1 = new Pawn(Color.BLACK, Cell.parse("b6"));
        var pawn2 = new Pawn(Color.BLACK, Cell.parse("d6"));
        var pawn3 = new Pawn(Color.BLACK, Cell.parse("b4"));
        var pawn4 = new Pawn(Color.BLACK, Cell.parse("d4"));
        board.setFigure(pawn);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);
        Assert.assertEquals(
                toCellsSet("B6", "D6", "c6"),
                extractCellTo(pawn.getAllMoves(board)));
    }

    @Test
    public void testPawnEnPassant() throws ChessException, IllegalArgumentException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(Color.WHITE, white1.getTo());

        board = Mockito.spy(board);
        Mockito.when(board.getPrevMove()).thenReturn(white1);

        Figure figureB1 = new Pawn(Color.BLACK, Cell.parse("b4"));
        Figure figureW2 = new Pawn(Color.WHITE, Cell.parse("a4"));
        Figure figureW3 = new Pawn(Color.WHITE, Cell.parse("b2"));

        board.setFigure(figureW1);
        board.setFigure(figureB1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);

        Assert.assertEquals(toCellsSet("b3", "c3"),
                extractCellTo(figureB1.getAllMoves(board)));
        Assert.assertEquals(toCellsSet("b3"),
                extractCellTo(figureW3.getAllMoves(board)));
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