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

    public Pawn(Color color, Cell position) {
        super(color, position);
    }

    /**
     * Проверяет, является ли атака пешки взятием на проходе.
     * Входные данные должны гарантировать, что это именно атака пешки (диагональный ход)
     *
     * @return true если это взятие на проходе
     */
    public static boolean isPawnEnPassant(Board board, Cell from, Cell to) {
        try {
            var currentPawn = (Pawn) board.getFigure(from);
            var prevMove = board.getPrevMove();
            var pawn = (Pawn) board.getFigure(prevMove.getTo());

            Cell cellDown = pawn.getColor() == Color.WHITE
                    ? new Cell(prevMove.getTo().getColumn(), prevMove.getTo().getRow() + 1)
                    : new Cell(prevMove.getTo().getColumn(), prevMove.getTo().getRow() - 1);
            Cell cellDoubleDown = pawn.getColor() == Color.WHITE
                    ? new Cell(cellDown.getColumn(), cellDown.getRow() + 1)
                    : new Cell(cellDown.getColumn(), cellDown.getRow() - 1);

            return currentPawn.getColor() != pawn.getColor()
                    && cellDoubleDown.equals(prevMove.getFrom())
                    && cellDown.equals(to);
        } catch (ChessException | ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public Set<Move> getAllMoves(Board board) {
        Set<Move> result = new HashSet<>();
        Cell forwardShift = color == Color.WHITE ? new Cell(0, -1) : new Cell(0, 1);
        addShortAndLongMove(board, forwardShift, result);
        addEnPassant(board, forwardShift, result);
        return result;
    }

    private void addShortAndLongMove(Board board, Cell forwardShift, Set<Move> result) {
        Cell move = position.createAdd(forwardShift);
        if (board.isEmptyCell(move)) {
            result.add(new Move(isTurnInto(move)
                    ? MoveType.TURN_INTO
                    : MoveType.QUIET_MOVE, position, move));

            Cell longMove = move.createAdd(forwardShift);
            if (isStartPosition(position) && board.isEmptyCell(longMove))
                result.add(new Move(MoveType.LONG_MOVE, position, longMove));
        }
    }

    private void addEnPassant(Board board, Cell forwardShift, Set<Move> result) {
        Cell leftAttack = position.createAdd(forwardShift).createAdd(new Cell(-1, 0));
        Cell rightAttack = position.createAdd(forwardShift).createAdd(new Cell(1, 0));

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
    }

    private boolean isTurnInto(Cell end) {
        return end.getRow() == (color == Color.WHITE ? 0 : Board.BOARD_SIZE - 1);
    }

    private boolean isStartPosition(Cell start) {
        return start.getRow() == (color == Color.BLACK ? 1 : Board.BOARD_SIZE - 2);
    }

    @Override
    public TypeFigure getType() {
        return TypeFigure.PAWN;
    }
}