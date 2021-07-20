package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;
import static io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.deeplay.qchess.game.exceptions.ChessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell {
    @JsonIgnore private static final Logger logger = LoggerFactory.getLogger(Cell.class);

    @JsonProperty("column")
    private int column;

    @JsonProperty("row")
    private int row;

    public Cell(final int column, final int row) {
        this.column = column;
        this.row = row;
    }

    public Cell() {}

    /** @deprecated Использует стандартный размер доски - плохо для гибкости */
    @Deprecated(since = "only for tests")
    public static Cell parse(String pos) throws ChessException {
        if (pos.length() == 2) {
            char letter = Character.toLowerCase(pos.charAt(0));
            if (letter >= 'a' && letter <= 'h') {
                int digit = pos.charAt(1) - '0';
                if (digit >= 1 && digit <= STD_BOARD_SIZE)
                    return new Cell(letter - 'a', STD_BOARD_SIZE - digit);
            }
        }
        logger.warn("Координаты клетки заданы некорректно");
        throw new ChessException(INCORRECT_COORDINATES);
    }

    /** @return создает новую клетку, суммируя с текущей */
    public Cell createAdd(Cell shiftCell) {
        return new Cell(column + shiftCell.column, row + shiftCell.row);
    }

    /** Сдвигает текущую клетку на указанный вектор */
    public void shift(Cell shiftCell) {
        column += shiftCell.column;
        row += shiftCell.row;
    }

    /** @deprecated Не использовать вне доски */
    @Deprecated
    @Override
    public int hashCode() {
        // Из-за небольших размеров доски конкатенация чисел однозначно определяет клетку
        return 10 * column + row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return column == cell.column && row == cell.row;
    }

    /** @deprecated Использует стандартный размер доски - плохо для гибкости */
    @Deprecated
    @Override
    public String toString() {
        return String.format("%c%d", 'a' + column, STD_BOARD_SIZE - row);
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
}
