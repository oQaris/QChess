package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.Set;

public class Knight extends Figure {

    public Knight(Color color) {
        super(color);
    }

    @Override
    public String toString() {
        return color + " Knight";
    }

    @Override
    public Set<Move> getAllMoves(Board board, Cell position) {
        return stepForEach(board, position, knightMove);
    }

    @Override
    public TypeFigure getType() {
        return TypeFigure.KNIGHT;
    }
}