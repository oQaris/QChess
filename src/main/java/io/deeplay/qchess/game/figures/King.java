package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class King extends Figure {

    public King(Board board, boolean white, Cell pos) {
        super(board, white, pos);
    }

    @Override
    public Set<Move> getAllMoves() {
        // надо проверить, если есть рокировка, то добавить ходы тоже
        return stepForEach(Stream.concat(xMove.stream(), plusMove.stream())
                .collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "King " + (white ? "White" : "Black");
    }
}