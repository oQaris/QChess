package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.IFigure;

public final class Board {

    private final static int BOARD_SIZE = 8;
    private static Board board = new Board();

    private Board() {
    }

    private IFigure[][] cells = new IFigure[BOARD_SIZE][BOARD_SIZE];

    public static Board getBoard() {
        return board;
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
     * Перемещает фигуру, если ход корректный
     *
     * @throws ChessException если ход некорректный
     * @return true если ход был выполнен успешно
     */
    public boolean moveFigure(Move move) throws ChessException {
        try {
            int toX = move.getTo().getX();
            int toY = move.getTo().getY();
            int fromX = move.getFrom().getX();
            int fromY = move.getFrom().getY();
            if (!cells[fromX][fromY].getAllMovePositions().contains(move.getTo())) {
                throw new ChessException();
            }
            cells[toX][toY] = cells[fromX][fromY];
            cells[fromX][fromY] = null;
        } catch (ChessException e) {
            throw new ChessException("Ход некорректный");
        }
        return true;
    }
}
