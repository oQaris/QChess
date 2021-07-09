package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.figures.*;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;

import java.util.ArrayList;
import java.util.List;

public final class Board {

    public static final int BOARD_SIZE = 8;
    private final Figure[][] cells = new Figure[BOARD_SIZE][BOARD_SIZE];

    /**
     * Создает пустую доску
     */
    public Board() {
    }

    public Move getPrevMove() {
        //todo
        return null;
    }

    /**
     * @param color true - белый, false - черный
     * @return true, если клетка cell атакуется цветом color
     */
    public boolean isAttackedCell(Cell cell, Color color) {
        for (Figure f : getFigures(color)) {
            for (Move m : f.getClass() == King.class ? ((King) f).getAttackedMoves() : f.getAllMoves()) {
                if (m.getTo().equals(cell)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Заполняет доску
     */
    public void initBoard(MoveSystem ms, BoardFilling bf) throws ChessError {
        switch (bf) {
            case EMPTY -> {
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
                    setFigure(new King(ms, this, true, Cell.parse("e1")));
                    setFigure(new King(ms, this, false, Cell.parse("e8")));

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
                    throw new ChessError("Стандартное заполнение доски некорректное", e);
                }
            }
            default -> {
            }
        }
    }

    /**
     * @param color цвет игрока
     * @return позиция короля определенного цвета
     * @throws ChessError если король не был найден
     */
    public Cell findKingCell(Color color) throws ChessError {
        Cell kingCell = null;
        for (Figure[] f : cells) {
            for (Figure ff : f) {
                if (ff != null && ff.getColor() == color && ff.getClass() == King.class) {
                    kingCell = ff.getCurrentPosition();
                    break;
                }
            }
        }
        if (kingCell == null) {
            throw new ChessError("Король не найден");
        }
        return kingCell;
    }

    /**
     * @param color цвет игрока
     * @return фигуры определенного цвета
     */
    public List<Figure> getFigures(Color color) {
        List<Figure> list = new ArrayList<>(16);
        for (Figure[] figures : cells) {
            for (Figure figure : figures) {
                if (figure != null && figure.getColor() == color) {
                    list.add(figure);
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
        figure.setWasMoved(true);
        setFigure(figure);
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
        return cells[y][x];
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
        cells[y][x] = figure;
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
        Figure old = cells[y][x];
        cells[y][x] = null;
        return old;
    }

    /**
     * @return true, если клетка лежит на доске и она пустая, иначе false
     */
    public boolean isEmptyCell(Cell cell) {
        int x = cell.getCol();
        int y = cell.getRow();
        return isCorrectCell(x, y) && cells[y][x] == null;
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    public boolean isCorrectCell(int col, int row) {
        return col >= 0 && row >= 0 && col < BOARD_SIZE && row < BOARD_SIZE;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb/*.append(" - ".repeat(Board.BOARD_SIZE))*/.append("\n");
        for (Figure[] line : cells) {
            sb.append('|');
            for (Figure figure : line) {
                if (figure == null) {
                    sb.append("_");
                } else {
                    sb.append(figureToIcon(figure));
                }
                sb.append('|');
            }
            sb.append("\n")/*.append(" - ".repeat(Board.BOARD_SIZE)).append("\n")*/;
        }
        return sb.toString();
    }

    private char figureToIcon(Figure figure) {
        return switch (figure.getColor()) {
            // todo вставить норм символы
            case WHITE -> switch (figure.getType()) {
                case BISHOP -> '1';
                case KING -> '2';
                case KNIGHT -> '3';
                case PAWN -> '4';
                case QUEEN -> '5';
                case ROOK -> '6';
            };
            case BLACK -> switch (figure.getType()) {
                case BISHOP -> '7';
                case KING -> '8';
                case KNIGHT -> '9';
                case PAWN -> '0';
                case QUEEN -> '-';
                case ROOK -> '=';
            };
        };
    }

    public static enum BoardFilling {
        EMPTY, STANDARD
    }
}