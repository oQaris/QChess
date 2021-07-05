package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.IFigure;

public final class Board {
    private final static int BOARD_SIZE = 8;
    private static Board board = new Board();
    private IFigure[][] cells = new IFigure[BOARD_SIZE][BOARD_SIZE];

    private Board() {
    }

    public static Board getBoard() {
        return board;
    }

    /**
     * @return фигура или null, если клетка пуста
     * @throws ChessException если клетка не лежит в пределах доски
     */
    public IFigure getFigure(Cell cell) throws ChessException {
        int x = cell.getCol();
        int y = cell.getRow();
        /*if (!isCorrectCell(x, y))
            throw new ChessException("Координаты выходят за границу доски");*/
        return isCorrectCell(x, y) ? cells[x][y] : null;
    }

    // для тестов
    public IFigure setFigure(IFigure figure) throws ChessException {
        int x = figure.getCurrentPosition().getCol();
        int y = figure.getCurrentPosition().getRow();
        if (!isCorrectCell(x, y))
            throw new ChessException("Координаты выходят за границу доски");
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
     * Перемещает фигуру, если ход корректный
     *
     * @throws ChessException если ход некорректный
     */
    public boolean moveFigure(Move move) throws ChessException {
        try {
            int toX = move.getTo().getCol();
            int toY = move.getTo().getRow();
            int fromX = move.getFrom().getCol();
            int fromY = move.getFrom().getRow();
            if (!cells[fromX][fromY].getAllMovePositions().contains(move.getTo())) {
                throw new ChessException("ашыпка!");
            }
            cells[toX][toY] = cells[fromX][fromY];
            cells[fromX][fromY] = null;
        } catch (ChessException e) {
            throw new ChessException("Ход некорректный");
        }
        return true;
    }
}