package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.NotationService;
import io.deeplay.qchess.game.model.figures.*;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int BOARD_SIZE = 8;
    private final Figure[][] cells = new Figure[BOARD_SIZE][BOARD_SIZE];

    public Board(BoardFilling bf) throws ChessError {
        if (bf != BoardFilling.STANDARD) {
            return;
        }
        try {
            setFigure(new Rook(Color.WHITE, Cell.parse("a1")));
            setFigure(new Rook(Color.WHITE, Cell.parse("h1")));
            setFigure(new Rook(Color.BLACK, Cell.parse("a8")));
            setFigure(new Rook(Color.BLACK, Cell.parse("h8")));
            setFigure(new Knight(Color.WHITE, Cell.parse("b1")));
            setFigure(new Knight(Color.WHITE, Cell.parse("g1")));
            setFigure(new Knight(Color.BLACK, Cell.parse("b8")));
            setFigure(new Knight(Color.BLACK, Cell.parse("g8")));
            setFigure(new Bishop(Color.WHITE, Cell.parse("c1")));
            setFigure(new Bishop(Color.WHITE, Cell.parse("f1")));
            setFigure(new Bishop(Color.BLACK, Cell.parse("c8")));
            setFigure(new Bishop(Color.BLACK, Cell.parse("f8")));

            setFigure(new Queen(Color.WHITE, Cell.parse("d1")));
            setFigure(new Queen(Color.BLACK, Cell.parse("d8")));
            setFigure(new King(Color.WHITE, Cell.parse("e1")));
            setFigure(new King(Color.BLACK, Cell.parse("e8")));

            setFigure(new Pawn(Color.WHITE, Cell.parse("a2")));
            setFigure(new Pawn(Color.WHITE, Cell.parse("b2")));
            setFigure(new Pawn(Color.WHITE, Cell.parse("c2")));
            setFigure(new Pawn(Color.WHITE, Cell.parse("d2")));
            setFigure(new Pawn(Color.WHITE, Cell.parse("e2")));
            setFigure(new Pawn(Color.WHITE, Cell.parse("f2")));
            setFigure(new Pawn(Color.WHITE, Cell.parse("g2")));
            setFigure(new Pawn(Color.WHITE, Cell.parse("h2")));

            setFigure(new Pawn(Color.BLACK, Cell.parse("a7")));
            setFigure(new Pawn(Color.BLACK, Cell.parse("b7")));
            setFigure(new Pawn(Color.BLACK, Cell.parse("c7")));
            setFigure(new Pawn(Color.BLACK, Cell.parse("d7")));
            setFigure(new Pawn(Color.BLACK, Cell.parse("e7")));
            setFigure(new Pawn(Color.BLACK, Cell.parse("f7")));
            setFigure(new Pawn(Color.BLACK, Cell.parse("g7")));
            setFigure(new Pawn(Color.BLACK, Cell.parse("h7")));
        } catch (ChessException e) {
            throw new ChessError("Стандартное заполнение доски некорректное", e);
        }
    }

    public Board(String placement) throws ChessError {
        if (!NotationService.checkValidityPlacement(placement)) {
            throw new ChessError("Заполнение из доски из некорректной строки");
        }
        int y = 0;
        int x = 0;
        for (Character currentSymbol : placement.toCharArray()) {
            if(currentSymbol.equals('/')) {
                y++;
                x = 0;
            } else if(Character.isDigit(currentSymbol)) {
                x += Integer.getInteger(String.valueOf(currentSymbol));
            } else {
                try {
                    setFigure(NotationService.getFigureByChar(currentSymbol, x, y));
                } catch (ChessException e) {
                    throw new ChessError("Ошибка при установке фигуры на доску");
                }
            }
        }
    }

    /**
     * Устанавливает фигуру на доску
     */
    public void setFigure(Figure figure) throws ChessException {
        int x = figure.getCurrentPosition().getColumn();
        int y = figure.getCurrentPosition().getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        cells[y][x] = figure;
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    public boolean isCorrectCell(int col, int row) {
        return col >= 0 && row >= 0 && col < BOARD_SIZE && row < BOARD_SIZE;
    }

    /**
     * @return true, если клетка cell атакуется цветом color
     */
    public static boolean isAttackedCell(GameSettings settings, Cell cell, Color color) {
        for (Figure f : settings.board.getFigures(color)) {
            for (Move m :
                    f.getType() == TypeFigure.KING
                            ? ((King) f).getAttackedMoves(settings.board)
                            : f.getAllMoves(settings)) {
                if (m.getTo().equals(cell)) {
                    return true;
                }
            }
        }
        return false;
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
     * @param color цвет игрока
     * @return позиция короля определенного цвета
     * @throws ChessError если король не был найден
     */
    public Cell findKingCell(Color color) throws ChessError {
        Cell kingCell = null;
        for (Figure[] figures : cells) {
            for (Figure f : figures) {
                if (f != null && f.getColor() == color && f.getType() == TypeFigure.KING) {
                    kingCell = f.getCurrentPosition();
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
        int x = cell.getColumn();
        int y = cell.getRow();
        if (!isCorrectCell(x, y)) {
            throw new ChessException("Координаты выходят за границу доски");
        }
        return cells[y][x];
    }

    /**
     * Убирает фигуру с доски
     *
     * @return удаленную фигуру или null, если клетка была пуста
     */
    public Figure removeFigure(Cell cell) throws ChessException {
        int x = cell.getColumn();
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
        int x = cell.getColumn();
        int y = cell.getRow();
        return isCorrectCell(x, y) && cells[y][x] == null;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(System.lineSeparator());
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
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private char figureToIcon(Figure figure) {
        return switch (figure.getColor()) {
            case WHITE -> switch (figure.getType()) {
                case BISHOP -> "♝".toCharArray()[0];
                case KING -> "♚".toCharArray()[0];
                case KNIGHT -> "♞".toCharArray()[0];
                case PAWN -> "♟".toCharArray()[0];
                case QUEEN -> "♛".toCharArray()[0];
                case ROOK -> "♜".toCharArray()[0];
            };
            case BLACK -> switch (figure.getType()) {
                case BISHOP -> "♗".toCharArray()[0];
                case KING -> "♔".toCharArray()[0];
                case KNIGHT -> "♘".toCharArray()[0];
                case PAWN -> "♙".toCharArray()[0];
                case QUEEN -> "♕".toCharArray()[0];
                case ROOK -> "♖".toCharArray()[0];
            };
        };
    }

    public static enum BoardFilling {
        EMPTY, STANDARD
    }
}
