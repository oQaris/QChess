package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import java.util.HashSet;
import java.util.Set;

public class Pawn extends Figure {

    private MoveSystem ms;

    public Pawn(MoveSystem ms, Board board, boolean white, Cell pos) {
        super(board, white, pos);
        this.ms = ms;
    }

    @Override
    public Set<Cell> getAllMovePositions() {
        var result = new HashSet<Cell>();

        Cell shift;
        if (white) {
            shift = new Cell(0, -1);
        } else {
            shift = new Cell(0, 1);
        }

        var move = pos.add(shift);
        if (board.isEmptyCell(move)) {
            result.add(move);
        }
        var moveLong = move.add(shift);
        if (isFirstMove && board.isEmptyCell(moveLong)) {
            result.add(moveLong);
        }

        var cellLeft = move.add(new Cell(-1, 0));
        var cellRight = move.add(new Cell(1, 0));
        if (isEnemyFigureOn(cellLeft) || ms.isPawnEnPassant(pos, cellLeft)) {
            result.add(cellLeft);
        }
        if (isEnemyFigureOn(cellRight) || ms.isPawnEnPassant(pos, cellRight)) {
            result.add(cellRight);
        }

        return result;
    }

    @Override
    public String toString() {
        return "Pawn " + (white ? "White" : "Black");
    }
}
