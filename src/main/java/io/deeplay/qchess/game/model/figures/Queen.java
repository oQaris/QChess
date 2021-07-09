package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queen extends Figure {

    public Queen(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getAllMoves(Board board, Cell position) {
        return rayTrace(board, position,
                Stream.concat(xMove.stream(), plusMove.stream())
                        .collect(Collectors.toList()));
    }

    @Override
    public TypeFigure getType() {
        return TypeFigure.QUEEN;
    }

    @Override
    public String toString() {
        return color + " Queen";
    }
}