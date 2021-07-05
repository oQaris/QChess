package io.deeplay.qchess.game;

public final class Board {

    private final static int BOARD_SIZE = 8;
    private static Board board = new Board();

    private Board() {
    }

    private Cell[][] cells = new Cell[BOARD_SIZE][BOARD_SIZE];

    public static Board getBoard() {
        return board;
    }

    /**
     * @throw IllegalArgumentException если координаты выходят за границу доски
     * @return клетка доски
     */
    public Cell getCell(int i, int j) throws IllegalArgumentException {
        if (i < 0 || j < 0 || i >= BOARD_SIZE || j >= BOARD_SIZE) {
            throw new IllegalArgumentException("Координаты выходят за границу доски");
        }
        return cells[i][j];
    }
}
