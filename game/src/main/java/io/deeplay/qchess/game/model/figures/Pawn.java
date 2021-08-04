package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Figure {

    public Pawn(Color color, Cell position) {
        super(color, position, FigureType.PAWN);
    }

    @Override
    public List<Move> getAllMoves(GameSettings settings) {
        List<Move> result = new ArrayList<>(4);

        Cell forwardShift = color == Color.WHITE ? new Cell(0, -1) : new Cell(0, 1);
        addShortAndLongMove(settings, forwardShift, result);

        Move leftSpecialMove =
                getSpecialMove(settings, position.createAdd(forwardShift).shift(new Cell(-1, 0)));
        Move rightSpecialMove =
                getSpecialMove(settings, position.createAdd(forwardShift).shift(new Cell(1, 0)));
        if (leftSpecialMove != null) result.add(leftSpecialMove);
        if (rightSpecialMove != null) result.add(rightSpecialMove);

        return result;
    }

    @Override
    public boolean isAttackedCell(GameSettings settings, Cell cell) {
        Cell forwardShift = color == Color.WHITE ? new Cell(0, -1) : new Cell(0, 1);
        Cell shifted = position.createAdd(forwardShift).shift(new Cell(-1, 0));
        return shifted.equals(cell) || shifted.shift(new Cell(2, 0)).equals(cell);
    }

    private void addShortAndLongMove(GameSettings settings, Cell forwardShift, List<Move> result) {
        Cell move = position.createAdd(forwardShift);
        if (settings.board.isEmptyCell(move)) {
            result.add(
                    new Move(
                            isTurnInto(move, settings) ? MoveType.TURN_INTO : MoveType.QUIET_MOVE,
                            position,
                            move));

            Cell longMove = move.createAdd(forwardShift);
            if (!wasMoved && settings.board.isEmptyCell(longMove))
                result.add(new Move(MoveType.LONG_MOVE, position, longMove));
        }
    }

    private Move getSpecialMove(GameSettings settings, Cell attack) {
        boolean isEnPassant = isPawnEnPassant(settings, attack);
        if (settings.board.isEnemyFigureOn(color, attack) || isEnPassant) {
            if (isTurnInto(attack, settings)) {
                return new Move(MoveType.TURN_INTO_ATTACK, position, attack);
            } else {
                return new Move(
                        isEnPassant ? MoveType.EN_PASSANT : MoveType.ATTACK, position, attack);
            }
        }
        return null;
    }

    /**
     * Проверяет, является ли атака пешки взятием на проходе. Входные данные должны гарантировать,
     * что это именно атака пешки (диагональный ход)
     *
     * @return true если это взятие на проходе
     */
    private boolean isPawnEnPassant(GameSettings settings, Cell to) {
        Move prevMove = settings.history.getLastMove();
        if (prevMove == null) return false;
        Cell prevMoveTo = prevMove.getTo();

        Figure pawn = settings.board.getFigureUgly(prevMoveTo);
        if (pawn == null || pawn.figureType != FigureType.PAWN || color == pawn.getColor())
            return false;

        Cell shift = new Cell(0, pawn.getColor() == Color.WHITE ? 1 : -1);

        Cell cellDown = prevMoveTo.createAdd(shift);
        if (!cellDown.equals(to)) return false;

        Cell cellDoubleDown = cellDown.shift(shift);
        return cellDoubleDown.equals(prevMove.getFrom());
    }

    private boolean isTurnInto(Cell end, GameSettings settings) {
        return end.row == (color == Color.WHITE ? 0 : settings.board.boardSize - 1);
    }
}
