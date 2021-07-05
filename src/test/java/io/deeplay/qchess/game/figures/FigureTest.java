package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class FigureTest {
    Board board = Board.getBoard();

    @Test
    void getMoves() throws ChessException {
        //--- Слон ---//

        var bishop1 = new Bishop(board, true, Cell.parse("e7"));
        board.setFigure(bishop1);

        Assertions.assertEquals(
                toCellsSet("D8", "F8", "D6", "F6", "C5", "G5", "B4", "H4", "A3"),
                bishop1.getAllMovePositions());
        Assertions.assertEquals(
                toCellsSet("D8", "F8", "D6", "F6", "C5", "G5", "B4", "H4", "A3"),
                bishop1.getAllMovePositions());

        var bishop2 = new Bishop(board, true, Cell.parse("b3"));
        board.setFigure(bishop2);
        Assertions.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8"),
                bishop2.getAllMovePositions());

        //--- Ладья ---//

        var rook = new Rook(board, false, Cell.parse("a6"));
        board.setFigure(rook);
        Assertions.assertEquals(
                toCellsSet("A8", "A7", "A5", "A4", "A3", "A2", "A1", "B6", "C6", "D6", "E6", "F6", "G6", "H6"),
                rook.getAllMovePositions());

        //--- Ферзь ---//

        var queen = new Queen(board, false, Cell.parse("b3"));
        board.setFigure(queen);
        Assertions.assertEquals(
                toCellsSet("A4", "C2", "D1", "A2", "C4", "D5", "E6", "F7", "G8", "B8", "B7", "B6", "B5",
                        "B4", "B2", "B1", "A3", "C3", "D3", "E3", "F3", "G3", "H3"),
                queen.getAllMovePositions());

        //--- Король ---//

        var king = new King(board, false, Cell.parse("e1"));
        board.setFigure(king);
        Assertions.assertEquals(
                toCellsSet(/*"B1", */"D1", "D2", "E2", "F2", "F1" /*"G1"*/),
                king.getAllMovePositions());

        //--- Конь ---//

        var knight = new Knight(board, false, Cell.parse("f4"));
        board.setFigure(knight);
        Assertions.assertEquals(
                toCellsSet("E6", "G6", "D5", "D3", "E2", "G2", "H3", "H5"),
                knight.getAllMovePositions());

        //--- Пешка ---//

        var pawn = new Pawn(board, false, Cell.parse("c2"));
        board.setFigure(pawn);
        Assertions.assertEquals(
                toCellsSet("C3", "C4"/*, "B3"*/),
                pawn.getAllMovePositions());
    }

    @NotNull
    private Set<Cell> toCellsSet(String... pos) {
        var result = new HashSet<Cell>();
        for (String p : pos)
            result.add(Cell.parse(p));
        return result;
    }
}