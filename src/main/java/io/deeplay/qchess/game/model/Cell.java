package io.deeplay.qchess.game.model;

import java.util.Objects;

public class Cell {

    private int col, row;

    public Cell(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public static Cell parse(String pos) {
        if (pos.length() == 2) {
            char letter = Character.toLowerCase(pos.charAt(0));
            if (letter >= 'a' && letter <= 'h') {
                int digit = pos.charAt(1) - '0';
                if (digit >= 1 && digit <= 8) {
                    return new Cell(letter - 'a', Board.BOARD_SIZE - digit);
                }
            }
        }
        throw new IllegalArgumentException("Incorrect position!");
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return создает новую клетку, суммируя с текущей
     */
    public Cell createAdd(Cell shiftCell) {
        return new Cell(col + shiftCell.col, row + shiftCell.row);
    }

    @Override
    public String toString() {
        return "" + (char) ('a' + col) + (char) ('0' + (Board.BOARD_SIZE - row));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Cell o = (Cell) obj;
        return col == o.col && row == o.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCol(), getRow());
    }
}
