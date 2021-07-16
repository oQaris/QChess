package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_COORDINATES;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_STRING_FOR_FILLING_BOARD;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_NOT_FOUND;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.service.NotationService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Board {
    private static final Logger logger = LoggerFactory.getLogger(Board.class);
    public final int boardSize;
    private final Figure[][] cells;

    public Board(int size, BoardFilling fillingType) {
        boardSize = size;
        cells = new Figure[boardSize][boardSize];
        try{
            fill(fillingType);
        }
        catch (ChessException e){
            logger.error("Ошибка при заполнении доски");
        }
    }

    public Board(BoardFilling fillingType) {
        this(8, fillingType);
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
                //todo Добавить рандома
                new FigureType[] {
                    FigureType.KNIGHT, FigureType.QUEEN, FigureType.ROOK,
                    FigureType.KING, FigureType.BISHOP, FigureType.ROOK,
                    FigureType.KNIGHT, FigureType.BISHOP
                });
        }
        logger.debug("Доска {} инициализирована", fillingType);
    }

    public Board(String placement) throws ChessError {
        this(8, BoardFilling.EMPTY);
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
                    y++;
                    x = 0;
                } else if (Character.isDigit(currentSymbol)) {
                    x += Integer.parseInt(String.valueOf(currentSymbol));
                } else {
                    setFigure(NotationService.getFigureByChar(currentSymbol, x, y));
                    x++;
                }
            }
        } catch (ChessException e) {
            logger.error("Ошибка при установке фигуры на доску в конструкторе доски по строке");
            throw new ChessError(INCORRECT_COORDINATES);
        }
    }

    /** @return true, если клетка принадлежит доске */
    public boolean isCorrectCell(int column, int row) {
        return column >= 0 && row >= 0 && column < boardSize && row < boardSize;
    }

    /** @return true, если клетка cell атакуется цветом color */
    public static boolean isAttackedCell(GameSettings settings, Cell cell, Color color) {
        for (Figure f : settings.board.getFigures(color))
            for (Move m :
                    f.getType() == FigureType.KING
                            ? ((King) f).getAttackedMoves(settings.board)
                            : f.getAllMoves(settings)) if (m.getTo().equals(cell)) return true;
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

    private void fillBoardForFirstLine(FigureType[] orderFirstLine) throws ChessException {
        Cell startBlack = new Cell(0, 0);
        Cell startWhite = new Cell(0, boardSize- 1);
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
    public void setFigure(Figure figure) throws ChessException {
        int x = figure.getCurrentPosition().getColumn();
        int y = figure.getCurrentPosition().getRow();
        if (!isCorrectCell(x, y)) {
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

    /**
     * @param color цвет игрока
     * @return позиция короля определенного цвета
     * @throws ChessError если король не был найден
     */
    public Figure findKing(Color color) throws ChessError {
        for (Figure[] figures : cells)
            for (Figure f : figures)
                if (f != null && f.getColor() == color && f.getType() == FigureType.KING) return f;
        logger.error("Король {} не был найден", color);
        throw new ChessError(KING_NOT_FOUND);
    }

    /**
     * @param cell стартовая клетка
     * @param color цвет ладьи
     * @param shift вектор смещения (поиск в эту сторону)
     * @return ладья относительно стартовой клетки цвета color или null, если не найдена
     */
    public Figure findRook(Cell cell, Color color, Cell shift) {
        while (isCorrectCell(cell.getColumn(), cell.getRow())) {
            Figure figure = cells[cell.getRow()][cell.getColumn()];
            if (figure != null && figure.getColor() == color && figure.getType() == FigureType.ROOK)
                return figure;
            cell = cell.createAdd(shift);
        }
        return null;
    }

    /**
     * Перемещает фигуру с заменой старой, даже если ход некорректный. При срублении фигуры,
     * возвращается эта фигура без изменения собственных координат.
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
        if (!isCorrectCell(x, y)) {
            logger.warn("Фигура не была установлена на клетку: {}", cell);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        return cells[y][x];
    }

    /** @return фигура или null, если клетка пуста. Опасно! Проверки не выполняются. */
    public Figure getFigureUgly(Cell cell) {
        int x = cell.getColumn();
        int y = cell.getRow();
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
            logger.warn("Фигура не была удалена с клетки: {}", cell);
            throw new ChessException(INCORRECT_COORDINATES);
        }
        Figure old = cells[y][x];
        cells[y][x] = null;
        return old;
    }

    /** @return true, если клетка лежит на доске и она пустая, иначе false */
    public boolean isEmptyCell(Cell cell) {
        int x = cell.getColumn();
        int y = cell.getRow();
        return isCorrectCell(x, y) && cells[y][x] == null;
    }

    /**
     * @param color цвет своей фигуры
     * @return true, если клетка лежит на доске и на этой клетке есть фражеская фигура, иначе false
     */
    public boolean isEnemyFigureOn(Color color, Cell cell) {
        int x = cell.getColumn();
        int y = cell.getRow();
        return isCorrectCell(x, y) && cells[y][x] != null && cells[y][x].getColor() != color;
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
        EMPTY,
        STANDARD,
        CHESS960
    }
}
