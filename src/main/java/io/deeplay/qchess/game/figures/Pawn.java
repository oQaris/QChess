package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Figure {

    public Pawn(Board board, boolean white, Cell pos) {
        super(board, white, pos);
    }

    @Override
    public Set<Cell> getAllMovePositions() {
        var result = new HashSet<Cell>();
        Cell shift;
        if (white)
            shift = new Cell(0, -1);
        else shift = new Cell(0, 1);
        try {
            var move = pos.add(shift);
            if (board.isEmptyCell(move))
                result.add(move);
            var longMove = move.add(shift);
            if (board.isNotMakeMoves(this))
                result.add(longMove);

            var endFigureLeft = board.getFigure((longMove.add(new Cell(-1, 0))));
            var endFigureRight = board.getFigure((longMove.add(new Cell(1, 0))));
            if (endFigureLeft != null && white != endFigureLeft.isWhite())
                result.add(endFigureLeft.getCurrentPosition());
            if (endFigureRight != null && white != endFigureRight.isWhite())
                result.add(endFigureRight.getCurrentPosition());
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return result;
    }
}