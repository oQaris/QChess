package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Figure {

    private MoveSystem ms;

    public Pawn(MoveSystem ms, Board board, boolean white, Cell pos) {
        super(board, white, pos, white ? "♙".toCharArray()[0] : "♟".toCharArray()[0]);
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

        var move = position.createAdd(shift);
        if (board.isEmptyCell(move)) {
            result.add(new Move(move.getRow() == (white ? 0 : Board.BOARD_SIZE)
                    ? MoveType.TURN_INTO
                    : MoveType.SIMPLE_STEP, position, move));
        }
        var moveLong = move.createAdd(shift);
        if (position.getRow() == (white ? Board.BOARD_SIZE - 2 : 1) && board.isEmptyCell(moveLong)) {
            result.add(new Move(MoveType.LONG_MOVE, position, moveLong));
        }

        var cellLeft = move.createAdd(new Cell(-1, 0));
        var cellRight = move.createAdd(new Cell(1, 0));
        var isEnPassant = ms.isPawnEnPassant(position, cellLeft) || ms.isPawnEnPassant(position, cellRight);
        var specOrAttackMoveType = isEnPassant ? MoveType.EN_PASSANT : MoveType.ATTACK;
        if (isEnemyFigureOn(cellLeft) || ms.isPawnEnPassant(position, cellLeft)) {
            result.add(new Move(specOrAttackMoveType, position, cellLeft));
        }
        if (isEnemyFigureOn(cellRight) || ms.isPawnEnPassant(position, cellRight)) {
            result.add(new Move(specOrAttackMoveType, position, cellRight));
        }

        return result;
    }

    @Override
    public String toString() {
        return (white ? "White" : "Black") + " Pawn";
    }
}