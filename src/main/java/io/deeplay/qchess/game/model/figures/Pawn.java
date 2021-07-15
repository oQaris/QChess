package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pawn extends Figure {
    private static final Logger logger = LoggerFactory.getLogger(Pawn.class);

    public Pawn(Color color, Cell position) {
        super(color, position);
    }

    /**
     * Проверяет, является ли атака пешки взятием на проходе. Входные данные должны гарантировать,
     * что это именно атака пешки (диагональный ход)
     *
     * @return true если это взятие на проходе
     */
    public boolean isPawnEnPassant(GameSettings settings, Cell to) {
        try {
            Move prevMove = settings.history.getLastMove();
            if (prevMove == null) return false;
            Figure pawn = settings.board.getFigure(prevMove.getTo());
            if (pawn == null || pawn.getType() != FigureType.PAWN) return false;

            Cell cellDown =
                    pawn.getColor() == Color.WHITE
                            ? new Cell(prevMove.getTo().getColumn(), prevMove.getTo().getRow() + 1)
                            : new Cell(prevMove.getTo().getColumn(), prevMove.getTo().getRow() - 1);
            Cell cellDoubleDown =
                    pawn.getColor() == Color.WHITE
                            ? new Cell(cellDown.getColumn(), cellDown.getRow() + 1)
                            : new Cell(cellDown.getColumn(), cellDown.getRow() - 1);

            return color != pawn.getColor()
                    && cellDoubleDown.equals(prevMove.getFrom())
                    && cellDown.equals(to);
        } catch (ChessException | NullPointerException e) {
            logger.warn("В проверке взятия на проходе возникло исключение: {}", e.getMessage());
            return false;
        }
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
        logger.trace(
                "Начато добавление смещения {} для длинного или короткого перемещения",
                forwardShift);
        Cell move = position.createAdd(forwardShift);
        if (settings.board.isEmptyCell(move)) {
            result.add(
                    new Move(
                            isTurnInto(move) ? MoveType.TURN_INTO : MoveType.QUIET_MOVE,
                            position,
                            move));

            Cell longMove = move.createAdd(forwardShift);
            if (!wasMoved && settings.board.isEmptyCell(longMove))
                result.add(new Move(MoveType.LONG_MOVE, position, longMove));
        }
    }

    private void addEnPassant(GameSettings settings, Cell forwardShift, Set<Move> result) {
        logger.trace("Начато добавление смещения {} для взятия на проходе", forwardShift);
        Cell leftAttack = position.createAdd(forwardShift).createAdd(new Cell(-1, 0));
        Cell rightAttack = position.createAdd(forwardShift).createAdd(new Cell(1, 0));

        boolean isEnPassant =
                isPawnEnPassant(settings, leftAttack) || isPawnEnPassant(settings, rightAttack);
        MoveType specOrAttackMoveType = isEnPassant ? MoveType.EN_PASSANT : MoveType.ATTACK;

        if (settings.board.isEnemyFigureOn(color, leftAttack)
                || isPawnEnPassant(settings, leftAttack)) {
            result.add(
                    new Move(
                            isTurnInto(leftAttack) ? MoveType.TURN_INTO : specOrAttackMoveType,
                            position,
                            leftAttack));
        }
        if (settings.board.isEnemyFigureOn(color, rightAttack)
                || isPawnEnPassant(settings, rightAttack)) {
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

    @Override
    public FigureType getType() {
        return FigureType.PAWN;
    }
}
