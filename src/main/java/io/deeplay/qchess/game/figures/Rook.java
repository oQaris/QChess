package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;

import java.util.Set;

public class Rook extends Figure {

    public Rook(Board board, boolean white, Cell pos) {
        super(board, white, pos);
    }

    @Override
    public Set<Cell> getAllMovePositions() {
        return rayTrace(plusMove);
    }

    @Override
    public String toString() {
        return "Rook " + (white ? "White" : "Black");
    }
}