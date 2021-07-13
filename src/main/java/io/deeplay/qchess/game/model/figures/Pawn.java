package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
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

    @Override
    public Set<Move> getAllMoves(GameSettings settings) {
        Set<Move> result = new HashSet<>();
        Cell forwardShift = color == Color.WHITE ? new Cell(0, -1) : new Cell(0, 1);
        addShortAndLongMove(settings, forwardShift, result);
        addEnPassant(settings, forwardShift, result);
        return result;
    }

    private void addShortAndLongMove(GameSettings settings, Cell forwardShift, Set<Move> result) {
        Cell move = position.createAdd(forwardShift);
        if (settings.board.isEmptyCell(move)) {
            result.add(
                    new Move(
                            isTurnInto(move) ? MoveType.TURN_INTO : MoveType.QUIET_MOVE,
                            position,
                            move));

            Cell longMove = move.createAdd(forwardShift);
            if (isStartPosition(position) && settings.board.isEmptyCell(longMove)) {
                result.add(new Move(MoveType.LONG_MOVE, position, longMove));
            }
        }
    }

    private void addEnPassant(GameSettings settings, Cell forwardShift, Set<Move> result) {
        Cell leftAttack = position.createAdd(forwardShift).createAdd(new Cell(-1, 0));
        Cell rightAttack = position.createAdd(forwardShift).createAdd(new Cell(1, 0));

        boolean isEnPassant =
                isPawnEnPassant(settings, position, leftAttack)
                        || isPawnEnPassant(settings, position, rightAttack);
        MoveType specOrAttackMoveType = isEnPassant ? MoveType.EN_PASSANT : MoveType.ATTACK;

        if (isEnemyFigureOn(settings.board, leftAttack)
                || isPawnEnPassant(settings, position, leftAttack)) {
            result.add(
                    new Move(
                            isTurnInto(leftAttack) ? MoveType.TURN_INTO : specOrAttackMoveType,
                            position,
                            leftAttack));
        }
        if (isEnemyFigureOn(settings.board, rightAttack)
                || isPawnEnPassant(settings, position, rightAttack)) {
            result.add(
                    new Move(
                            isTurnInto(rightAttack) ? MoveType.TURN_INTO : specOrAttackMoveType,
                            position,
                            rightAttack));
        }
    }

    private boolean isTurnInto(Cell end) {
        return end.getRow() == (color == Color.WHITE ? 0 : Board.BOARD_SIZE - 1);
    }

    private boolean isStartPosition(Cell start) {
        return start.getRow() == (color == Color.BLACK ? 1 : Board.BOARD_SIZE - 2);
    }

    /**
     * Проверяет, является ли атака пешки взятием на проходе. Входные данные должны гарантировать,
     * что это именно атака пешки (диагональный ход)
     *
     * @return true если это взятие на проходе
     */
    public static boolean isPawnEnPassant(GameSettings settings, Cell from, Cell to) {
        try {
            Pawn currentPawn = (Pawn) settings.board.getFigure(from);
            Move prevMove = settings.history.getPrevMove();
            Pawn pawn = (Pawn) settings.board.getFigure(prevMove.getTo());

            Cell cellDown =
                    pawn.getColor() == Color.WHITE
                            ? new Cell(prevMove.getTo().getColumn(), prevMove.getTo().getRow() + 1)
                            : new Cell(prevMove.getTo().getColumn(), prevMove.getTo().getRow() - 1);
            Cell cellDoubleDown =
                    pawn.getColor() == Color.WHITE
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
    public TypeFigure getType() {
        return TypeFigure.PAWN;
    }
}
