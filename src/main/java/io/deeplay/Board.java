package io.deeplay;

public final class Board {

    private static Board board = new Board();

    private Board() {
    }

    private Cell[][] cells = new Cell[8][8];

    public static Board getBoard() {
        return board;
    }
}
