package io.deeplay.qchess.game.model.figures.interfaces;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.Rook;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Figure {
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

    protected final Color color;
    protected boolean wasMoved = false;
    protected Cell position;

    protected Figure(Color color, Cell position) {
        this.color = color;
        this.position = position;
        logger.debug("Фигура {} была создана", this);
    }

    public static Figure build(TypeFigure type, Color color, Cell position){
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
    public abstract Set<Move> getAllMoves(GameSettings settings);

    protected Set<Move> rayTrace(Board board, List<Cell> directions) {
        logger.trace("Запущен рэйтрейс фигуры {} из точки {}", this, position);
        Objects.requireNonNull(directions, "Список ходов не может быть null");
        Set<Move> result = new HashSet<>();
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

    protected Set<Move> stepForEach(Board board, List<Cell> moves) {
        logger.trace("Запущено нахождение ходов фигуры {} из точки {}", this, position);
        Objects.requireNonNull(moves, "Список ходов не может быть null");
        Set<Move> result = new HashSet<>();
        for (Cell shift : moves) {
            Cell cord = position.createAdd(shift);
            if (board.isEmptyCell(cord)) result.add(new Move(MoveType.QUIET_MOVE, position, cord));
            else if (board.isEnemyFigureOn(color, cord))
                result.add(new Move(MoveType.ATTACK, position, cord));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, wasMoved, position);
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
        return color.toString() + " " + getType();
    }

    /**
     * @return тип фигуры
     */
    public abstract TypeFigure getType();
}
