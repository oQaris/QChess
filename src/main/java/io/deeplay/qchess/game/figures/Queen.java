package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queen extends Figure {

    public Queen(Board board, boolean white, Cell pos) {
        super(board, white, pos);
    }

    @Override
    public Set<Move> getAllMove() {
        return rayTrace(Stream.concat(xMove.stream(), plusMove.stream())
                .collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "Queen " + (white ? "White" : "Black");
    }
}