package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class King extends Figure {

    public King(Board board, boolean white, Cell pos) {
        super(board, white, pos);
    }

    @Override
    public Set<Cell> getAllMovePositions() {
        return Stream.concat(xMove.stream(), plusMove.stream())
                .map(shift -> pos.add(shift))
                .filter(cell -> board.isEmptyCell(cell) || isEnemyFigureOn(cell))
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "King " + (white ? "White" : "Black");
    }
}