package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.figures.IFigure;
import java.util.Map;

public final class Board {

    private static Board board = new Board();

    private Board() {
    }

    private Map<Cell, IFigure> cells;

    public static Board getBoard() {
        return board;
    }

    /**
     * Перемещает фигуру, если ход корректный
     *
     * @throws IllegalArgumentException если ход некорректный
     */
    public void moveFigure(Move move) throws IllegalArgumentException {
        if (!cells.get(move.getFrom()).getAllMovePositions().contains(move.getTo())) {
            throw new IllegalArgumentException("Move is incorrect");
        }
        IFigure figure = cells.get(move.getFrom());
        cells.put(move.getTo(), figure);
    }
}
