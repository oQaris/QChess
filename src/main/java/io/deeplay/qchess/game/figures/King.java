package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
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
                .filter(cell -> {
                    try {
                        //todo костыли - переделать
                        return board.isEmptyCell(cell) || (board.getFigure(cell) != null && white != board.getFigure(cell).isWhite());
                    } catch (ChessException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toSet());
    }
}