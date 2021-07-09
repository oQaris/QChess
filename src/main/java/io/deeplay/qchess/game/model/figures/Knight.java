package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;

import java.util.Set;

public class Knight extends Figure {

    public Knight(Board board, boolean white, Cell pos) {
        super(board, white, pos, white ? "♘".toCharArray()[0] : "♞".toCharArray()[0]);
    }

    @Override
    public Set<Move> getAllMoves() {
        return stepForEach(knightMove);
    }

    @Override
    public String toString() {
        return (white ? "White" : "Black") + " Knight";
    }
}