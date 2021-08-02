package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import java.util.Set;

public class Rook extends Figure {

    public Rook(Color color, Cell position) {
        super(color, position, FigureType.ROOK);
    }

    public static boolean isAttackedCell(GameSettings settings, Cell fromPos, Cell cell) {
        int x = cell.column;
        int y = cell.row;
        int myX = fromPos.column;
        int myY = fromPos.row;
        if (x == myX && y == myY) return false;
        if (x != myX && y != myY) return false;
        if (x == myX) {
            Cell attackVector = new Cell(0, Integer.compare(y, myY));
            Cell pos = fromPos.createAdd(attackVector);
            while (pos.row != y && settings.board.isEmptyCell(pos)) pos.shift(attackVector);
            return pos.row == y;
        } else {
            Cell attackVector = new Cell(Integer.compare(x, myX), 0);
            Cell pos = fromPos.createAdd(attackVector);
            while (pos.column != x && settings.board.isEmptyCell(pos)) pos.shift(attackVector);
            return pos.column == x;
        }
    }

    @Override
    public Set<Move> getAllMoves(GameSettings settings) {
        return rayTrace(settings.board, Figure.plusMove);
    }

    @Override
    public boolean isAttackedCell(GameSettings settings, Cell cell) {
        return isAttackedCell(settings, position, cell);
    }
}
