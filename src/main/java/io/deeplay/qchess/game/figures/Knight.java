package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Knight extends Figure {

    public Knight(Board board, boolean white, Cell pos) {
        super(board, white, pos);
    }

    @Override
    public Set<Move> getAllMoves() {
        return stepForEach(knightMove);
    }

    @Override
    public String toString() {
        return "Knight " + (white ? "White" : "Black");
    }
}