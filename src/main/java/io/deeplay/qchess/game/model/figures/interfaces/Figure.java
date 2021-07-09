package io.deeplay.qchess.game.model.figures.interfaces;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.PortUnreachableException;
import java.util.*;

public abstract class Figure {
    protected static final Logger log = LoggerFactory.getLogger(Figure.class);

    protected static List<Cell> xMove = Arrays.asList(
            new Cell(-1, -1),
            new Cell(-1, 1),
            new Cell(1, -1),
            new Cell(1, 1));
    protected static List<Cell> plusMove = Arrays.asList(
            new Cell(-1, 0),
            new Cell(0, -1),
            new Cell(0, 1),
            new Cell(1, 0));
    protected static List<Cell> knightMove = Arrays.asList(
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

    public Figure(Color color) {
        this.color = color;
    }

    /**
     * Устанавливает, делала ли фигура хотя бы один ход
     */
    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }

    /**
     * @return true, если фигура делала ход
     */
    public boolean wasMoved() {
        return wasMoved;
    }

    /**
     * @return все возможные ходы фигуры, не учитывая шаха
     */
    public abstract Set<Move> getAllMoves(Board board, Cell position);

    /**
     * @return тип фигуры
     */
    public  abstract TypeFigure getType();

    /**
     * @return цвет фигуры
     */
    public Color getColor() {
        return Color.WHITE;
    }

    protected Set<Move> rayTrace(Board board, Cell position, List<Cell> directions) {
        log.debug("Запущен рэйтрейс фигуры {} из точки {}", this, position);
        Objects.requireNonNull(directions, "Список ходов не может быть null");
        var result = new HashSet<Move>();
        for (Cell shift : directions) {
            Cell cord = position.createAdd(shift);
            while (board.isEmptyCell(cord)) {
                result.add(new Move(MoveType.SIMPLE_STEP, position, cord));
                cord = cord.createAdd(shift);
            }
            if (isEnemyFigureOn(board, cord)) {
                result.add(new Move(MoveType.ATTACK, position, cord));
            }
        }
        return result;
    }

    protected Set<Move> stepForEach(Board board, Cell position, List<Cell> moves) {
        log.debug("Запущено нахождение ходов фигуры {} из точки {}", this, position);
        Objects.requireNonNull(moves, "Список ходов не может быть null");
        var result = new HashSet<Move>();
        for (Cell shift : moves) {
            Cell cord = position.createAdd(shift);
            if (board.isEmptyCell(cord)) {
                result.add(new Move(MoveType.SIMPLE_STEP, position, cord));
            } else if (isEnemyFigureOn(board, cord)) {
                result.add(new Move(MoveType.ATTACK, position, cord));
            }
        }
        return result;
    }

    /**
     * @return true, если клетка лежит на доске и на этой клетке есть фигура, иначе false
     */
    protected boolean isEnemyFigureOn(Board board, Cell cell) {
        Figure enemyFigure;
        try {
            enemyFigure = board.getFigure(cell);
        } catch (ChessException e) {
            log.error(e.toString());
            return false;
        }
        return color != enemyFigure.getColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Figure figure = (Figure) o;
        return wasMoved == figure.wasMoved && color == figure.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, wasMoved);
    }
}