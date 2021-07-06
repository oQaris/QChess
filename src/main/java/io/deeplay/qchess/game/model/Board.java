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

    /**
     * Устанавливает фигуру на доску
     */
    public void setFigure(IFigure figure) throws ChessException {
        int x = figure.getCurrentPosition().getCol();
        int y = figure.getCurrentPosition().getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        cells[x][y] = figure;
    }

    /**
     * Убирает фигуру с доски
     *
     * @return удаленную фигуру
     */
    public IFigure removeFigure(IFigure figure) throws ChessException {
        int x = figure.getCurrentPosition().getCol();
        int y = figure.getCurrentPosition().getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        IFigure old = cells[x][y];
        cells[x][y] = null;
        return old;
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
    public boolean isCorrectCell(int col, int row) {
        return col >= 0 && row >= 0 && col < BOARD_SIZE && row < BOARD_SIZE;
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    public boolean isCorrectCell(Cell cell) {
        return isCorrectCell(cell.getCol(), cell.getRow());
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
