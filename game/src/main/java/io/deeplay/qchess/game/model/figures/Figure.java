package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Figure {
    public static final int[][][][] hashCodes = new int[2][2][8][8];
    protected static final List<Cell> xMove =
            Arrays.asList(new Cell(-1, -1), new Cell(-1, 1), new Cell(1, -1), new Cell(1, 1));
    protected static final List<Cell> plusMove =
            Arrays.asList(new Cell(-1, 0), new Cell(0, -1), new Cell(0, 1), new Cell(1, 0));
    protected static final List<Cell> xPlusMove =
            Arrays.asList(
                    new Cell(-1, -1),
                    new Cell(-1, 1),
                    new Cell(1, -1),
                    new Cell(1, 1),
                    new Cell(-1, 0),
                    new Cell(0, -1),
                    new Cell(0, 1),
                    new Cell(1, 0));
    protected static final List<Cell> knightMove =
            Arrays.asList(
                    new Cell(-2, -1),
                    new Cell(-2, 1),
                    new Cell(-1, -2),
                    new Cell(-1, 2),
                    new Cell(1, -2),
                    new Cell(1, 2),
                    new Cell(2, -1),
                    new Cell(2, 1));

    private static final transient Logger logger = LoggerFactory.getLogger(Figure.class);

    static {
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                hashCodes[0][0][i][j] = Cell.hashCodes[i][j];
                hashCodes[0][1][i][j] = Cell.hashCodes[i][j] + 1;
                hashCodes[1][0][i][j] = Cell.hashCodes[i][j] + 2;
                hashCodes[1][1][i][j] = Cell.hashCodes[i][j] + 3;
            }
    }

    public final FigureType figureType;
    protected final Color color;
    /** Двигалась ли фигура на доске */
    public boolean wasMoved;

    protected Cell position;

    protected Figure(final Color color, final Cell position, final FigureType figureType) {
        this.color = color;
        this.position = position;
        this.figureType = figureType;
        logger.trace("Фигура {} была создана", this);
    }

    public static Figure build(final FigureType type, final Color color, final Cell position) {
        return switch (type) {
            case BISHOP -> new Bishop(color, position);
            case KING -> new King(color, position);
            case KNIGHT -> new Knight(color, position);
            case PAWN -> new Pawn(color, position);
            case QUEEN -> new Queen(color, position);
            case ROOK -> new Rook(color, position);
        };
    }

    public Cell getCurrentPosition() {
        return position;
    }

    public void setCurrentPosition(final Cell position) {
        this.position = position;
    }

    /** @return все возможные ходы фигуры, не учитывая шаха */
    public abstract List<Move> getAllMoves(GameSettings settings);

    protected List<Move> rayTrace(final Board board, final List<Cell> directions) {
        final List<Move> result = new LinkedList<>();
        for (final Cell shift : directions) {
            final Cell cord = position.createAdd(shift);
            while (board.isEmptyCell(cord)) {
                result.add(new Move(MoveType.QUIET_MOVE, position, new Cell(cord)));
                cord.shift(shift);
            }
            if (board.isEnemyFigureOn(color, cord))
                result.add(new Move(MoveType.ATTACK, position, cord));
        }
        return result;
    }

    /** @return цвет фигуры */
    public Color getColor() {
        return color;
    }

    protected List<Move> stepForEach(
            final Board board, final List<Cell> moves, final boolean withNewCell) {
        final Cell newCell = withNewCell ? new Cell(position.column, position.row) : position;
        final List<Move> result = new LinkedList<>();
        for (final Cell shift : moves) {
            final Cell cord = position.createAdd(shift);
            if (board.isEmptyCell(cord)) result.add(new Move(MoveType.QUIET_MOVE, newCell, cord));
            else if (board.isEnemyFigureOn(color, cord))
                result.add(new Move(MoveType.ATTACK, newCell, cord));
        }
        return result;
    }

    public abstract boolean isAttackedCell(GameSettings settings, Cell cell);

    @Override
    public int hashCode() {
        return hashCodes[color == Color.WHITE ? 0 : 1][wasMoved ? 0 : 1][position.column][
                position.row];
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Figure figure = (Figure) o;
        return wasMoved == figure.wasMoved
                && color == figure.color
                && Objects.equals(position, figure.position);
    }

    @Override
    public String toString() {
        return String.join(" ", color.toString(), FigureType.nameOfTypeNumber[figureType.type]);
    }
}
