package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import java.util.List;

public class Bishop extends Figure {

    public Bishop(final Color color, final Cell position) {
        super(color, position, FigureType.BISHOP);
    }

    public static boolean isAttackedCell(
            final GameSettings settings, final Cell fromPos, final Cell cell) {
        final int x = cell.column;
        final int y = cell.row;
        final int myX = fromPos.column;
        final int myY = fromPos.row;
        if (x == myX && y == myY) return false;
        if (x != myY - y + myX && x != myX + y - myY) return false;
        final Cell attackVector = new Cell(Integer.compare(x, myX), Integer.compare(y, myY));
        final Cell pos = fromPos.createAdd(attackVector);
        while (pos.column != x && settings.board.isEmptyCell(pos)) pos.shift(attackVector);
        return pos.column == x;
    }

    @Override
    public List<Move> getAllMoves(final GameSettings settings) {
        return rayTrace(settings.board, Figure.xMove);
    }

    @Override
    public boolean isAttackedCell(final GameSettings settings, final Cell cell) {
        return isAttackedCell(settings, position, cell);
    }
}
