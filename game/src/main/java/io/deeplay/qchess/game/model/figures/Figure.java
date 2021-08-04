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
    private static final Logger logger = LoggerFactory.getLogger(Figure.class);
    protected static List<Cell> xMove =
            Arrays.asList(new Cell(-1, -1), new Cell(-1, 1), new Cell(1, -1), new Cell(1, 1));
    protected static List<Cell> plusMove =
            Arrays.asList(new Cell(-1, 0), new Cell(0, -1), new Cell(0, 1), new Cell(1, 0));
    protected static List<Cell> knightMove =
            Arrays.asList(
                    new Cell(-2, -1),
                    new Cell(-2, 1),
                    new Cell(-1, -2),
                    new Cell(-1, 2),
                    new Cell(1, -2),
                    new Cell(1, 2),
                    new Cell(2, -1),
                    new Cell(2, 1));

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
    protected boolean wasMoved = false;
    protected Cell position;

    protected Figure(Color color, Cell position, FigureType figureType) {
        this.color = color;
        this.position = position;
        this.figureType = figureType;
        logger.trace("Фигура {} была создана", this);
    }

    public static Figure build(FigureType type, Color color, Cell position) {
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

    public void setCurrentPosition(Cell position) {
        this.position = position;
    }

    /** Устанавливает, делала ли фигура хотя бы один ход */
    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }

    /** @return true, если фигура делала ход */
    public boolean wasMoved() {
        return wasMoved;
    }

    /** @return все возможные ходы фигуры, не учитывая шаха */
    public abstract List<Move> getAllMoves(GameSettings settings);

    protected List<Move> rayTrace(Board board, List<Cell> directions) {
        List<Move> result = new LinkedList<>();
        for (Cell shift : directions) {
            Cell cord = position.createAdd(shift);
            while (board.isEmptyCell(cord)) {
                result.add(new Move(MoveType.QUIET_MOVE, position, cord));
                cord = cord.createAdd(shift);
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

    protected List<Move> stepForEach(Board board, List<Cell> moves) {
        return stepForEachWithNewCell(board, moves, false);
    }

    protected List<Move> stepForEachWithNewCell(
            Board board, List<Cell> moves, boolean withNewCell) {
        Cell newCell = withNewCell ? new Cell(position.column, position.row) : position;
        List<Move> result = new LinkedList<>();
        for (Cell shift : moves) {
            Cell cord = position.createAdd(shift);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Figure figure = (Figure) o;
        return wasMoved == figure.wasMoved
                && color == figure.color
                && Objects.equals(position, figure.position);
    }

    @Override
    public String toString() {
        return String.join(" ", color.toString(), FigureType.nameOfTypeNumber[figureType.type]);
    }
}
