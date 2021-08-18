package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_STRING_FOR_FILLING_BOARD;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_NOT_FOUND;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.math.GameMath;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.service.NotationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Board {
    public static final int STD_BOARD_SIZE = 8;

    private static final transient Logger logger = LoggerFactory.getLogger(Board.class);

    public final int boardSize;
    private final Figure[][] cells;
    private final int[] cellsType;
    public Cell blackKing;
    public Cell whiteKing;
    private int cellsTypeHash;

    public Board(final int size, final BoardFilling fillingType) {
        boardSize = size;
        cells = new Figure[boardSize][boardSize];
        cellsType = new int[boardSize * boardSize];
        for (int i = 0; i < 64; ++i) cellsType[i] = FigureType.EMPTY_TYPE;
        try {
            fill(fillingType);
        } catch (final ChessException e) {
            logger.error("Ошибка при заполнении доски");
        }
        cellsTypeHash = GameMath.hashCode64(cellsType);
    }

    public Board(final BoardFilling fillingType) {
        this(STD_BOARD_SIZE, fillingType);
    }

    /** @param fen строка в нотации Форсайта-Эдвардса */
    public Board(final String fen) throws ChessError {
        this(STD_BOARD_SIZE, BoardFilling.EMPTY);
        try {
            if (!NotationService.checkValidityPlacement(fen)) {
                logger.error(
                        "Ошибка при парсинге строки для конструктора доски (строка не валидна)");
                throw new ChessError(INCORRECT_STRING_FOR_FILLING_BOARD);
            }
            int y = 0;
            int x = 0;
            for (final Character currentSymbol : fen.toCharArray()) {
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
        } catch (final ChessException e) {
            logger.error("Ошибка при установке фигуры на доску в конструкторе доски по строке");
            throw new ChessError(INCORRECT_COORDINATES);
        }
    }

    /** Создает копию доски, включая копии фигур на ней */
    public Board(final Board board) {
        boardSize = board.boardSize;
        cells = new Figure[boardSize][boardSize];
        cellsType = board.cellsType.clone();
        cellsTypeHash = board.cellsTypeHash;

        for (int y = 0; y < boardSize; ++y)
            for (int x = 0; x < boardSize; ++x) {
                if (board.cells[y][x] != null) {
                    cells[y][x] =
                            Figure.build(
                                    board.cells[y][x].figureType,
                                    board.cells[y][x].getColor(),
                                    new Cell(x, y));

                    cells[y][x].wasMoved = board.cells[y][x].wasMoved;

                    if (cells[y][x].figureType == FigureType.KING) {
                        if (cells[y][x].getColor() == Color.WHITE)
                            whiteKing = cells[y][x].getCurrentPosition();
                        else blackKing = cells[y][x].getCurrentPosition();
                    }
                }
            }
    }

    /** @return true, если клетка cell атакуется цветом color */
    public static boolean isAttackedCell(final Board board, final Cell cell, final Color color) {
        for (final Figure f : board.getFigures(color))
            if (f.isAttackedCell(board, cell)) return true;
        return false;
    }

    /** @return символ фигуры figure цвета color */
    private static char figureToIcon(final Color color, final FigureType figure) {
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
     * @param gs нужен для получения ходов пешек и проверки на шах после хода
     * @return список ходов для цвета color, включая превращения пешек в ферзя, слона, ладью и коня
     *     (создает 4 отдельных хода). Все ходы гарантированно корректные и проверены на шах
     */
    public List<Move> getAllPreparedMoves(final GameSettings gs, final Color color)
            throws ChessError {
        final List<Move> allMoves = new LinkedList<>();
        for (int i = 0; i < 8; ++i) {
            if (i == 1 || i == 6) { // на линиях 2 и 7 - кандидаты (пешки) на превращение
                for (final Figure figure : cells[i])
                    if (figure != null && figure.getColor() == color)
                        if (figure.figureType == FigureType.PAWN) { // у пешки смотрим превращения
                            for (final Move move : figure.getAllMoves(gs)) {
                                switch (move.getMoveType()) {
                                    case TURN_INTO, TURN_INTO_ATTACK:
                                        move.turnInto = FigureType.QUEEN; // 1 тип превращения
                                        // проверка на шах превращения (проверка для других типов
                                        // превращения эквивалентна):
                                        if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) {
                                            allMoves.add(move);
                                            // 2, 3, 4 типы превращения:
                                            allMoves.add(new Move(move, FigureType.KNIGHT));
                                            allMoves.add(new Move(move, FigureType.ROOK));
                                            allMoves.add(new Move(move, FigureType.BISHOP));
                                        }
                                        break;
                                    default: // проверка на шах другого типа хода пешки:
                                        if (gs.moveSystem.isCorrectVirtualMoveSilence(move))
                                            allMoves.add(move);
                                        break;
                                }
                            }
                        } else // обычное заполнение
                        for (final Move move : figure.getAllMoves(gs)) // проверка на шах:
                            if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) allMoves.add(move);
            } else // остальные линии
            for (final Figure figure : cells[i])
                    if (figure != null && figure.getColor() == color) // обычное заполнение
                    for (final Move move : figure.getAllMoves(gs)) // проверка на шах:
                        if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) allMoves.add(move);
        }
        return allMoves;
    }

    /**
     * @param gs нужен для получения ходов пешек и проверки на шах после хода
     * @return true, если у игрока цвета color нет корректных ходов (поставлен пат)
     */
    public boolean isHasAnyCorrectMove(final GameSettings gs, final Color color) throws ChessError {
        for (int i = 0; i < 8; ++i) {
            if (i == 1 || i == 6) { // на линиях 2 и 7 - кандидаты (пешки) на превращение
                for (final Figure figure : cells[i])
                    if (figure != null && figure.getColor() == color)
                        if (figure.figureType == FigureType.PAWN) { // у пешки смотрим превращения
                            for (final Move move : figure.getAllMoves(gs)) {
                                switch (move.getMoveType()) {
                                    case TURN_INTO, TURN_INTO_ATTACK:
                                        // только для проверки виртуального хода:
                                        move.turnInto = FigureType.QUEEN;
                                        // убирать фигуру не нужно, т.к. это копия хода
                                        break;
                                    default:
                                        break;
                                }
                                // проверка на шах хода пешки:
                                if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) return true;
                            }
                        } else // любая другая фигура
                        for (final Move move : figure.getAllMoves(gs)) // проверка на шах:
                            if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) return true;
            } else // остальные линии
            for (final Figure figure : cells[i])
                    if (figure != null && figure.getColor() == color)
                        for (final Move move : figure.getAllMoves(gs)) // проверка на шах:
                        if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) return true;
        }
        return false;
    }

    /** Заполняет доску расстановкой fillingType */
    private void fill(final BoardFilling fillingType) throws ChessException {
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

    /**
     * @return true, если клетка принадлежит доске
     * @deprecated Оставлено для совместимости и тестирования
     */
    @Deprecated
    public boolean isCorrectCell(final int column, final int row) {
        return column >= 0 && row >= 0 && column < boardSize && row < boardSize;
    }

    /**
     * Быстрое заполнение доски по первой линии фигур. 1 и 8 линии задаются orderFirstLine, на 2 и 7
     * будут стоять пешки, остальные линии останутся пустыми
     */
    private void fillBoardForFirstLine(final FigureType[] orderFirstLine) throws ChessException {
        final Cell startBlack = new Cell(0, 0);
        final Cell startWhite = new Cell(0, boardSize - 1);
        final Cell shift = new Cell(1, 0);

        for (final FigureType figureType : orderFirstLine) {
            setFigure(Figure.build(figureType, Color.BLACK, new Cell(startBlack)));
            setFigure(new Pawn(Color.BLACK, startBlack.createAdd(new Cell(0, 1))));
            setFigure(Figure.build(figureType, Color.WHITE, new Cell(startWhite)));
            setFigure(new Pawn(Color.WHITE, startWhite.createAdd(new Cell(0, -1))));
            startBlack.shift(shift);
            startWhite.shift(shift);
        }
    }

    /**
     * Устанавливает фигуру на доску
     *
     * @deprecated Оставлено для совместимости
     */
    @Deprecated
    public void setFigure(final Figure figure) throws ChessException {
        final Cell position = figure.getCurrentPosition();
        if (!isCorrectCell(position.column, position.row)) {
            logger.warn("Ошибка установки фигуры {} на доску", figure);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        cells[position.row][position.column] = figure;

        final int i = position.row * STD_BOARD_SIZE + position.column;
        final int newValue = figure.getValue();
        cellsTypeHash += GameMath.hash64Coeff[i] * (newValue - cellsType[i]);
        cellsType[i] = newValue;

        if (figure.figureType == FigureType.KING) {
            if (figure.getColor() == Color.WHITE) whiteKing = figure.getCurrentPosition();
            else blackKing = figure.getCurrentPosition();
        }
        logger.trace("Фигура {} установлена на доску", figure);
    }

    /** Устанавливает фигуру на доску БЕЗ ПРОВЕРОК */
    public void setFigureUgly(final Figure figure) throws ArrayIndexOutOfBoundsException {
        final Cell position = figure.getCurrentPosition();
        cells[position.row][position.column] = figure;

        final int i = position.row * 8 + position.column;
        final int newValue = figure.getValue();
        cellsTypeHash += GameMath.hash64Coeff[i] * (newValue - cellsType[i]);
        cellsType[i] = newValue;
    }

    /** Устанавливает фигуру на доску БЕЗ ПРОВЕРОК и пересчитывания хеша доски */
    public void setFigureUglyWithoutRecalcHash(final Figure figure)
            throws ArrayIndexOutOfBoundsException {
        final Cell position = figure.getCurrentPosition();
        cells[position.row][position.column] = figure;
    }

    /**
     * @param color цвет игрока
     * @return фигуры определенного цвета
     */
    public List<Figure> getFigures(final Color color) {
        final List<Figure> list = new ArrayList<>(16);
        for (final Figure[] figures : cells)
            for (final Figure figure : figures)
                if (figure != null && figure.getColor() == color) list.add(figure);
        return list;
    }

    /** @return все фигуры на доске */
    public List<Figure> getAllFigures() {
        final List<Figure> list = new ArrayList<>(32);
        for (final Figure[] figures : cells)
            for (final Figure figure : figures) if (figure != null) list.add(figure);
        return list;
    }

    /** @return количество фигур на доске цвета color */
    public int getFigureCount(final Color color) {
        int count = 0;
        for (int yl = 0, yr = boardSize - 1; yl < yr; ++yl, --yr) {
            for (int xl = 0, xr = boardSize - 1; xl < xr; ++xl, --xr) {
                if (cells[yl][xl] != null && cells[yl][xl].getColor() == color) ++count;
                if (cells[yl][xr] != null && cells[yl][xr].getColor() == color) ++count;
                if (cells[yr][xl] != null && cells[yr][xl].getColor() == color) ++count;
                if (cells[yr][xr] != null && cells[yr][xr].getColor() == color) ++count;
            }
        }
        return count;
    }

    /** @return фигура короля цвета color или null, если король не найден */
    public Figure findKing(final Color color) {
        return color == Color.WHITE
                ? cells[whiteKing.row][whiteKing.column]
                : cells[blackKing.row][blackKing.column];
    }

    /** @return позиция короля цвета color или null, если король не найден */
    public Cell findKingCell(final Color color) {
        return color == Color.WHITE ? whiteKing : blackKing;
    }

    /**
     * 0 - нет возможности рокироваться<br>
     * 1 - левая рокировка возможна<br>
     * 2 - правая<br>
     * 3 - обе
     */
    public int isCastlingPossible(final Color color) throws ChessError {
        final Figure king = findKing(color);
        if (king == null) throw new ChessError(KING_NOT_FOUND);
        if (king.wasMoved) return 0;
        return (isNotLeftRookStandardMoved(color) ? 1 : 0)
                + (isNotRightRookStandardMoved(color) ? 2 : 0);
    }

    /**
     * @param color цвет ладьи
     * @return левая ладья в углу для длинной рокировки цвета color или null, если не найдена
     */
    public boolean isNotLeftRookStandardMoved(final Color color) {
        final Figure rook = cells[(color == Color.BLACK ? 0 : boardSize - 1)][0];
        return rook != null
                && rook.figureType == FigureType.ROOK
                && rook.getColor() == color
                && !rook.wasMoved;
    }

    /**
     * @param color цвет ладьи
     * @return правая ладья в углу для короткой рокировки цвета color или null, если не найдена
     */
    public boolean isNotRightRookStandardMoved(final Color color) {
        final Figure rook = cells[(color == Color.BLACK ? 0 : boardSize - 1)][boardSize - 1];
        return rook != null
                && rook.figureType == FigureType.ROOK
                && rook.getColor() == color
                && !rook.wasMoved;
    }

    /**
     * Перемещает фигуру с заменой старой, даже если ход некорректный. При срублении фигуры,
     * возвращается эта фигура без изменения собственных координат.
     *
     * @return предыдущая фигура на месте перемещения или null, если клетка была пуста
     * @throws ChessException если ход выходит за пределы доски
     * @deprecated Оставлено для совместимости
     */
    @Deprecated
    public Figure moveFigure(final Move move) throws ChessException {
        logger.trace("Начато перемещение фигуры: {}", move);
        final Figure figureFrom = getFigure(move.getFrom());
        final Figure figureTo = getFigure(move.getTo());
        figureFrom.setCurrentPosition(move.getTo());
        figureFrom.wasMoved = true;
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
    public Figure moveFigureUgly(final Move move) throws ArrayIndexOutOfBoundsException {
        final Figure figureFrom = getFigureUgly(move.getFrom());
        final Figure figureTo = getFigureUgly(move.getTo());
        figureFrom.setCurrentPosition(move.getTo());
        setFigureUgly(figureFrom);
        removeFigureUgly(move.getFrom());
        return figureTo;
    }

    /**
     * Перемещает фигуру без проверок и установки флагов перемещения, и пересчитывания хеша доски
     *
     * @return предыдущая фигура на месте перемещения или null, если клетка была пуста
     */
    public Figure moveFigureUglyWithoutRecalcHash(final Move move)
            throws ArrayIndexOutOfBoundsException {
        final Figure figureFrom = getFigureUgly(move.getFrom());
        final Figure figureTo = getFigureUgly(move.getTo());
        figureFrom.setCurrentPosition(move.getTo());
        setFigureUglyWithoutRecalcHash(figureFrom);
        removeFigureUglyWithoutRecalcHash(move.getFrom());
        return figureTo;
    }

    /**
     * @return фигура или null, если клетка пуста
     * @throws ChessException если клетка не лежит в пределах доски
     * @deprecated Оставлено для совместимости
     */
    @Deprecated
    public Figure getFigure(final Cell cell) throws ChessException {
        final int x = cell.column;
        final int y = cell.row;
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
    public Figure getFigureUgly(final Cell cell) throws ArrayIndexOutOfBoundsException {
        return cells[cell.row][cell.column];
    }

    /**
     * Опасно! Проверки не выполняются.
     *
     * @return фигура или null, если клетка пуста.
     */
    public Figure getFigureUgly(final int row, final int column)
            throws ArrayIndexOutOfBoundsException {
        return cells[row][column];
    }

    /**
     * Убирает фигуру с доски
     *
     * @return удаленную фигуру или null, если клетка была пуста
     * @deprecated Оставлено для совместимости
     */
    @Deprecated
    public Figure removeFigure(final Cell cell) throws ChessException {
        if (!isCorrectCell(cell.column, cell.row)) {
            logger.warn("Фигура не была удалена с клетки: {}", cell);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        final Figure old = cells[cell.row][cell.column];
        cells[cell.row][cell.column] = null;

        final int i = cell.row * 8 + cell.column;
        final int newValue = FigureType.EMPTY_TYPE;
        cellsTypeHash += GameMath.hash64Coeff[i] * (newValue - cellsType[i]);
        cellsType[i] = newValue;

        return old;
    }

    /**
     * Убирает фигуру с доски БЕЗ ПРОВЕРОК
     *
     * @return удаленную фигуру или null, если клетка была пуста
     */
    public Figure removeFigureUgly(final Cell cell) throws ArrayIndexOutOfBoundsException {
        final Figure old = cells[cell.row][cell.column];
        cells[cell.row][cell.column] = null;

        final int i = cell.row * 8 + cell.column;
        final int newValue = FigureType.EMPTY_TYPE;
        cellsTypeHash += GameMath.hash64Coeff[i] * (newValue - cellsType[i]);
        cellsType[i] = newValue;

        return old;
    }

    /**
     * Убирает фигуру с доски БЕЗ ПРОВЕРОК и пересчитывания хеша доски
     *
     * @return удаленную фигуру или null, если клетка была пуста
     */
    public Figure removeFigureUglyWithoutRecalcHash(final Cell cell) {
        final Figure old = cells[cell.row][cell.column];
        cells[cell.row][cell.column] = null;
        return old;
    }

    /** @return true, если клетка лежит на доске и она пустая, иначе false */
    public boolean isEmptyCell(final Cell cell) {
        try {
            return cells[cell.row][cell.column] == null;
        } catch (final ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * @param color цвет своей фигуры
     * @return true, если клетка лежит на доске и на этой клетке есть фражеская фигура, иначе false
     */
    public boolean isEnemyFigureOn(final Color color, final Cell cell) {
        try {
            return cells[cell.row][cell.column].getColor() != color;
        } catch (final ArrayIndexOutOfBoundsException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (final Figure[] line : cells) {
            sb.append('|');
            for (final Figure figure : line) {
                if (figure == null) sb.append("_");
                else sb.append(Board.figureToIcon(figure.getColor(), figure.figureType));
                sb.append('|');
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Board board = (Board) o;
        return cellsTypeHash == board.cellsTypeHash && Arrays.equals(cellsType, board.cellsType);
    }

    @Override
    public int hashCode() {
        return cellsTypeHash;
    }

    /** @return копия состояния типов фигур на доске */
    public int[] fastSnapshot() {
        return cellsType.clone();
    }

    /** @return ссылка на состояния типов фигур на доске (совместимо с PeSTO) */
    public int[] fastSnapshotReference() {
        return cellsType;
    }

    public enum BoardFilling {
        EMPTY,
        STANDARD,
        CHESS960
    }
}
