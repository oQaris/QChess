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
                if (digit >= 1 && digit <= 8)
                    return new Cell(letter - 'a', digit - 1);
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

    public Cell add(Cell shiftCell) {
        return new Cell(col + shiftCell.col, row + shiftCell.row);
    }

    @Override
    public String toString() {
        //return "" + (Arrays.toString(Character.toChars('a' + col))) + Arrays.toString(Character.toChars('1' + row));
        return "" + (char) ('a' + col) + (char) ('1' + row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return getCol() == cell.getCol() &&
                getRow() == cell.getRow();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCol(), getRow());
    }

    /**
     * @return true если клетку бьет черная фигура, false иначе
     */
    public boolean blackHits() {
        // TODO: реализовать рейкастом (или перебором будет быстрее?)
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @return true если клетку бьет белая фигура, false иначе
     */
    public boolean whiteHits() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
