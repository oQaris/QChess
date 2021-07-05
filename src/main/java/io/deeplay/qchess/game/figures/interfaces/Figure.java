package io.deeplay.qchess.game.figures.interfaces;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Figure implements IFigure {
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

    public Figure(Board board, boolean white, Cell pos) {
        this.board = board;
        this.white = white;
        this.pos = pos;
    }

    public Board getBoard() {
        return board;
    }

    @Override
    public boolean isWhite() {
        return white;
    }

    @Override
    public Cell getCurrentPosition() {
        return pos;
    }

    protected Set<Cell> rayTrace(@NotNull List<Cell> moves) {
        var result = new HashSet<Cell>();
        try {
            for (Cell shift : moves) {
                Cell cord = pos.add(shift);
                while (board.isEmptyCell(cord)) {
                    result.add(cord);
                    cord = cord.add(shift);
                }
                //todo можно сделать добавление в другое множество
                var endFigure = board.getFigure(cord);
                if (endFigure != null && white != endFigure.isWhite())
                    result.add(cord);
            }
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return result;
    }
}