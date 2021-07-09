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
        Set<Move> result = new HashSet<>();

        Cell shift;
        if (color == Color.WHITE) {
            shift = new Cell(0, -1);
        } else {
            shift = new Cell(0, 1);
        }

        Cell move = position.createAdd(shift);
        if (board.isEmptyCell(move)) {
            result.add(new Move(isTurnInto(move)
                    ? MoveType.TURN_INTO
                    : MoveType.SIMPLE_STEP, position, move));
        }
        Cell moveLong = move.createAdd(shift);
        if (isStartPosition(position) && board.isEmptyCell(moveLong)) {
            result.add(new Move(MoveType.LONG_MOVE, position, moveLong));
        }

        Cell leftAttack = move.createAdd(new Cell(-1, 0));
        Cell rightAttack = move.createAdd(new Cell(1, 0));
        boolean isEnPassant = isPawnEnPassant(board, position, leftAttack)
                || isPawnEnPassant(board, position, rightAttack);

        MoveType specOrAttackMoveType = isEnPassant ? MoveType.EN_PASSANT : MoveType.ATTACK;
        if (isEnemyFigureOn(board, leftAttack) || isPawnEnPassant(board, position, leftAttack)) {
            result.add(new Move(isTurnInto(leftAttack)
                    ? MoveType.TURN_INTO
                    : specOrAttackMoveType, position, leftAttack));
        }
        if (isEnemyFigureOn(board, rightAttack) || isPawnEnPassant(board, position, rightAttack)) {
            result.add(new Move(isTurnInto(rightAttack)
                    ? MoveType.TURN_INTO
                    : specOrAttackMoveType, position, rightAttack));
        }
        return result;
    }

    private boolean isTurnInto(Cell end) {
        return end.getRow() == (color == Color.WHITE ? 0 : Board.BOARD_SIZE - 1);
    }

    private boolean isStartPosition(Cell start) {
        return start.getRow() == (color == Color.WHITE ? 0 : Board.BOARD_SIZE - 1);
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
    public TypeFigure getType() {
        return TypeFigure.PAWN;
    }

    @Override
    public String toString() {
        return color + " Pawn";
    }
}