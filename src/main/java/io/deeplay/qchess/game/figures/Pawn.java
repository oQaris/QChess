package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Figure {

    private MoveSystem ms;

    public Pawn(MoveSystem ms, Board board, boolean white, Cell pos) {
        super(board, white, pos);
        this.ms = ms;
    }

    @Override
    public Set<Move> getAllMoves() {
        var result = new HashSet<Move>();

        Cell shift;
        if (white) {
            shift = new Cell(0, -1);
        } else {
            shift = new Cell(0, 1);
        }

        var move = pos.add(shift);
        if (board.isEmptyCell(move)) {
            result.add(new Move(move.getRow() == (white ? 0 : Board.BOARD_SIZE)
                    ? MoveType.TURN_INTO
                    : MoveType.SIMPLE_STEP, pos, move));
        }
        var moveLong = move.add(shift);
        if (pos.getRow() == (white ? Board.BOARD_SIZE - 2 : 1) && board.isEmptyCell(moveLong)) {
            result.add(new Move(MoveType.LONG_MOVE, pos, moveLong));
        }

        var cellLeft = move.add(new Cell(-1, 0));
        var cellRight = move.add(new Cell(1, 0));
        var isEnPassant = ms.isPawnEnPassant(pos, cellLeft) || ms.isPawnEnPassant(pos, cellRight);
        var specOrAttackMoveType = isEnPassant ? MoveType.EN_PASSANT : MoveType.ATTACK;
        if (isEnemyFigureOn(cellLeft) || ms.isPawnEnPassant(pos, cellLeft)) {
            result.add(new Move(specOrAttackMoveType, pos, cellLeft));
        }
        if (isEnemyFigureOn(cellRight) || ms.isPawnEnPassant(pos, cellRight)) {
            result.add(new Move(specOrAttackMoveType, pos, cellRight));
        }

        return result;
    }

    @Override
    public String toString() {
        return "Pawn " + (white ? "White" : "Black");
    }
}
