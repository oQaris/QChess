package io.deeplay.qchess.game.figures.interfaces;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Figure {
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
    protected boolean isFirstMove = true;

    public void madeFirstMove() {
        isFirstMove = false;
    }

    public Figure(Board board, boolean white, Cell pos) {
        this.board = board;
        this.white = white;
        this.pos = pos;
    }

    public abstract Set<Cell> getAllMovePositions();

    public Board getBoard() {
        return board;
    }

    public boolean isWhite() {
        return white;
    }

    public Cell getCurrentPosition() {
        return pos;
    }

    protected Set<Cell> rayTrace(List<Cell> moves) {
        if (moves == null) {
            throw new NullPointerException("Список ходов не может быть null");
        }
        var result = new HashSet<Cell>();
        for (Cell shift : moves) {
            Cell cord = pos.add(shift);
            while (board.isEmptyCell(cord)) {
                result.add(cord);
                cord = cord.add(shift);
            }
            //todo можно сделать добавление в другое множество
            if (isEnemyFigureOn(cord)) {
                result.add(cord);
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
}
