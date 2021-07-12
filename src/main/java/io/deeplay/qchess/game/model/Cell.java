package io.deeplay.qchess.game.model;

import java.util.Objects;

public class Cell {
  private final int column;
  private final int row;

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

  /** @return создает новую клетку, суммируя с текущей */
  public Cell createAdd(Cell shiftCell) {
    return new Cell(column + shiftCell.column, row + shiftCell.row);
  }

  @Override
  public int hashCode() {
    return Objects.hash(column, row);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Cell)) return false;
    Cell cell = (Cell) o;
    return getColumn() == cell.getColumn() && getRow() == cell.getRow();
  }

  public int getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }

  @Override
  public String toString() {
    return String.format("%c%c", 'a' + column, '0' + Board.BOARD_SIZE - row);
  }
}
