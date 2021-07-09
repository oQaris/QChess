package io.deeplay.qchess.game.model;

import java.util.Objects;

public class Cell {
    private int column;
    private int row;

    public Cell(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public static Cell parse(String pos) {
        if (pos.length() == 2) {
            char letter = Character.toLowerCase(pos.charAt(0));
            if (letter >= 'a' && letter <= 'h') {
                int digit = pos.charAt(1) - '0';
                if (digit >= 1 && digit <= Board.BOARD_SIZE) {
                    return new Cell(letter - 'a', Board.BOARD_SIZE - digit);
                }
            }
        }
        throw new IllegalArgumentException("Incorrect position!");
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
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
        return new Cell(column + shiftCell.column, row + shiftCell.row);
    }

    @Override
    public String toString() {
        return "" + (char) ('a' + column) + (char) ('0' + (Board.BOARD_SIZE - row));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Cell o = (Cell) obj;
        return column == o.column && row == o.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColumn(), getRow());
    }
}
