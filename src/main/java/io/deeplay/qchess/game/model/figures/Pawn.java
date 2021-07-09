package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Figure {

    public Pawn(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getAllMoves(Board board, Cell position) {
        var result = new HashSet<Move>();

        Cell shift;
        if (color == Color.WHITE) {
            shift = new Cell(0, -1);
        } else {
            shift = new Cell(0, 1);
        }

        var move = position.createAdd(shift);
        if (board.isEmptyCell(move)) {
            result.add(new Move(move.getRow() == (color == Color.WHITE ? 0 : Board.BOARD_SIZE)
                    ? MoveType.TURN_INTO
                    : MoveType.SIMPLE_STEP, position, move));
        }
        var moveLong = move.createAdd(shift);
        if (position.getRow() == (color == Color.WHITE ? Board.BOARD_SIZE - 2 : 1) && board.isEmptyCell(moveLong)) {
            result.add(new Move(MoveType.LONG_MOVE, position, moveLong));
        }

        var cellLeft = move.createAdd(new Cell(-1, 0));
        var cellRight = move.createAdd(new Cell(1, 0));
        var isEnPassant = isPawnEnPassant(board, position, cellLeft) || isPawnEnPassant(board, position, cellRight);
        var specOrAttackMoveType = isEnPassant ? MoveType.EN_PASSANT : MoveType.ATTACK;
        if (isEnemyFigureOn(board, cellLeft) || isPawnEnPassant(board, position, cellLeft)) {
            result.add(new Move(specOrAttackMoveType, position, cellLeft));
        }
        if (isEnemyFigureOn(board, cellRight) || isPawnEnPassant(board, position, cellRight)) {
            result.add(new Move(specOrAttackMoveType, position, cellRight));
        }
        return result;
    }

    @Override
    public TypeFigure getType() {
        return TypeFigure.PAWN;
    }

    /**
     * Проверяет, является ли атака пешки взятием на проходе.
     * Входные данные должны гарантировать, что это именно атака пешки (диагональный ход)
     *
     * @return true если это взятие на проходе
     */
    public boolean isPawnEnPassant(Board board, Cell from, Cell to) {
        try {
            Pawn currentPawn = (Pawn) board.getFigure(from);
            Move prevMove = board.getPrevMove();
            Pawn pawn = (Pawn) board.getFigure(prevMove.getTo());

            Cell cellDown = pawn.getColor() == Color.WHITE
                    ? new Cell(prevMove.getTo().getCol(), prevMove.getTo().getRow() + 1)
                    : new Cell(prevMove.getTo().getCol(), prevMove.getTo().getRow() - 1);
            Cell cellDoubleDown = pawn.getColor() == Color.WHITE
                    ? new Cell(cellDown.getCol(), cellDown.getRow() + 1)
                    : new Cell(cellDown.getCol(), cellDown.getRow() - 1);

            return currentPawn.getColor() != pawn.getColor()
                    && cellDoubleDown.equals(prevMove.getFrom())
                    && cellDown.equals(to);
        } catch (ChessException | ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return color + " Pawn";
    }
}