package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_FILLING_BOARD;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_NOT_FOUND;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Board {
    public static final int BOARD_SIZE = 8;
    private static final Logger logger = LoggerFactory.getLogger(Board.class);
    private final Figure[][] cells = new Figure[Board.BOARD_SIZE][Board.BOARD_SIZE];

    public Board(BoardFilling bf) throws ChessError {
        logger.debug("Начато заполнение {} доски", bf);
        try {
            switch (bf) {
                case STANDARD -> {
                    TypeFigure[] orderFirstLine = new TypeFigure[]{TypeFigure.ROOK, TypeFigure.KNIGHT, TypeFigure.BISHOP,
                            TypeFigure.QUEEN, TypeFigure.KING, TypeFigure.BISHOP,TypeFigure.KNIGHT,TypeFigure.ROOK};

                    Cell startBlack = new Cell(0, 0);
                    Cell startWhite = new Cell(0, Board.BOARD_SIZE - 1);
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
        } catch (ChessException | NullPointerException e) {
            logger.error("Ошибка при заполнении доски");
            throw new ChessError(INCORRECT_FILLING_BOARD, e);
        }
        logger.debug("Доска {} инициализирована", bf);
    }

    /**
     * @return true, если клетка принадлежит доске
     */
    public static boolean isCorrectCell(int column, int row) {
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

    private static char figureToIcon(Color color, TypeFigure figure) {
        return switch (color) {
            case WHITE -> switch (figure) {
                case BISHOP -> '♝';
                case KING -> '♚';
                case KNIGHT -> '♞';
                case PAWN -> '♟';
                case QUEEN -> '♛';
                case ROOK -> '♜';
            };
            case BLACK -> switch (figure) {
                case BISHOP -> '♗';
                case KING -> '♔';
                case KNIGHT -> '♘';
                case PAWN -> '♙';
                case QUEEN -> '♕';
                case ROOK -> '♖';
            };
        };
    }

    /**
     * Устанавливает фигуру на доску
     */
    public void setFigure(Figure figure) throws ChessException {
        int x = figure.getCurrentPosition().getColumn();
        int y = figure.getCurrentPosition().getRow();
        if (!Board.isCorrectCell(x, y)) {
            logger.warn("Ошибка установки фигуры {} на доску", figure);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        cells[y][x] = figure;
        logger.trace("Фигура {} установлена на доску", figure);
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
        if (kingCell == null) {
            logger.error("Король {} не был найден", color);
            throw new ChessError(KING_NOT_FOUND);
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
        logger.trace("Начато перемещение фигуры: {}", move);
        Figure figureFrom = getFigure(move.getFrom());
        Figure figureTo = getFigure(move.getTo());
        figureFrom.setCurrentPosition(move.getTo());
        figureFrom.setWasMoved(true);
        setFigure(figureFrom);
        removeFigure(move.getFrom());
        logger.trace("Фигура была перемещена: {}", move);
        return figureTo;
    }

    /**
     * @return фигура или null, если клетка пуста
     * @throws ChessException если клетка не лежит в пределах доски
     */
    public Figure getFigure(Cell cell) throws ChessException {
        int x = cell.getColumn();
        int y = cell.getRow();
        if (!Board.isCorrectCell(x, y)) {
            logger.warn("Фигура не была установлена на клетку: {}", cell);
            throw new ChessException(INCORRECT_COORDINATES);
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
        if (!Board.isCorrectCell(x, y)) {
            logger.warn("Фигура не была удалена с клетки: {}", cell);
            throw new ChessException(INCORRECT_COORDINATES);
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
        return Board.isCorrectCell(x, y) && cells[y][x] == null;
    }

    /**
     * @param color цвет своей фигуры
     * @return true, если клетка лежит на доске и на этой клетке есть фражеская фигура, иначе false
     */
    public boolean isEnemyFigureOn(Color color, Cell cell) {
        int x = cell.getColumn();
        int y = cell.getRow();
        return Board.isCorrectCell(x, y) && cells[y][x] != null && cells[y][x].getColor() != color;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (Figure[] line : cells) {
            sb.append('|');
            for (Figure figure : line) {
                if (figure == null) sb.append("_");
                else sb.append(Board.figureToIcon(figure.getColor(), figure.getType()));
                sb.append('|');
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public enum BoardFilling {
        EMPTY, STANDARD, CHESS960
    }
}
