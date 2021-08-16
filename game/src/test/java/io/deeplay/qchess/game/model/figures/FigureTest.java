package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FigureTest {
    private GameSettings gameSettings;
    private Board board;

    private static List<Move> toMoveList(
            final Cell from, final MoveType type, final String... tos) {
        final List<Move> result = new ArrayList<>(tos.length);
        for (final String to : tos) result.add(new Move(type, from, Cell.parse(to)));
        sort(result);
        return result;
    }

    private static List<Move> sort(final List<Move> moves) {
        moves.sort(
                (m1, m2) -> {
                    if (m1.getFrom().column == m2.getFrom().column) {
                        if (m1.getFrom().row == m2.getFrom().row) {
                            if (m1.getTo().column == m2.getTo().column) {
                                return m1.getTo().row - m2.getTo().row;
                            }
                            return m1.getTo().column - m2.getTo().column;
                        }
                        return m1.getFrom().row - m2.getFrom().row;
                    }
                    return m1.getFrom().column - m2.getFrom().column;
                });
        return moves;
    }

    @Before
    public void setUp() {
        gameSettings = new GameSettings(Board.BoardFilling.EMPTY);
        board = gameSettings.board;
    }

    @Test
    public void testBishopQuiet() throws ChessException {
        // --- Чёрный --- //
        final Cell from1 = Cell.parse("e7");
        final Figure bishop1 = new Bishop(Color.BLACK, from1);
        board.setFigure(bishop1);
        Assert.assertEquals(
                toMoveList(
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
                sort(bishop1.getAllMoves(gameSettings)));

        // --- Белый --- //
        final Cell from2 = Cell.parse("b3");
        final Figure bishop2 = new Bishop(Color.WHITE, from2);
        board.setFigure(bishop2);
        Assert.assertEquals(
                toMoveList(
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
                sort(bishop2.getAllMoves(gameSettings)));

        // --- Угловой --- //
        final Cell from3 = Cell.parse("h1");
        final Figure bishop3 = new Bishop(Color.BLACK, from3);
        board.setFigure(bishop3);
        Assert.assertEquals(
                toMoveList(from3, MoveType.QUIET_MOVE, "A8", "B7", "C6", "D5", "E4", "F3", "G2"),
                sort(bishop3.getAllMoves(gameSettings)));
    }

    @Test
    public void testBishopWithPawn() throws ChessException {
        final Cell from = Cell.parse("c1");
        final Figure bishop = new Bishop(Color.WHITE, from);
        board.setFigure(bishop);

        // --- Слон с вражесткой пешкой --- //
        final Figure enemyPawn = new Pawn(Color.BLACK, Cell.parse("e3"));
        board.setFigure(enemyPawn);

        final List<Move> expected = toMoveList(from, MoveType.QUIET_MOVE, "A3", "B2", "D2");
        expected.addAll(toMoveList(from, MoveType.ATTACK, "E3"));

        Assert.assertEquals(expected, sort(bishop.getAllMoves(gameSettings)));

        // --- Слон с дружеской пешкой --- //
        final Figure friendPawn = new Pawn(Color.WHITE, Cell.parse("a3"));
        board.setFigure(friendPawn);
        expected.remove(new Move(MoveType.QUIET_MOVE, from, Cell.parse("a3")));

        Assert.assertEquals(expected, sort(bishop.getAllMoves(gameSettings)));
    }

    @Test
    public void testRook() throws ChessException {
        // --- Ладья Свободная --- //
        final Cell from = Cell.parse("a6");
        final Figure rook = new Rook(Color.BLACK, from);
        board.setFigure(rook);
        Assert.assertEquals(
                toMoveList(
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
                sort(rook.getAllMoves(gameSettings)));
    }

    @Test
    public void testCornerBlockedRook() throws ChessException {
        // --- Ладья в углу с противниками на пути --- //
        final Cell from = Cell.parse("a8");
        final Figure rook = new Rook(Color.WHITE, from);
        board.setFigure(rook);

        final Figure enemyRook1 = new Rook(Color.BLACK, Cell.parse("a6"));
        final Figure enemyRook2 = new Rook(Color.BLACK, Cell.parse("c8"));
        board.setFigure(enemyRook1);
        board.setFigure(enemyRook2);

        final List<Move> expected = toMoveList(from, MoveType.QUIET_MOVE, "b8", "a7");
        expected.addAll(toMoveList(from, MoveType.ATTACK, "a6", "c8"));

        Assert.assertEquals(sort(expected), sort(rook.getAllMoves(gameSettings)));
    }

    @Test
    public void testQueen() throws ChessException {
        // --- Ферзь --- //
        final Cell from = Cell.parse("b3");
        final Figure queen = new Queen(Color.BLACK, from);
        board.setFigure(queen);
        Assert.assertEquals(
                toMoveList(
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
                sort(queen.getAllMoves(gameSettings)));
    }

    @Test
    public void testQueenJumpBlack() throws ChessException {
        // --- Ферзь --- //
        final Cell from = Cell.parse("c6");
        final Figure queen = new Queen(Color.WHITE, from);
        board.setFigure(queen);
        board.setFigure(new Queen(Color.BLACK, Cell.parse("d7")));
        board.setFigure(new King(Color.BLACK, Cell.parse("e8")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c7")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("c5")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("b6")));
        board.setFigure(new Pawn(Color.WHITE, Cell.parse("d6")));
        board.setFigure(new Pawn(Color.BLACK, Cell.parse("f3")));

        final List<Move> expected =
                toMoveList(from, MoveType.QUIET_MOVE, "B7", "A8", "B5", "A4", "D5", "E4");
        expected.addAll(toMoveList(from, MoveType.ATTACK, "F3", "D7"));

        Assert.assertEquals(sort(expected), sort(queen.getAllMoves(gameSettings)));
    }

    @Test
    public void testKing() throws ChessException {
        // --- Король --- //
        final Cell from1 = Cell.parse("e1");
        final Figure king1 = new King(Color.WHITE, from1);
        final Cell from2 = Cell.parse("e8");
        final Figure king2 = new King(Color.BLACK, from2);
        board.setFigure(king1);
        board.setFigure(king2);

        Assert.assertEquals(
                toMoveList(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8"),
                sort(king2.getAllMoves(gameSettings)));

        // --- Рокировка --- //
        final Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        final Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        final Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        final Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        final List<Move> expected1 =
                toMoveList(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1");
        expected1.addAll(toMoveList(from1, MoveType.SHORT_CASTLING, "G1"));
        expected1.addAll(toMoveList(from1, MoveType.LONG_CASTLING, "C1"));

        final List<Move> expected2 =
                toMoveList(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8");
        expected2.addAll(toMoveList(from2, MoveType.SHORT_CASTLING, "G8"));
        expected2.addAll(toMoveList(from2, MoveType.LONG_CASTLING, "C8"));

        Assert.assertEquals(sort(expected1), sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(sort(expected2), sort(king2.getAllMoves(gameSettings)));

        // --- Уже низя рокировку --- //
        final Figure pawnB1 = new Pawn(Color.BLACK, Cell.parse("b1"));
        final Figure pawnB2 = new Pawn(Color.BLACK, Cell.parse("g1"));
        final Figure pawnW1 = new Pawn(Color.WHITE, Cell.parse("b8"));
        final Figure pawnW2 = new Pawn(Color.WHITE, Cell.parse("g8"));
        board.setFigure(pawnB1);
        board.setFigure(pawnB2);
        board.setFigure(pawnW1);
        board.setFigure(pawnW2);

        Assert.assertEquals(
                toMoveList(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8"),
                sort(king2.getAllMoves(gameSettings)));
    }

    @Test
    public void testKingCastlingFalseRook() throws ChessException {
        // --- Король ---//
        final Cell from1 = Cell.parse("e1");
        final Figure king1 = new King(Color.WHITE, from1);
        final Cell from2 = Cell.parse("e8");
        final Figure king2 = new King(Color.BLACK, from2);
        final Figure knightW1 = new Knight(Color.WHITE, Cell.parse("h1"));
        final Figure knightW2 = new Knight(Color.WHITE, Cell.parse("a1"));
        final Figure knightB1 = new Knight(Color.BLACK, Cell.parse("h8"));
        final Figure knightB2 = new Knight(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(knightW1);
        board.setFigure(knightW2);
        board.setFigure(knightB1);
        board.setFigure(knightB2);

        Assert.assertEquals(
                toMoveList(from1, MoveType.QUIET_MOVE, "D1", "D2", "E2", "F2", "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(from2, MoveType.QUIET_MOVE, "D8", "D7", "E7", "F7", "F8"),
                sort(king2.getAllMoves(gameSettings)));
    }

    @Test
    public void testKingCastling1() throws ChessException {
        // --- Король ---//
        final Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        final Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        final Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        final Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        final Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        final Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        final List<Move> expected1 =
                toMoveList(
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
        Assert.assertEquals(sort(expected1), sort(king1.getAllMoves(gameSettings)));

        final List<Move> expected2 =
                toMoveList(
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
        Assert.assertEquals(sort(expected2), sort(king2.getAllMoves(gameSettings)));

        rookW1.wasMoved = true;
        rookW2.wasMoved = true;
        rookB1.wasMoved = true;
        rookB2.wasMoved = true;

        Assert.assertEquals(
                toMoveList(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                sort(king2.getAllMoves(gameSettings)));
    }

    @Test
    public void testKingCastling2() throws ChessException {
        // --- Король ---//
        final Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        final Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        final Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        final Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        final Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        final Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        king1.wasMoved = true;
        king2.wasMoved = true;

        Assert.assertEquals(
                toMoveList(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                sort(king2.getAllMoves(gameSettings)));
    }

    @Test
    public void testKingCastling3() throws ChessException {
        // --- Король ---//
        final Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        final Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        final Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        final Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        final Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        final Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        final Figure FrookB = new Rook(Color.BLACK, Cell.parse("e4"));
        final Figure FrookW = new Rook(Color.WHITE, Cell.parse("e5"));
        board.setFigure(FrookB);
        board.setFigure(FrookW);

        Assert.assertEquals(
                toMoveList(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                sort(king2.getAllMoves(gameSettings)));
    }

    @Test
    public void testKingCastling4() throws ChessException {
        // --- Король ---//
        final Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        final Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        final Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        final Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        final Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        final Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        final Figure FrookB1 = new Rook(Color.BLACK, Cell.parse("f4"));
        final Figure FrookB2 = new Rook(Color.BLACK, Cell.parse("d4"));
        final Figure FrookW1 = new Rook(Color.WHITE, Cell.parse("f5"));
        final Figure FrookW2 = new Rook(Color.WHITE, Cell.parse("d5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toMoveList(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                sort(king2.getAllMoves(gameSettings)));
    }

    @Test
    public void testKingCastling5() throws ChessException {
        // --- Король ---//
        final Figure king1 = new King(Color.WHITE, Cell.parse("e1"));
        final Figure king2 = new King(Color.BLACK, Cell.parse("e8"));
        final Figure rookW1 = new Rook(Color.WHITE, Cell.parse("h1"));
        final Figure rookW2 = new Rook(Color.WHITE, Cell.parse("a1"));
        final Figure rookB1 = new Rook(Color.BLACK, Cell.parse("h8"));
        final Figure rookB2 = new Rook(Color.BLACK, Cell.parse("a8"));
        board.setFigure(king1);
        board.setFigure(king2);
        board.setFigure(rookW1);
        board.setFigure(rookW2);
        board.setFigure(rookB1);
        board.setFigure(rookB2);

        final Figure FrookB1 = new Queen(Color.BLACK, Cell.parse("g4"));
        final Figure FrookB2 = new Queen(Color.BLACK, Cell.parse("c4"));
        final Figure FrookW1 = new Queen(Color.WHITE, Cell.parse("g5"));
        final Figure FrookW2 = new Queen(Color.WHITE, Cell.parse("c5"));
        board.setFigure(FrookB1);
        board.setFigure(FrookB2);
        board.setFigure(FrookW1);
        board.setFigure(FrookW2);

        Assert.assertEquals(
                toMoveList(
                        king1.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D1",
                        "D2",
                        "E2",
                        "F2",
                        "F1"),
                sort(king1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(
                        king2.getCurrentPosition(),
                        MoveType.QUIET_MOVE,
                        "D8",
                        "D7",
                        "E7",
                        "F7",
                        "F8"),
                sort(king2.getAllMoves(gameSettings)));
    }

    @Test
    public void testKnight() throws ChessException {
        // --- Конь --- //
        final Figure knight = new Knight(Color.BLACK, Cell.parse("f4"));
        board.setFigure(knight);
        Assert.assertEquals(
                toMoveList(
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
                sort(knight.getAllMoves(gameSettings)));
    }

    @Test
    public void testKnightWithFriendPawns() throws ChessException {
        // --- Конь с дружественными пешками вокруг коня, но не закрывающие ход --- //
        final Figure knight = new Knight(Color.WHITE, Cell.parse("a1"));
        final Figure pawn1 = new Pawn(Color.WHITE, Cell.parse("a2"));
        final Figure pawn2 = new Pawn(Color.WHITE, Cell.parse("b2"));
        final Figure pawn3 = new Pawn(Color.WHITE, Cell.parse("b1"));

        board.setFigure(knight);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        Assert.assertEquals(
                toMoveList(knight.getCurrentPosition(), MoveType.QUIET_MOVE, "b3", "c2"),
                sort(knight.getAllMoves(gameSettings)));
    }

    @Test
    public void testPawn() throws ChessException {
        // --- Пешка --- //
        final Figure pawn = new Pawn(Color.WHITE, Cell.parse("c2"));
        final Figure enemy = new Queen(Color.BLACK, Cell.parse("d3"));
        board.setFigure(pawn);
        board.setFigure(enemy);

        final List<Move> expected =
                toMoveList(pawn.getCurrentPosition(), MoveType.QUIET_MOVE, "C3");
        expected.add(new Move(MoveType.LONG_MOVE, pawn.getCurrentPosition(), Cell.parse("C4")));
        expected.add(new Move(MoveType.ATTACK, pawn.getCurrentPosition(), Cell.parse("D3")));

        Assert.assertEquals(sort(expected), sort(pawn.getAllMoves(gameSettings)));

        board.setFigure(new Pawn(Color.BLACK, Cell.parse("c3")));

        Assert.assertEquals(
                toMoveList(pawn.getCurrentPosition(), MoveType.ATTACK, "D3"),
                sort(pawn.getAllMoves(gameSettings)));
    }

    @Test
    public void testPawnForEnemyRespawn() throws ChessException {
        // --- Пешка дошедшая до конца поля --- //
        final Figure pawn = new Pawn(Color.BLACK, Cell.parse("d1"));
        board.setFigure(pawn);
        Assert.assertEquals(List.of(), sort(pawn.getAllMoves(gameSettings)));
    }

    @Test
    public void testPawnWithXEnemy() throws ChessException {
        // --- Пешка окружённая противниками по диагональным клеткам и с противником на пути --- //
        final Figure pawn = new Pawn(Color.WHITE, Cell.parse("c5"));
        final Figure pawn1 = new Pawn(Color.BLACK, Cell.parse("b6"));
        final Figure pawn2 = new Pawn(Color.BLACK, Cell.parse("d6"));
        final Figure pawn3 = new Pawn(Color.BLACK, Cell.parse("b4"));
        final Figure pawn4 = new Pawn(Color.BLACK, Cell.parse("d4"));
        final Figure pawn5 = new Pawn(Color.BLACK, Cell.parse("c6"));
        board.setFigure(pawn);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);
        board.setFigure(pawn5);
        Assert.assertEquals(
                toMoveList(pawn.getCurrentPosition(), MoveType.ATTACK, "B6", "D6"),
                sort(pawn.getAllMoves(gameSettings)));
    }

    @Test
    public void testPawnWithXEnemy2() throws ChessException {
        // --- Пешка окружённая противниками по диагональным клеткам и с противником на пути --- //
        final Figure pawn = new Pawn(Color.WHITE, Cell.parse("c5"));
        final Figure pawn1 = new Pawn(Color.BLACK, Cell.parse("b6"));
        final Figure pawn2 = new Pawn(Color.BLACK, Cell.parse("d6"));
        final Figure pawn3 = new Pawn(Color.BLACK, Cell.parse("b4"));
        final Figure pawn4 = new Pawn(Color.BLACK, Cell.parse("d4"));
        pawn.wasMoved = true;
        board.setFigure(pawn);
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);

        final List<Move> expected =
                toMoveList(pawn.getCurrentPosition(), MoveType.ATTACK, "B6", "D6");
        expected.add(new Move(MoveType.QUIET_MOVE, pawn.getCurrentPosition(), Cell.parse("c6")));

        Assert.assertEquals(sort(expected), sort(pawn.getAllMoves(gameSettings)));
    }

    @Test
    public void testPawnWithEndOfBoard()
            throws ChessException, NoSuchFieldException, IllegalAccessException {
        // --- Пешки стоящие у краев доски --- //
        final Figure pawn1 = new Pawn(Color.BLACK, Cell.parse("a4"));
        final Figure pawn2 = new Pawn(Color.BLACK, Cell.parse("h4"));
        final Figure pawn3 = new Pawn(Color.WHITE, Cell.parse("b4"));
        final Figure pawn4 = new Pawn(Color.WHITE, Cell.parse("g4"));
        pawn1.wasMoved = true;
        pawn2.wasMoved = true;
        board.setFigure(pawn1);
        board.setFigure(pawn2);
        board.setFigure(pawn3);
        board.setFigure(pawn4);
        setPrevMove(new Move(MoveType.LONG_MOVE, Cell.parse("b2"), Cell.parse("b4")));

        List<Move> expected = toMoveList(pawn1.getCurrentPosition(), MoveType.EN_PASSANT, "b3");
        expected.add(new Move(MoveType.QUIET_MOVE, pawn1.getCurrentPosition(), Cell.parse("a3")));

        Assert.assertEquals(sort(expected), sort(pawn1.getAllMoves(gameSettings)));
        setPrevMove(new Move(MoveType.LONG_MOVE, Cell.parse("g2"), Cell.parse("g4")));

        expected = toMoveList(pawn2.getCurrentPosition(), MoveType.EN_PASSANT, "g3");
        expected.add(new Move(MoveType.QUIET_MOVE, pawn2.getCurrentPosition(), Cell.parse("h3")));

        Assert.assertEquals(sort(expected), sort(pawn2.getAllMoves(gameSettings)));
    }

    private void setPrevMove(final Move move) throws NoSuchFieldException, IllegalAccessException {
        final Field prevMove = gameSettings.history.getClass().getDeclaredField("lastMove");
        prevMove.setAccessible(true);
        prevMove.set(gameSettings.history, move);
    }

    @Test
    public void testPawnEnPassant()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
        final Move white1 = new Move(MoveType.LONG_MOVE, Cell.parse("c2"), Cell.parse("c4"));
        final Figure figureW1 = new Pawn(Color.WHITE, white1.getTo());

        setPrevMove(white1);

        final Figure figureB1 = new Pawn(Color.BLACK, Cell.parse("b4"));
        final Figure figureW2 = new Pawn(Color.WHITE, Cell.parse("a4"));
        final Figure figureW3 = new Pawn(Color.WHITE, Cell.parse("b2"));

        board.setFigure(figureW1);
        board.setFigure(figureB1);
        board.setFigure(figureW2);
        board.setFigure(figureW3);

        final List<Move> expected =
                toMoveList(figureB1.getCurrentPosition(), MoveType.QUIET_MOVE, "b3");
        expected.add(
                new Move(MoveType.EN_PASSANT, figureB1.getCurrentPosition(), Cell.parse("c3")));

        Assert.assertEquals(expected, sort(figureB1.getAllMoves(gameSettings)));
        Assert.assertEquals(
                toMoveList(figureW3.getCurrentPosition(), MoveType.QUIET_MOVE, "b3"),
                sort(figureW3.getAllMoves(gameSettings)));
    }

    @Test
    public void testFalsePawnEnPassant()
            throws ChessException, IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {

        final Figure whitePawn = new Pawn(Color.WHITE, Cell.parse("c5"));
        final Figure blackPawn1 = new Pawn(Color.BLACK, Cell.parse("b6"));
        final Figure blackPawn2 = new Pawn(Color.BLACK, Cell.parse("c6"));

        final Move longMove = new Move(MoveType.LONG_MOVE, Cell.parse("d7"), Cell.parse("d5"));
        final Figure blackPawn3 = new Pawn(Color.BLACK, longMove.getTo());
        setPrevMove(longMove);

        board.setFigure(whitePawn);
        board.setFigure(blackPawn1);
        board.setFigure(blackPawn2);
        board.setFigure(blackPawn3);

        final List<Move> expected =
                toMoveList(whitePawn.getCurrentPosition(), MoveType.ATTACK, "b6");
        expected.add(
                new Move(MoveType.EN_PASSANT, whitePawn.getCurrentPosition(), Cell.parse("d6")));

        Assert.assertEquals(expected, sort(whitePawn.getAllMoves(gameSettings)));
    }
}
