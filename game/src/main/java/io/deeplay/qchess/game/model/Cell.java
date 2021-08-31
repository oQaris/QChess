package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;
import static io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell implements Serializable {
    public static final transient int[][] hashCodes = new int[STD_BOARD_SIZE][STD_BOARD_SIZE];

    private static final transient Logger logger = LoggerFactory.getLogger(Cell.class);

    static {
        for (int i = 0; i < STD_BOARD_SIZE; ++i)
            for (int j = 0; j < STD_BOARD_SIZE; ++j) hashCodes[i][j] = i * STD_BOARD_SIZE + j;
    }

    @SerializedName("column")
    public int column;

    @SerializedName("row")
    public int row;

    public Cell(final int column, final int row) {
        this.column = column;
        this.row = row;
    }

    public Cell(final Cell cell) {
        column = cell.column;
        row = cell.row;
    }

    /** @deprecated Использует стандартный размер доски - плохо для гибкости */
    @Deprecated(since = "only for tests")
    public static Cell parse(final String pos) {
        if (pos.length() == 2) {
            final char letter = Character.toLowerCase(pos.charAt(0));
            if (letter >= 'a' && letter <= 'h') {
                final int digit = pos.charAt(1) - '0';
                if (digit >= 1 && digit <= STD_BOARD_SIZE)
                    return new Cell(letter - 'a', STD_BOARD_SIZE - digit);
            }
        }
        logger.warn("Координаты клетки заданы некорректно");
        throw new IllegalArgumentException(INCORRECT_COORDINATES.getMessage());
    }

    /** @return номер клетки на доске */
    public int toSquare() {
        return row * 8 + column;
    }

    /** @return создает новую клетку, суммируя с текущей */
    public Cell createAdd(final Cell shiftCell) {
        return new Cell(column + shiftCell.column, row + shiftCell.row);
    }

    /** Сдвигает текущую клетку на указанный вектор */
    public Cell shift(final Cell shiftCell) {
        column += shiftCell.column;
        row += shiftCell.row;
        return this;
    }

    /** @deprecated Не использовать вне доски 8x8 */
    @Deprecated
    @Override
    public int hashCode() {
        return hashCodes[column][row];
    }

    public byte toByte() {
        return (byte) hashCodes[column][row];
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Cell cell = (Cell) o;
        return column == cell.column && row == cell.row;
    }

    /** @deprecated Использует стандартный размер доски */
    @Deprecated
    @Override
    public String toString() {
        return String.format("%c%d", 'a' + column, STD_BOARD_SIZE - row);
    }
}
