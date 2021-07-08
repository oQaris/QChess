package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;

import java.util.Set;

public class Bishop extends Figure {

    public Bishop(Board board, boolean white, Cell pos) {
        super(board, white, pos, white ? "♗".toCharArray()[0] : "♝".toCharArray()[0]);
    }

    @Override
    public Set<Move> getAllMoves() {
        return rayTrace(xMove);
    }

    @Override
    public String toString() {
        return (white ? "White" : "Black") + " Bishop";
    }
}