package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_STRING_FOR_FILLING_BOARD;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.service.NotationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Board {
    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    /** @deprecated Плохо для гибкости */
    @Deprecated public static int STD_BOARD_SIZE = 8;

    public final int boardSize;
    private final Figure[][] cells;
    public Cell blackKing;
    public Cell whiteKing;

    public Board(int size, BoardFilling fillingType) {
        boardSize = size;
        cells = new Figure[boardSize][boardSize];
        try {
            fill(fillingType);
        } catch (ChessException e) {
            logger.error("Ошибка при заполнении доски");
        }
    }

    public Board(BoardFilling fillingType) {
        this(STD_BOARD_SIZE, fillingType);
    }

    public Board(String placement) throws ChessError {
        this(STD_BOARD_SIZE, BoardFilling.EMPTY);
        try {
            if (!NotationService.checkValidityPlacement(placement)) {
                logger.error(
                        "Ошибка при парсинге строки для конструктора доски (строка не валидна)");
                throw new ChessError(INCORRECT_STRING_FOR_FILLING_BOARD);
            }
            int y = 0;
            int x = 0;
            for (Character currentSymbol : placement.toCharArray()) {
                if (currentSymbol.equals('/')) {
                    ++y;
                    x = 0;
                } else if (Character.isDigit(currentSymbol)) {
                    x += currentSymbol - 48;
                } else {
                    setFigure(NotationService.getFigureByChar(currentSymbol, x, y));
                    ++x;
                }
            }
        } catch (ChessException e) {
            logger.error("Ошибка при установке фигуры на доску в конструкторе доски по строке");
            throw new ChessError(INCORRECT_COORDINATES);
        }
    }

    /** Создает копию доски, включая копии фигур на ней */
    public Board(Board board) {
        this(board.boardSize, BoardFilling.EMPTY);
        for (int y = 0; y < boardSize; ++y)
            for (int x = 0; x < boardSize; ++x)
                cells[y][x] =
                        Figure.build(
                                board.cells[y][x].getType(),
                                board.cells[y][x].getColor(),
                                new Cell(x, y));
    }

    /** @return true, если клетка cell атакуется цветом color */
    public static boolean isAttackedCell(GameSettings settings, Cell cell, Color color) {
        for (Figure f : settings.board.getFigures(color))
            if (f.isAttackedCell(settings, cell)) return true;
        return false;
    }

    private static char figureToIcon(Color color, FigureType figure) {
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

    private void fill(BoardFilling fillingType) throws ChessException {
        logger.debug("Начато заполнение {} доски", fillingType);
        switch (fillingType) {
            case STANDARD -> fillBoardForFirstLine(
                    new FigureType[] {
                        FigureType.ROOK, FigureType.KNIGHT, FigureType.BISHOP,
                        FigureType.QUEEN, FigureType.KING, FigureType.BISHOP,
                        FigureType.KNIGHT, FigureType.ROOK
                    });
            case CHESS960 -> fillBoardForFirstLine(
                    // todo Добавить рандома
                    new FigureType[] {
                        FigureType.KNIGHT, FigureType.QUEEN, FigureType.ROOK,
                        FigureType.KING, FigureType.BISHOP, FigureType.ROOK,
                        FigureType.KNIGHT, FigureType.BISHOP
                    });
        }
        logger.debug("Доска {} инициализирована", fillingType);
    }

    /** @return true, если клетка принадлежит доске */
    @Deprecated
    public boolean isCorrectCell(int column, int row) {
        return column >= 0 && row >= 0 && column < boardSize && row < boardSize;
    }

    private void fillBoardForFirstLine(FigureType[] orderFirstLine) throws ChessException {
        Cell startBlack = new Cell(0, 0);
        Cell startWhite = new Cell(0, boardSize - 1);
        Cell shift = new Cell(1, 0);

        for (FigureType figureType : orderFirstLine) {
            setFigure(Figure.build(figureType, Color.BLACK, startBlack));
            setFigure(new Pawn(Color.BLACK, startBlack.createAdd(new Cell(0, 1))));
            setFigure(Figure.build(figureType, Color.WHITE, startWhite));
            setFigure(new Pawn(Color.WHITE, startWhite.createAdd(new Cell(0, -1))));
            startBlack = startBlack.createAdd(shift);
            startWhite = startWhite.createAdd(shift);
        }
    }

    /** Устанавливает фигуру на доску */
    @Deprecated
    public void setFigure(Figure figure) throws ChessException {
        Cell position = figure.getCurrentPosition();
        if (!isCorrectCell(position.column, position.row)) {
            logger.warn("Ошибка установки фигуры {} на доску", figure);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        cells[position.row][position.column] = figure;
        if (figure.getType() == FigureType.KING) {
            if (figure.getColor() == Color.WHITE) whiteKing = figure.getCurrentPosition();
            else blackKing = figure.getCurrentPosition();
        }
        logger.trace("Фигура {} установлена на доску", figure);
    }

    /** Устанавливает фигуру на доску БЕЗ ПРОВЕРОК */
    public void setFigureUgly(Figure figure) throws ArrayIndexOutOfBoundsException {
        Cell position = figure.getCurrentPosition();
        cells[position.row][position.column] = figure;
    }

    /**
     * @param color цвет игрока
     * @return фигуры определенного цвета
     */
    public List<Figure> getFigures(Color color) {
        List<Figure> list = new ArrayList<>(16);
        for (Figure[] figures : cells)
            for (Figure figure : figures)
                if (figure != null && figure.getColor() == color) list.add(figure);
        return list;
    }

    /**
     * @param color цвет игрока
     * @return позиция короля определенного цвета или null, если король не найден
     */
    public Figure findKing(Color color) {
        return color == Color.WHITE
                ? cells[whiteKing.row][whiteKing.column]
                : cells[blackKing.row][blackKing.column];
    }

    public Cell findKingCell(Color color) {
        return color == Color.WHITE ? whiteKing : blackKing;
    }

    /**
     * @param color цвет ладьи
     * @return левая ладья в углу для длинной рокировки цвета color или null, если не найдена
     */
    public Figure findLeftRookStandard(Color color) {
        Figure rook = cells[(color == Color.BLACK ? 0 : boardSize - 1)][0];
        if (rook != null && rook.getType() == FigureType.ROOK && rook.getColor() == color)
            return rook;
        return null;
    }

    /**
     * @param color цвет ладьи
     * @return правая ладья в углу для короткой рокировки цвета color или null, если не найдена
     */
    public Figure findRightRookStandard(Color color) {
        Figure rook = cells[(color == Color.BLACK ? 0 : boardSize - 1)][boardSize - 1];
        if (rook != null && rook.getType() == FigureType.ROOK && rook.getColor() == color)
            return rook;
        return null;
    }

    /**
     * Перемещает фигуру с заменой старой, даже если ход некорректный. При срублении фигуры,
     * возвращается эта фигура без изменения собственных координат.
     *
     * @return предыдущая фигура на месте перемещения или null, если клетка была пуста
     * @throws ChessException если ход выходит за пределы доски
     */
    @Deprecated
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
     * Перемещает фигуру без проверок и установки флагов перемещения
     *
     * @return предыдущая фигура на месте перемещения или null, если клетка была пуста
     */
    public Figure moveFigureUgly(Move move) throws ArrayIndexOutOfBoundsException {
        Figure figureFrom = getFigureUgly(move.getFrom());
        Figure figureTo = getFigureUgly(move.getTo());
        figureFrom.setCurrentPosition(move.getTo());
        setFigureUgly(figureFrom);
        removeFigureUgly(move.getFrom());
        return figureTo;
    }

    /**
     * @return фигура или null, если клетка пуста
     * @throws ChessException если клетка не лежит в пределах доски
     */
    @Deprecated
    public Figure getFigure(Cell cell) throws ChessException {
        int x = cell.column;
        int y = cell.row;
        if (x < 0 || y < 0 || x >= boardSize || y >= boardSize) {
            logger.warn("Фигура не была установлена на клетку: {}", cell);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        return cells[cell.row][cell.column];
    }

    /**
     * Опасно! Проверки не выполняются.
     *
     * @return фигура или null, если клетка пуста.
     */
    public Figure getFigureUgly(Cell cell) throws ArrayIndexOutOfBoundsException {
        return cells[cell.row][cell.column];
    }

    /**
     * Убирает фигуру с доски
     *
     * @return удаленную фигуру или null, если клетка была пуста
     */
    @Deprecated
    public Figure removeFigure(Cell cell) throws ChessException {
        if (!isCorrectCell(cell.column, cell.row)) {
            logger.warn("Фигура не была удалена с клетки: {}", cell);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        Figure old = cells[cell.row][cell.column];
        cells[cell.row][cell.column] = null;
        return old;
    }

    /**
     * Убирает фигуру с доски БЕЗ ПРОВЕРОК
     *
     * @return удаленную фигуру или null, если клетка была пуста
     */
    public Figure removeFigureUgly(Cell cell) throws ArrayIndexOutOfBoundsException {
        Figure old = cells[cell.row][cell.column];
        cells[cell.row][cell.column] = null;
        return old;
    }

    /** @return true, если клетка лежит на доске и она пустая, иначе false */
    public boolean isEmptyCell(Cell cell) {
        try {
            return cells[cell.row][cell.column] == null;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * @param color цвет своей фигуры
     * @return true, если клетка лежит на доске и на этой клетке есть фражеская фигура, иначе false
     */
    public boolean isEnemyFigureOn(Color color, Cell cell) {
        try {
            return cells[cell.row][cell.column].getColor() != color;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return false;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return boardSize == board.boardSize && Arrays.deepEquals(cells, board.cells);
    }

    @Override
    public int hashCode() {
        int h = boardSize;
        for (int yl = 0, yr = boardSize - 1; yl < yr; ++yl, --yr) {
            for (int xl = 0, xr = boardSize - 1; xl < xr; ++xl, --xr) {
                h = 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * (cells[yl][xl] == null ? 0 : cells[yl][xl].hashCode())
                    + 31 * 31 * (cells[yl][xr] == null ? 0 : cells[yl][xr].hashCode())
                    + 31 * (cells[yr][xl] == null ? 0 : cells[yr][xl].hashCode())
                    + (cells[yr][xr] == null ? 0 : cells[yr][xr].hashCode());
            }
        }
        return h;
    }

    public enum BoardFilling {
        EMPTY,
        STANDARD,
        CHESS960
    }
}
