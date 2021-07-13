package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.ArrayList;
import java.util.List;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.*;

public class Board {
    public static final int BOARD_SIZE = 8;
    private final Figure[][] cells = new Figure[Board.BOARD_SIZE][Board.BOARD_SIZE];

    public Board(BoardFilling bf) throws ChessError {
        try {
            switch (bf) {
                case STANDARD -> {
                    TypeFigure[] orderFirstLine = new TypeFigure[]{TypeFigure.ROOK, TypeFigure.KNIGHT, TypeFigure.BISHOP,
                            TypeFigure.QUEEN, TypeFigure.KING, TypeFigure.BISHOP,TypeFigure.KNIGHT,TypeFigure.ROOK};

                    Cell startBlack = new Cell(0, 0);
                    Cell startWhite = new Cell( 0, Board.BOARD_SIZE-1);
                    Cell shift = new Cell(1, 0);

                    for (TypeFigure typeFigure : orderFirstLine) {
                        setFigure(Figure.build(typeFigure, Color.BLACK, startBlack));
                        setFigure(new Pawn(Color.BLACK, startBlack.createAdd(new Cell(0, 1))));
                        setFigure(Figure.build(typeFigure, Color.WHITE, startWhite));
                        setFigure(new Pawn(Color.WHITE, startWhite.createAdd(new Cell(0, -1))));
                        startBlack = startBlack.createAdd(shift);
                        startWhite = startWhite.createAdd(shift);
                    }
                }
                case CHESS960 -> {
                    //todo Сделать рандомное заполнение Фишера
                }
                case EMPTY -> { }
            }
        } catch (ChessException e) {
            throw new ChessError(INCORRECT_FILLING_BOARD, e);
        }
    }

    /**
     * Устанавливает фигуру на доску
     */
    public void setFigure(Figure figure) throws ChessException {
        int x = figure.getCurrentPosition().getColumn();
        int y = figure.getCurrentPosition().getRow();
        if (!Board.isCorrectCell(x, y)) throw new ChessException(INCORRECT_COORDINATES);
        cells[y][x] = figure;
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    static boolean isCorrectCell(int column, int row) {
        return column >= 0 && row >= 0 && column < Board.BOARD_SIZE && row < Board.BOARD_SIZE;
    }

    /**
     * @return true, если клетка cell атакуется цветом color
     */
    public static boolean isAttackedCell(GameSettings settings, Cell cell, Color color) {
        for (Figure f : settings.board.getFigures(color))
            for (Move m :
                    f.getType() == TypeFigure.KING
                            ? ((King) f).getAttackedMoves(settings.board)
                            : f.getAllMoves(settings))
                if (m.getTo().equals(cell)) return true;
        return false;
    }

    /**
     * @param color цвет игрока
     * @return фигуры определенного цвета
     */
    public List<Figure> getFigures(Color color) {
        List<Figure> list = new ArrayList<>(16);
        for (Figure[] figures : cells)
            for (Figure figure : figures)
                if (figure != null && figure.getColor() == color)
                    list.add(figure);
        return list;
    }

    private static void checkCell(int col, int row) throws ChessException {
        if (!Board.isCorrectCell(col, row)) throw new ChessException(INCORRECT_COORDINATES);
    }

    /**
     * @param color цвет игрока
     * @return позиция короля определенного цвета
     * @throws ChessError если король не был найден
     */
    public Cell findKingCell(Color color) throws ChessError {
        Cell kingCell = null;
        for (Figure[] figures : cells)
            for (Figure f : figures)
                if (f != null && f.getColor() == color && f.getType() == TypeFigure.KING) {
                    kingCell = f.getCurrentPosition();
                    break;
                }
        if (kingCell == null) throw new ChessError(KING_NOT_FOUND);
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
        Figure figureFrom = getFigure(move.getFrom());
        Figure figureTo = getFigure(move.getTo());
        figureFrom.setCurrentPosition(move.getTo());
        figureFrom.setWasMoved(true);
        setFigure(figureFrom);
        removeFigure(move.getFrom());
        return figureTo;
    }

    /**
     * @return фигура или null, если клетка пуста
     * @throws ChessException если клетка не лежит в пределах доски
     */
    public Figure getFigure(Cell cell) throws ChessException {
        int x = cell.getColumn();
        int y = cell.getRow();
        Board.checkCell(x, y);
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
        Board.checkCell(x, y);
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
        return Board.isCorrectCell(x, y) && cells[y][x] == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (Figure[] line : cells) {
            sb.append('|');
            for (Figure figure : line) {
                if (figure == null) sb.append("_");
                else sb.append(Board.figureToIcon(figure));
                sb.append('|');
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static char figureToIcon(Figure figure) {
        return switch (figure.getColor()) {
            case WHITE -> switch (figure.getType()) {
                case BISHOP -> '♝';
                case KING -> '♚';
                case KNIGHT -> '♞';
                case PAWN -> '♟';
                case QUEEN -> '♛';
                case ROOK -> '♜';
            };
            case BLACK -> switch (figure.getType()) {
                case BISHOP -> '♗';
                case KING -> '♔';
                case KNIGHT -> '♘';
                case PAWN -> '♙';
                case QUEEN -> '♕';
                case ROOK -> '♖';
            };
        };
    }

    public enum BoardFilling {
        EMPTY, STANDARD, CHESS960
    }
}
