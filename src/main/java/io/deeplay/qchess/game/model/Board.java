package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.*;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.logics.MoveSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class Board {

    public final static int BOARD_SIZE = 8;
    private static final Logger logger = LoggerFactory.getLogger(Board.class);
    private Figure[][] cells = new Figure[BOARD_SIZE][BOARD_SIZE];

    /**
     * Создает пустую доску
     */
    public Board() {
    }

    /**
     * Заполняет доску
     */
    public void initBoard(MoveSystem ms, BoardFilling bf) throws ChessError {
        switch (bf) {
            case EMPTY -> {
                break;
            }
            case STANDARD -> {
                try {
                    setFigure(new Rook(this, true, Cell.parse("a1")));
                    setFigure(new Rook(this, true, Cell.parse("h1")));
                    setFigure(new Rook(this, false, Cell.parse("a8")));
                    setFigure(new Rook(this, false, Cell.parse("h8")));
                    setFigure(new Knight(this, true, Cell.parse("b1")));
                    setFigure(new Knight(this, true, Cell.parse("g1")));
                    setFigure(new Knight(this, false, Cell.parse("b8")));
                    setFigure(new Knight(this, false, Cell.parse("g8")));
                    setFigure(new Bishop(this, true, Cell.parse("c1")));
                    setFigure(new Bishop(this, true, Cell.parse("f1")));
                    setFigure(new Bishop(this, false, Cell.parse("c8")));
                    setFigure(new Bishop(this, false, Cell.parse("f8")));

                    setFigure(new Queen(this, true, Cell.parse("d1")));
                    setFigure(new Queen(this, false, Cell.parse("d8")));
                    setFigure(new King(this, true, Cell.parse("e1")));
                    setFigure(new King(this, false, Cell.parse("e8")));

                    setFigure(new Pawn(ms, this, true, Cell.parse("a2")));
                    setFigure(new Pawn(ms, this, true, Cell.parse("b2")));
                    setFigure(new Pawn(ms, this, true, Cell.parse("c2")));
                    setFigure(new Pawn(ms, this, true, Cell.parse("d2")));
                    setFigure(new Pawn(ms, this, true, Cell.parse("e2")));
                    setFigure(new Pawn(ms, this, true, Cell.parse("f2")));
                    setFigure(new Pawn(ms, this, true, Cell.parse("g2")));
                    setFigure(new Pawn(ms, this, true, Cell.parse("h2")));

                    setFigure(new Pawn(ms, this, false, Cell.parse("a7")));
                    setFigure(new Pawn(ms, this, false, Cell.parse("b7")));
                    setFigure(new Pawn(ms, this, false, Cell.parse("c7")));
                    setFigure(new Pawn(ms, this, false, Cell.parse("d7")));
                    setFigure(new Pawn(ms, this, false, Cell.parse("e7")));
                    setFigure(new Pawn(ms, this, false, Cell.parse("f7")));
                    setFigure(new Pawn(ms, this, false, Cell.parse("g7")));
                    setFigure(new Pawn(ms, this, false, Cell.parse("h7")));
                } catch (ChessException e) {
                    logger.error("Стандартное заполнение доски некорректное: {}", e.getMessage());
                    throw new ChessError("Стандартное заполнение доски некорректное");
                }
                break;
            }
            default -> {
                break;
            }
        }
    }

    /**
     * @param white цвет фигур, true - белые, false - черные
     * @return позиция короля определенного цвета
     * @throws ChessError если король не был найден
     */
    public Cell findKingCell(boolean white) throws ChessError {
        Cell kingCell = null;
        for (Figure[] f : cells) {
            for (Figure ff : f) {
                if (ff != null && ff.isWhite() == white && ff.getClass() == King.class) {
                    kingCell = ff.getCurrentPosition();
                    break;
                }
            }
        }
        if (kingCell == null) {
            logger.error("Возникла невозможная ситуация: король не найден");
            throw new ChessError("Король не найден");
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
                if (ff != null && ff.isWhite() == white) {
                    list.add(ff);
                }
            }
        }
        return list;
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
        Figure old = getFigure(move.getTo());
        figure.setCurrentPosition(move.getTo());
        setFigure(figure);
        figure.addMove(1);
        removeFigure(move.getFrom());
        return old;
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
     * @return удаленную фигуру или null, если клетка была пуста
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
     * @return true, если клетка принадлежит доске
     */
    public boolean isCorrectCell(int col, int row) {
        return col >= 0 && row >= 0 && col < BOARD_SIZE && row < BOARD_SIZE;
    }

    public static enum BoardFilling {
        EMPTY, STANDARD;
    }
}
