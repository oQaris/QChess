package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.King;
import io.deeplay.qchess.game.figures.interfaces.Figure;

import java.util.*;

public final class Board /*implements Iterable<Figure>*/ {

    public final static int BOARD_SIZE = 8;
    private Figure[][] cells = new Figure[BOARD_SIZE][BOARD_SIZE];

    public Board() {

    }

    /**
     * @param white цвет фигур, true - белые, false - черные
     * @return позиция короля определенного цвета
     */
    public Cell findKingCell(boolean white) {
        Cell kingCell = null;
        for (Figure[] f : cells) {
            for (Figure ff : f) {
                if (ff == null) {
                    continue;
                }
                if (ff.isWhite() == white && ff.getClass() == King.class) {
                    kingCell = ff.getCurrentPosition();
                    break;
                }
            }
        }
        return kingCell;
    }

    /**
     * @param white цвет фигур, true - белые, false - черные
     * @return фигуры определенного цвета
     */
    public List<Figure> getFigures(boolean white) {
        List<Figure> list = new ArrayList<>();
        for (Figure[] f : cells) {
            for (Figure ff : f) {
                if (ff == null) {
                    continue;
                }
                if (ff.isWhite() == white) {
                    list.add(ff);
                }
            }
        }
        return list;
    }

    /**
     * @return фигура или null, если клетка пуста
     * @throws ChessException если клетка не лежит в пределах доски
     */
    public Figure getFigure(Cell cell) throws ChessException {
        int x = cell.getCol();
        int y = cell.getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        return cells[x][y];
    }

    /**
     * Устанавливает фигуру на доску
     */
    public void setFigure(Figure figure) throws ChessException {
        int x = figure.getCurrentPosition().getCol();
        int y = figure.getCurrentPosition().getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        cells[x][y] = figure;
    }

    /**
     * Убирает фигуру с доски
     *
     * @return удаленную фигуру
     */
    public Figure removeFigure(Cell cell) throws ChessException {
        int x = cell.getCol();
        int y = cell.getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        Figure old = cells[x][y];
        cells[x][y] = null;
        return old;
    }

    public boolean isEmptyCell(Cell cell) {
        int x = cell.getCol();
        int y = cell.getRow();
        return isCorrectCell(x, y) && cells[x][y] == null;
    }

    /**
     * @return true, если данная фигура ещё не делала ходов
     */
    public boolean isNotMakeMoves(Figure figure) {
        return true;
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    public boolean isCorrectCell(int col, int row) {
        return col >= 0 && row >= 0 && col < BOARD_SIZE && row < BOARD_SIZE;
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    public boolean isCorrectCell(Cell cell) {
        return isCorrectCell(cell.getCol(), cell.getRow());
    }

    /**
     * Перемещает фигуру с заменой старой, даже если ход некорректный.
     * При срублении фигуры, возвращается эта фигура без изменения собственных координат.
     *
     * @return предыдущая фигура на месте перемещения или null, если клетка была пуста
     * @throws ChessException если ход выходит за пределы доски
     */
    public Figure moveFigure(Move move) throws ChessException {
        Figure figure = getFigure(move.getFrom());
        Figure oldFigure = getFigure(move.getTo());

        figure.setCurrentPosition(move.getTo());
        setFigure(figure);

        cells[move.getFrom().getCol()][move.getFrom().getRow()] = null;

        return oldFigure;
    }

    public Set<Move> getAllMoves(boolean color){
        //todo надо сделать как то
        return new HashSet<>();
    }

    // не, это отстой какой то, надо хранить множество фигур
    /*@Override
    public Iterator<Figure> iterator() {
        return null;
    }

    private class FigIterator implements Iterator<Figure> {
        private Cell curPos = new Cell(0, 0);

        @Override
        public boolean hasNext() {
            if (curPos.getCol() == BOARD_SIZE)
                return false;
        }

        @Override
        public Figure next() {
            try {
                return getFigure(curPos);
                curPos = curPos.add(new Cell(0, 1));
                if (curPos.getCol() == BOARD_SIZE)
            } catch (ChessException ignored) {
            }
            return null;
        }
    }*/
}