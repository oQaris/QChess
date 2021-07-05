package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
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
     * @throws ChessException если ход некорректный
     * @return true если ход был выполнен успешно
     */
    public boolean moveFigure(Move move) throws ChessException {
        if (!cells.get(move.getFrom()).getAllMovePositions().contains(move.getTo())) {
            throw new ChessException("Move is incorrect");
        }
        IFigure figure = cells.get(move.getFrom());
        cells.put(move.getTo(), figure);
        return true;
    }
}
