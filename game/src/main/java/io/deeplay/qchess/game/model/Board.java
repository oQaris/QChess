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
    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    /** @deprecated Плохо для гибкости */
    @Deprecated public static int STD_BOARD_SIZE = 8;

    public final int boardSize;
    private final Figure[][] cells;
    private final byte[] cellsType;
    public Cell blackKing;
    public Cell whiteKing;
    private int cellsTypeHash;

    public Board(int size, BoardFilling fillingType) {
        boardSize = size;
        cells = new Figure[boardSize][boardSize];
        cellsType = new byte[boardSize * boardSize];
        try {
            fill(fillingType);
        } catch (ChessException e) {
            logger.error("Ошибка при заполнении доски");
        }
        cellsTypeHash = GameMath.hashCode64(cellsType);
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
                    if (cells[y][x].figureType == FigureType.KING) {
                        if (cells[y][x].getColor() == Color.WHITE)
                            whiteKing = cells[y][x].getCurrentPosition();
                        else blackKing = cells[y][x].getCurrentPosition();
                    }
                }
            }
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

    /**
     * @param gs нужен для получения ходов пешек и проверки на шах после хода
     * @return список ходов для цвета color, включая превращения пешек в ферзя, слона, ладью и коня
     *     (создает 4 отдельных хода). Все ходы гарантированно корректные и проверены на шах
     */
    public List<Move> getAllPreparedMoves(GameSettings gs, Color color) throws ChessError {
        List<Move> allMoves = new LinkedList<>();
        for (int i = 0; i < 8; ++i) {
            if (i == 1 || i == 6) { // на диагоналях 2 и 7 - кандидаты (пешки) на превращение
                for (Figure figure : cells[i])
                    if (figure != null && figure.getColor() == color)
                        if (figure.figureType == FigureType.PAWN) { // у пешки смотрим превращения
                            for (Move move : figure.getAllMoves(gs)) {
                                if (move.getMoveType() == MoveType.TURN_INTO
                                        || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
                                    move.setTurnInto(FigureType.QUEEN); // 1 тип превращения
                                    // проверка на шах превращения (проверка для других типов
                                    // превращения эквивалентна):
                                    if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) {
                                        allMoves.add(move);
                                        // 2, 3, 4 типы превращения:
                                        allMoves.add(new Move(move, FigureType.KNIGHT));
                                        allMoves.add(new Move(move, FigureType.ROOK));
                                        allMoves.add(new Move(move, FigureType.BISHOP));
                                    }
                                }
                                // проверка на шах другого типа хода пешки:
                                else if (gs.moveSystem.isCorrectVirtualMoveSilence(move))
                                    allMoves.add(move);
                            }
                        } else // обычное заполнение
                        for (Move move : figure.getAllMoves(gs)) // проверка на шах:
                            if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) allMoves.add(move);
            } else // остальные диагонали
            for (Figure figure : cells[i])
                    if (figure != null && figure.getColor() == color) // обычное заполнение
                    for (Move move : figure.getAllMoves(gs)) // проверка на шах:
                        if (gs.moveSystem.isCorrectVirtualMoveSilence(move)) allMoves.add(move);
        }
        return allMoves;
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

        int i = position.row * 8 + position.column;
        cellsTypeHash += GameMath.hash64Coeff[i] * (figure.figureType.type - cellsType[i]);
        cellsType[i] = figure.figureType.type;

        if (figure.figureType == FigureType.KING) {
            if (figure.getColor() == Color.WHITE) whiteKing = figure.getCurrentPosition();
            else blackKing = figure.getCurrentPosition();
        }
        logger.trace("Фигура {} установлена на доску", figure);
    }

    /** Устанавливает фигуру на доску БЕЗ ПРОВЕРОК */
    public void setFigureUgly(Figure figure) throws ArrayIndexOutOfBoundsException {
        Cell position = figure.getCurrentPosition();
        cells[position.row][position.column] = figure;

        int i = position.row * 8 + position.column;
        cellsTypeHash += GameMath.hash64Coeff[i] * (figure.figureType.type - cellsType[i]);
        cellsType[i] = figure.figureType.type;
    }

    public void setFigureUglyWithoutRecalcHash(Figure figure)
            throws ArrayIndexOutOfBoundsException {
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

    /** @return все фигуры на доске */
    public List<Figure> getAllFigures() {
        List<Figure> list = new ArrayList<>(32);
        for (Figure[] figures : cells)
            for (Figure figure : figures) if (figure != null) list.add(figure);
        return list;
    }

    public int getFigureCount(Color color) {
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

    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    public int isCastlingPossible(Color color) throws ChessError {
        Figure king = findKing(color);
        if (king == null) throw new ChessError(KING_NOT_FOUND);
        if (king.wasMoved()) return 0;
        return (isNotLeftRookStandardMoved(color) ? 1 : 0)
                + (isNotRightRookStandardMoved(color) ? 2 : 0);
    }

    /**
     * @param color цвет ладьи
     * @return левая ладья в углу для длинной рокировки цвета color или null, если не найдена
     */
    public boolean isNotLeftRookStandardMoved(Color color) {
        Figure rook = cells[(color == Color.BLACK ? 0 : boardSize - 1)][0];
        return rook != null
                && rook.figureType == FigureType.ROOK
                && rook.getColor() == color
                && !rook.wasMoved();
    }

    /**
     * @param color цвет ладьи
     * @return правая ладья в углу для короткой рокировки цвета color или null, если не найдена
     */
    public boolean isNotRightRookStandardMoved(Color color) {
        Figure rook = cells[(color == Color.BLACK ? 0 : boardSize - 1)][boardSize - 1];
        return rook != null
                && rook.figureType == FigureType.ROOK
                && rook.getColor() == color
                && !rook.wasMoved();
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

    public Figure moveFigureUglyWithoutRecalcHash(Move move) throws ArrayIndexOutOfBoundsException {
        Figure figureFrom = getFigureUgly(move.getFrom());
        Figure figureTo = getFigureUgly(move.getTo());
        figureFrom.setCurrentPosition(move.getTo());
        setFigureUglyWithoutRecalcHash(figureFrom);
        removeFigureUglyWithoutRecalcHash(move.getFrom());
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

        int i = cell.row * 8 + cell.column;
        cellsTypeHash -= GameMath.hash64Coeff[i] * cellsType[i];
        cellsType[i] = 0;

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

        int i = cell.row * 8 + cell.column;
        cellsTypeHash -= GameMath.hash64Coeff[i] * cellsType[i];
        cellsType[i] = 0;

        return old;
    }

    public Figure removeFigureUglyWithoutRecalcHash(Cell cell) {
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
                else sb.append(Board.figureToIcon(figure.getColor(), figure.figureType));
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
        return cellsTypeHash == board.cellsTypeHash && Arrays.equals(cellsType, board.cellsType);
    }

    @Override
    public int hashCode() {
        return cellsTypeHash;
    }

    /** @return копия состояния типов фигур на доске */
    public byte[] fastSnapshot() {
        return cellsType.clone();
    }

    public enum BoardFilling {
        EMPTY,
        STANDARD,
        CHESS960
    }
}
