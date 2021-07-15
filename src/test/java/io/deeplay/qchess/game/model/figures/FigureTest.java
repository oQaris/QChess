package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FigureTest {
    private GameSettings gameSettings;
    private Board board;

    private static Set<Move> toMoveSet(Cell from, MoveType type, String... tos)
            throws ChessException {
        Set<Move> result = new HashSet<>(tos.length);
        for (String to : tos) result.add(new Move(type, from, Cell.parse(to)));
        return result;
    }

    @Before
    public void setUp() {
        gameSettings = new GameSettings(Board.BoardFilling.EMPTY);
        board = gameSettings.board;
    }

    @Test
    public void testBishopQuiet() throws ChessException {
        // --- Чёрный --- //
        Cell from1 = Cell.parse("e7");
        Figure bishop1 = new Bishop(Color.BLACK, from1);
        board.setFigure(bishop1);
        Assert.assertEquals(
                toMoveSet(
                        from1,
                        MoveType.QUIET_MOVE,
                        "D8",
                        "F8",
                        "D6",
                        "F6",
                        "C5",
                        "G5",
                        "B4",
                        "H4",
                        "A3"),
                bishop1.getAllMoves(gameSettings));

        // --- Белый --- //
        Cell from2 = Cell.parse("b3");
        Figure bishop2 = new Bishop(Color.WHITE, from2);
        board.setFigure(bishop2);
        Assert.assertEquals(
                toMoveSet(
                        from2,
                        MoveType.QUIET_MOVE,
                        "A4",
                        "C2",
                        "D1",
                        "A2",
                        "C4",
                        "D5",
                        "E6",
                        "F7",
                        "G8"),
                bishop2.getAllMoves(gameSettings));

        // --- Угловой --- //
        Cell from3 = Cell.parse("h1");
        Figure bishop3 = new Bishop(Color.BLACK, from3);
        board.setFigure(bishop3);
        Assert.assertEquals(
                toMoveSet(from3, MoveType.QUIET_MOVE, "A8", "B7", "C6", "D5", "E4", "F3", "G2"),
                bishop3.getAllMoves(gameSettings));
    }

    @Test
    public void testBishopWithPawn() throws ChessException {
        Cell from = Cell.parse("c1");
        Figure bishop = new Bishop(Color.WHITE, from);
        board.setFigure(bishop);

        // --- Слон с вражесткой пешкой --- //
        Figure enemyPawn = new Pawn(Color.BLACK, Cell.parse("e3"));
        board.setFigure(enemyPawn);

        Set<Move> expected = toMoveSet(from, MoveType.QUIET_MOVE, "A3", "B2", "D2");
        expected.addAll(toMoveSet(from, MoveType.ATTACK, "E3"));

        Assert.assertEquals(expected, bishop.getAllMoves(gameSettings));

        // --- Слон с дружеской пешкой --- //
        Figure friendPawn = new Pawn(Color.WHITE, Cell.parse("a3"));
        board.setFigure(friendPawn);
        expected.remove(new Move(MoveType.QUIET_MOVE, from, Cell.parse("a3")));

        Assert.assertEquals(expected, bishop.getAllMoves(gameSettings));
    }

    @Test
    public void testRook() throws ChessException {
        // --- Ладья Свободная --- //
        Cell from = Cell.parse("a6");
        Figure rook = new Rook(Color.BLACK, from);
        board.setFigure(rook);
        Assert.assertEquals(
                toMoveSet(
                        from,
                        MoveType.QUIET_MOVE,
                        "A8",
                        "A7",
                        "A5",
                        "A4",
                        "A3",
                        "A2",
                        "A1",
                        "B6",
                        "C6",
                        "D6",
                        "E6",
                        "F6",
                        "G6",
                        "H6"),
                rook.getAllMoves(gameSettings));
    }

    @Test
    public void testCornerBlockedRook() throws ChessException {
        // --- Ладья в углу с противниками на пути --- //
        Cell from = Cell.parse("a8");
        Figure rook = new Rook(Color.WHITE, from);
        board.setFigure(rook);

        Figure enemyRook1 = new Rook(Color.BLACK, Cell.parse("a6"));
        Figure enemyRook2 = new Rook(Color.BLACK, Cell.parse("c8"));
        board.setFigure(enemyRook1);
        board.setFigure(enemyRook2);

        Set<Move> expected = toMoveSet(from, MoveType.QUIET_MOVE, "b8", "a7");
        expected.addAll(toMoveSet(from, MoveType.ATTACK, "a6", "c8"));

        Assert.assertEquals(expected, rook.getAllMoves(gameSettings));
    }

    @Test
    public void testQueen() throws ChessException {
        // --- Ферзь --- //
        Cell from = Cell.parse("b3");
        Figure queen = new Queen(Color.BLACK, from);
        board.setFigure(queen);
        Assert.assertEquals(
                toMoveSet(
                        from,
                        MoveType.QUIET_MOVE,
                        "A4",
                        "C2",
                        "D1",
                        "A2",
                        "C4",
                        "D5",
                        "E6",
                        "F7",
                        "G8",
                        "B8",
                        "B7",
                        "B6",
                        "B5",
                        "B4",
                        "B2",
                        "B1",
                        "A3",
                        "C3",
                        "D3",
                        "E3",
                        "F3",
                        "G3",
                        "H3"),
                queen.getAllMoves(gameSettings));
    }

    @Test
    public void testQueenJumpBlack() throws ChessException {
        // --- Ферзь --- //
        Cell from = Cell.parse("c6");
        Figure queen = new Queen(Color.WHITE, from);
        board.setFigure(queen);
        board.setFigure(new Queen(Color.BLACK, Cell.parse("d7")));
        board.setFigure(new King(Color.BLACK, Cell.parse("e8")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c5")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("b6")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("d6")));
        board.setFigure(new Pawn(Color.BLACK, Cell.parse("f3")));

        Set<Move> expected =
                toMoveSet(from, MoveType.QUIET_MOVE, "B7", "A8", "B5", "A4", "D5", "E4");
        expected.addAll(toMoveSet(from, MoveType.ATTACK, "F3", "D7"));

        Assert.assertEquals(expected, queen.getAllMoves(gameSettings));
    }

    @Test
    public void testKing() throws ChessException {
        // --- Король --- //
        Cell from1 = Cell.parse("e1");
        Figure king1 = new King(Color.WHITE, from1);
        Cell from2 = Cell.parse("e8");
        Figure king2 = new King(Color.BLACK, from2);
        board.setFigure(king1);
        board.setFigure(king2);

        Assert.assertEquals(
                toMoveSet(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8"),
                king2.getAllMoves(gameSettings));

        // --- Рокировка --- //
        Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        Set<Move> expected1 = toMoveSet(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1");
        expected1.addAll(toMoveSet(from1, MoveType.SHORT_CASTLING, "G1"));
        expected1.addAll(toMoveSet(from1, MoveType.LONG_CASTLING, "C1"));

        Set<Move> expected2 = toMoveSet(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8");
        expected2.addAll(toMoveSet(from2, MoveType.SHORT_CASTLING, "G8"));
        expected2.addAll(toMoveSet(from2, MoveType.LONG_CASTLING, "C8"));

        Assert.assertEquals(expected1, king1.getAllMoves(gameSettings));
        Assert.assertEquals(expected2, king2.getAllMoves(gameSettings));

        // --- Уже низя рокировку --- //
        Figure pawnB1 = new Pawn(Color.BLACK, Cell.parse("b1"));
        Figure pawnB2 = new Pawn(Color.BLACK, Cell.parse("g1"));
        Figure pawnW1 = new Pawn(Color.WHITE, Cell.parse("b8"));
        Figure pawnW2 = new Pawn(Color.WHITE, Cell.parse("g8"));
        board.setFigure(pawnB1);
        board.setFigure(pawnB2);
        board.setFigure(pawnW1);
        board.setFigure(pawnW2);

        Assert.assertEquals(
                toMoveSet(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8"),
                king2.getAllMoves(gameSettings));
    }

    @Test
    public void testKingCastlingFalseRook() throws ChessException {
        // --- Король ---//
        Cell from1 = Cell.parse("e1");
        Figure king1 = new King(Color.WHITE, from1);
        Cell from2 = Cell.parse("e8");
        Figure king2 = new King(Color.BLACK, from2);
        Figure knightW1 = new Knight(Color.WHITE, Cell.parse("h1"));
        Figure knightW2 = new Knight(Color.WHITE, Cell.parse("a1"));
        Figure knightB1 = new Knight(Color.BLACK, Cell.parse("h8"));
        Figure knightB2 = new Knight(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(knightW1);
        board.setFigure(knightW2);
        board.setFigure(knightB1);
        board.setFigure(knightB2);

        Assert.assertEquals(
                toMoveSet(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8"),
                king2.getAllMoves(gameSettings));
    }

    @Test
    public void testKingCastling1() throws ChessException {
        // --- Король ---//
        Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        Set<Move> expected1 =
                toMoveSet(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1");
        expected1.add(
                new Move(MoveType.LONG_CASTLING, king1.getCurrentPosition(), Cell.parse("C1")));
        expected1.add(
                new Move(MoveType.SHORT_CASTLING, king1.getCurrentPosition(), Cell.parse("G1")));
        Assert.assertEquals(expected1, king1.getAllMoves(gameSettings));

        Set<Move> expected2 =
                toMoveSet(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8");
        expected2.add(
                new Move(MoveType.LONG_CASTLING, king2.getCurrentPosition(), Cell.parse("C8")));
        expected2.add(
                new Move(MoveType.SHORT_CASTLING, king2.getCurrentPosition(), Cell.parse("G8")));
        Assert.assertEquals(expected2, king2.getAllMoves(gameSettings));

        rookW1.setWasMoved(true);
        rookW2.setWasMoved(true);
        rookB1.setWasMoved(true);
        rookB2.setWasMoved(true);

        Assert.assertEquals(
                toMoveSet(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                king2.getAllMoves(gameSettings));
    }

    @Test
    public void testKingCastling2() throws ChessException {
        // --- Король ---//
        Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        king1.setWasMoved(true);
        king2.setWasMoved(true);

        Assert.assertEquals(
                toMoveSet(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                king2.getAllMoves(gameSettings));
    }

    @Test
    public void testKingCastling3() throws ChessException {
        // --- Король ---//
        Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        Figure FrookB = new Rook(Color.BLACK, Cell.parse("e4"));
        Figure FrookW = new Rook(Color.WHITE, Cell.parse("e5"));
        board.setFigure(FrookB);
        board.setFigure(FrookW);

        Assert.assertEquals(
                toMoveSet(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                king2.getAllMoves(gameSettings));
    }

    @Test
    public void testKingCastling4() throws ChessException {
        // --- Король ---//
        Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        Figure FrookB1 = new Rook(Color.BLACK, Cell.parse("f4"));
        Figure FrookB2 = new Rook(Color.BLACK, Cell.parse("d4"));
        Figure FrookW1 = new Rook(Color.WHITE, Cell.parse("f5"));
        Figure FrookW2 = new Rook(Color.WHITE, Cell.parse("d5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toMoveSet(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                king2.getAllMoves(gameSettings));
    }

    @Test
    public void testKingCastling5() throws ChessException {
        // --- Король ---//
        Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        Figure FrookB1 = new Queen(Color.BLACK, Cell.parse("g4"));
        Figure FrookB2 = new Queen(Color.BLACK, Cell.parse("c4"));
        Figure FrookW1 = new Queen(Color.WHITE, Cell.parse("g5"));
        Figure FrookW2 = new Queen(Color.WHITE, Cell.parse("c5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toMoveSet(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                king1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                king2.getAllMoves(gameSettings));
    }

    @Test
    public void testKnight() throws ChessException {
        // --- Конь --- //
        Figure knight = new Knight(Color.BLACK, Cell.parse("f4"));
        board.setFigure(knight);
        Assert.assertEquals(
                toMoveSet(
                        knight.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "E6",
                        "G6",
                        "D5",
                        "D3",
                        "E2",
                        "G2",
                        "H3",
                        "H5"),
                knight.getAllMoves(gameSettings));
    }

    @Test
    public void testKnightWithFriendPawns() throws ChessException {
        // --- Конь с дружественными пешками вокруг коня, но не закрывающие ход --- //
        Figure knight = new Knight(Color.WHITE, Cell.parse("a1"));
        Figure pawn1 = new Pawn(Color.WHITE, Cell.parse("a2"));
        Figure pawn2 = new Pawn(Color.WHITE, Cell.parse("b2"));
        Figure pawn3 = new Pawn(Color.WHITE, Cell.parse("b1"));

        board.setFigure(knight);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        Assert.assertEquals(
                toMoveSet(knight.getCurrentPosition(), MoveType.QUIET_MOVE, "b3", "c2"),
                knight.getAllMoves(gameSettings));
    }

    @Test
    public void testPawn() throws ChessException {
        // --- Пешка --- //
        Figure pawn = new Pawn(Color.WHITE, Cell.parse("c2"));
        Figure enemy = new Queen(Color.BLACK, Cell.parse("d3"));
        board.setFigure(pawn);
        board.setFigure(enemy);

        Set<Move> expected = toMoveSet(pawn.getCurrentPosition(), MoveType.QUIET_MOVE, "C3");
        expected.add(new Move(MoveType.LONG_MOVE, pawn.getCurrentPosition(), Cell.parse("C4")));
        expected.add(new Move(MoveType.ATTACK, pawn.getCurrentPosition(), Cell.parse("D3")));

        Assert.assertEquals(expected, pawn.getAllMoves(gameSettings));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("c3")));

        Assert.assertEquals(
                toMoveSet(pawn.getCurrentPosition(), MoveType.ATTACK, "D3"),
                pawn.getAllMoves(gameSettings));
    }

    @Test
    public void testPawnForEnemyRespawn() throws ChessException {
        // --- Пешка дошедшая до конца поля --- //
        Figure pawn = new Pawn(Color.BLACK, Cell.parse("d1"));
        board.setFigure(pawn);
        Assert.assertEquals(Set.of(), pawn.getAllMoves(gameSettings));
    }

    @Test
    public void testPawnWithXEnemy() throws ChessException {
        // --- Пешка окружённая противниками по диагональным клеткам и с противником на пути --- //
        Figure pawn = new Pawn(Color.WHITE, Cell.parse("c5"));
        Figure pawn1 = new Pawn(Color.BLACK, Cell.parse("b6"));
        Figure pawn2 = new Pawn(Color.BLACK, Cell.parse("d6"));
        Figure pawn3 = new Pawn(Color.BLACK, Cell.parse("b4"));
        Figure pawn4 = new Pawn(Color.BLACK, Cell.parse("d4"));
        Figure pawn5 = new Pawn(Color.BLACK, Cell.parse("c6"));
        board.setFigure(pawn);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);
        board.setFigure(pawn5);
        Assert.assertEquals(
                toMoveSet(pawn.getCurrentPosition(), MoveType.ATTACK, "B6", "D6"),
                pawn.getAllMoves(gameSettings));
    }

    @Test
    public void testPawnWithXEnemy2() throws ChessException {
        // --- Пешка окружённая противниками по диагональным клеткам и с противником на пути --- //
        Figure pawn = new Pawn(Color.WHITE, Cell.parse("c5"));
        Figure pawn1 = new Pawn(Color.BLACK, Cell.parse("b6"));
        Figure pawn2 = new Pawn(Color.BLACK, Cell.parse("d6"));
        Figure pawn3 = new Pawn(Color.BLACK, Cell.parse("b4"));
        Figure pawn4 = new Pawn(Color.BLACK, Cell.parse("d4"));
        pawn.setWasMoved(true);
        board.setFigure(pawn);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);

        Set<Move> expected = toMoveSet(pawn.getCurrentPosition(), MoveType.ATTACK, "B6", "D6");
        expected.add(new Move(MoveType.QUIET_MOVE, pawn.getCurrentPosition(), Cell.parse("c6")));

        Assert.assertEquals(expected, pawn.getAllMoves(gameSettings));
    }

    @Test
    public void testPawnWithEndOfBoard()
            throws ChessException, NoSuchFieldException, IllegalAccessException {
        // --- Пешки стоящие у краев доски --- //
        Figure pawn1 = new Pawn(Color.BLACK, Cell.parse("a4"));
        Figure pawn2 = new Pawn(Color.BLACK, Cell.parse("h4"));
        Figure pawn3 = new Pawn(Color.WHITE, Cell.parse("b4"));
        Figure pawn4 = new Pawn(Color.WHITE, Cell.parse("g4"));
        pawn1.setWasMoved(true);
        pawn2.setWasMoved(true);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);
        setPrevMove(new Move(MoveType.LONG_MOVE, Cell.parse("b2"), Cell.parse("b4")));

        Set<Move> expected = toMoveSet(pawn1.getCurrentPosition(), MoveType.EN_PASSANT, "b3");
        expected.add(new Move(MoveType.QUIET_MOVE, pawn1.getCurrentPosition(), Cell.parse("a3")));

        Assert.assertEquals(expected, pawn1.getAllMoves(gameSettings));
        setPrevMove(new Move(MoveType.LONG_MOVE, Cell.parse("g2"), Cell.parse("g4")));

        expected = toMoveSet(pawn2.getCurrentPosition(), MoveType.EN_PASSANT, "g3");
        expected.add(new Move(MoveType.QUIET_MOVE, pawn2.getCurrentPosition(), Cell.parse("h3")));

        Assert.assertEquals(expected, pawn2.getAllMoves(gameSettings));
    }

    private void setPrevMove(Move move) throws NoSuchFieldException, IllegalAccessException {
        Field prevMove = gameSettings.history.getClass().getDeclaredField("lastMove");
        prevMove.setAccessible(true);
        prevMove.set(gameSettings.history, move);
    }

    @Test
    public void testPawnEnPassant()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        Figure figureW1 = new Pawn(Color.WHITE, white1.getTo());

        setPrevMove(white1);

        Figure figureB1 = new Pawn(Color.BLACK, Cell.parse("b4"));
        Figure figureW2 = new Pawn(Color.WHITE, Cell.parse("a4"));
        Figure figureW3 = new Pawn(Color.WHITE, Cell.parse("b2"));

        board.setFigure(figureW1);
        board.setFigure(figureB1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);

        Set<Move> expected = toMoveSet(figureB1.getCurrentPosition(), MoveType.QUIET_MOVE, "b3");
        expected.add(
                new Move(MoveType.EN_PASSANT, figureB1.getCurrentPosition(), Cell.parse("c3")));

        Assert.assertEquals(expected, figureB1.getAllMoves(gameSettings));
        Assert.assertEquals(
                toMoveSet(figureW3.getCurrentPosition(), MoveType.QUIET_MOVE, "b3"),
                figureW3.getAllMoves(gameSettings));
    }
}
