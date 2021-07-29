package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;
import static io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.game.exceptions.ChessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell {
    public static final transient int[][] hashCodes = new int[STD_BOARD_SIZE][STD_BOARD_SIZE];

    private static final transient Logger logger = LoggerFactory.getLogger(Cell.class);

    static {
        for (int i = 0; i < STD_BOARD_SIZE; ++i)
            for (int j = 0; j < STD_BOARD_SIZE; ++j)
                hashCodes[i][j] = (i * STD_BOARD_SIZE + j) * 10;
    }

    @SerializedName("column")
    public int column;

    @SerializedName("row")
    public int row;

    public Cell(final int column, final int row) {
        this.column = column;
        this.row = row;
    }

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
    public Cell shift(Cell shiftCell) {
        column += shiftCell.column;
        row += shiftCell.row;
        return this;
    }

    /** @deprecated Не использовать вне доски */
    @Deprecated
    @Override
    public int hashCode() {
        return hashCodes[column][row];
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
}
