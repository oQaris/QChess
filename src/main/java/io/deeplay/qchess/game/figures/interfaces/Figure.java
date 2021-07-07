package io.deeplay.qchess.game.figures.interfaces;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected final Board board;
    protected final boolean white;
    protected Cell pos;
    protected int countMoves = 0;

    public Figure(Board board, boolean white, Cell pos) {
        this.board = board;
        this.white = white;
        this.pos = pos;
    }

    public void addMove(int count) {
        countMoves += count;
    }

    /**
     * @return все варианты для перемещения фигуры, не выходящие за границы доски, учитывая уже занятые клетки
     */
    public abstract Set<Move> getAllMoves();

    public Board getBoard() {
        return board;
    }

    public boolean isWhite() {
        return white;
    }

    public Cell getCurrentPosition() {
        return pos;
    }

    public void setCurrentPosition(Cell pos) {
        this.pos = pos;
    }

    protected Set<Move> rayTrace(List<Cell> directions) {
        log.debug("Запущен рэйтрейс фигуры {} из точки {}", this, pos);
        if (directions == null) {
            throw new NullPointerException("Список ходов не может быть null");
        }
        var result = new HashSet<Move>();
        for (Cell shift : directions) {
            Cell cord = pos.add(shift);
            while (board.isEmptyCell(cord)) {
                result.add(new Move(MoveType.SIMPLE_STEP, pos, cord));
                cord = cord.add(shift);
            }
            if (isEnemyFigureOn(cord)) {
                result.add(new Move(MoveType.ATTACK, pos, cord));
            }
        }
        return result;
    }

    protected Set<Move> stepForEach(List<Cell> moves) {
        log.debug("Запущено нахождение ходов фигуры {} из точки {}", this, pos);
        if (moves == null) {
            throw new NullPointerException("Список ходов не может быть null");
        }
        var result = new HashSet<Move>();
        for (Cell shift : moves) {
            Cell cord = pos.add(shift);
            if (board.isEmptyCell(cord)) {
                result.add(new Move(MoveType.SIMPLE_STEP, pos, cord));
            } else if (isEnemyFigureOn(cord)) {
                result.add(new Move(MoveType.ATTACK, pos, cord));
            }
        }
        return result;
    }

    protected boolean isEnemyFigureOn(Cell cell) {
        Figure enemyFigure = null;
        try {
            enemyFigure = board.getFigure(cell);
        } catch (ChessException ignored) {
        }
        return enemyFigure != null && white != enemyFigure.isWhite();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.white ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.pos);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Figure f = (Figure) o;
        return white == f.white && Objects.equals(pos, f.pos);
    }
}
