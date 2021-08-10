package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class King extends Figure {
    private static final transient Logger logger = LoggerFactory.getLogger(King.class);

    public King(final Color color, final Cell position) {
        super(color, position, FigureType.KING);
    }

    @Override
    public List<Move> getAllMoves(final GameSettings settings) {
        final List<Move> res = getAttackedMoves(settings.board);
        final Cell newCell = new Cell(position.column, position.row);
        // рокировка
        if (isCorrectCastling(settings, true))
            res.add(new Move(MoveType.SHORT_CASTLING, newCell, position.createAdd(new Cell(2, 0))));
        if (isCorrectCastling(settings, false))
            res.add(new Move(MoveType.LONG_CASTLING, newCell, position.createAdd(new Cell(-2, 0))));
        return res;
    }

    @Override
    public boolean isAttackedCell(final GameSettings settings, final Cell cell) {
        final int x = cell.column;
        final int y = cell.row;
        final int myX = position.column;
        final int myY = position.row;
        return (x != myX || y != myY) && Math.abs(myX - x) <= 1 && Math.abs(myY - y) <= 1;
    }

    @Override
    public void setCurrentPosition(final Cell position) {
        this.position.column = position.column;
        this.position.row = position.row;
    }

    /** @return ходы без рокировки */
    public List<Move> getAttackedMoves(final Board board) {
        return stepForEach(board, xPlusMove, true);
    }

    /** @return true, если рокировка возможна */
    private boolean isCorrectCastling(final GameSettings settings, final boolean shortCastling) {
        logger.trace("Запущена проверка на возможность рокировки для {}", this);
        if (wasMoved
                || !settings.board.isEmptyCell(
                        position.createAdd(new Cell(shortCastling ? 1 : -1, 0)))
                || !settings.board.isEmptyCell(
                        position.createAdd(new Cell(shortCastling ? 2 : -2, 0)))
                || !shortCastling
                        && !settings.board.isEmptyCell(position.createAdd(new Cell(-3, 0)))
                || Board.isAttackedCell(settings, position, color.inverse())
                || Board.isAttackedCell(
                        settings,
                        position.createAdd(new Cell(shortCastling ? 1 : -1, 0)),
                        color.inverse())
                || Board.isAttackedCell(
                        settings,
                        position.createAdd(new Cell(shortCastling ? 2 : -2, 0)),
                        color.inverse())) return false;

        return shortCastling
                ? settings.board.isNotRightRookStandardMoved(color)
                : settings.board.isNotLeftRookStandardMoved(color);
    }
}
