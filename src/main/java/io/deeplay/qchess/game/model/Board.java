package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.IFigure;

public final class Board {

    private final static int BOARD_SIZE = 8;
    private IFigure[][] cells = new IFigure[BOARD_SIZE][BOARD_SIZE];

    public Board() {
    }

    /**
     * @throws ChessException если клетка не лежит в пределах доски
     * @return фигура или null, если клетка пуста
     */
    public IFigure getFigure(Cell cell) throws ChessException {
        int x = cell.getCol();
        int y = cell.getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        return isCorrectCell(x, y) ? cells[x][y] : null;
    }

    // для тестов
    public IFigure setFigure(IFigure figure) throws ChessException {
        int x = figure.getCurrentPosition().getCol();
        int y = figure.getCurrentPosition().getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        return cells[x][y] = figure;
    }

    public boolean isEmptyCell(Cell cell) {
        int x = cell.getCol();
        int y = cell.getRow();
        return isCorrectCell(x, y) && cells[x][y] == null;
    }

    /**
     * @return true, если данная фигура ещё не делала ходов
     */
    public boolean isNotMakeMoves(IFigure figure) {
        return true;
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    private boolean isCorrectCell(int x, int y) {
        return x >= 0 && y >= 0 && x < BOARD_SIZE && y < BOARD_SIZE;
    }

    /**
     * Перемещает фигуру с заменой старой, даже если ход некорректный.
     * Перед применением необходима проверка на корректность
     *
     * @return предыдущая фигура на месте перемещения или null, если клетка была пуста
     */
    public IFigure moveFigure(Move move) {
        int toX = move.getTo().getCol();
        int toY = move.getTo().getRow();
        int fromX = move.getFrom().getCol();
        int fromY = move.getFrom().getRow();

        IFigure oldFigure = cells[toX][toY];
        cells[toX][toY] = cells[fromX][fromY];
        cells[fromX][fromY] = null;

        return oldFigure;
    }
}
