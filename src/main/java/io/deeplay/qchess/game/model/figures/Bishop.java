package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.Set;

public class Bishop extends Figure {

    public Bishop(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getAllMoves(Board board, Cell position) {
        return rayTrace(board, position, xMove);
    }

    @Override
    public TypeFigure getType() {
        return TypeFigure.BISHOP;
    }


    @Override
    public String toString() {
        return color.toString() + " Bishop";
    }
}