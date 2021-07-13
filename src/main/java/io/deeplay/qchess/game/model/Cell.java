package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;

import io.deeplay.qchess.game.exceptions.ChessException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell {
    private static final Logger logger = LoggerFactory.getLogger(Cell.class);
    private final int column;
    private final int row;

    public Cell(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public static Cell parse(String pos) throws ChessException {
        if (pos.length() == 2) {
            char letter = Character.toLowerCase(pos.charAt(0));
            if (letter >= 'a' && letter <= 'h') {
                int digit = pos.charAt(1) - '0';
                if (digit >= 1 && digit <= Board.BOARD_SIZE)
                    return new Cell(letter - 'a', Board.BOARD_SIZE - digit);
            }
        }
        logger.warn("Координаты клетки заданы некорректно");
        throw new ChessException(INCORRECT_COORDINATES);
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
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return column == cell.column && row == cell.row;
    }

    @Override
    public String toString() {
        return String.format("%c%d", 'a' + column, Board.BOARD_SIZE - row);
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
}
