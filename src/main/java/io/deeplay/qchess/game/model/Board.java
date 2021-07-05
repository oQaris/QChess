package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.IFigure;

public final class Board {

    private final static int BOARD_SIZE = 8;
    private IFigure[][] cells = new IFigure[BOARD_SIZE][BOARD_SIZE];

    public Board() {
    }

    /**
     * @throws ChessException если клетка не лежит в пределах доски
     * @return фигура или null, если клетка пуста
     */
    public IFigure getFigure(int x, int y) throws ChessException {
        if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        return cells[x][y];
    }

    /**
     * Перемещает фигуру с заменой старой, даже если ход некорректный.
     * Перед применением необходима проверка на корректность
     *
     * @return предыдущая фигура на месте перемещения или null, если клетка была пуста
     */
    public IFigure moveFigure(Move move) {
        int toX = move.getTo().getX();
        int toY = move.getTo().getY();
        int fromX = move.getFrom().getX();
        int fromY = move.getFrom().getY();

        IFigure oldFigure = cells[toX][toY];
        cells[toX][toY] = cells[fromX][fromY];
        cells[fromX][fromY] = null;

        return oldFigure;
    }
}
