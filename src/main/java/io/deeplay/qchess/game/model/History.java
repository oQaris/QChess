package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.EXCEPTION_IN_HISTORY;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class History implements Iterable<String> {
    private static final Logger log = LoggerFactory.getLogger(History.class);
    private final Map<TypeFigure, Character> notation = new EnumMap<>(TypeFigure.class);
    private final Map<String, Integer> repetitionsMap;
    private final List<String> recordsList;
    private final Board board;
    private boolean whiteStep = true;
    private Move lastMove;
    private int pieceMoveCount = 0;

    public History(Board board) throws ChessError {
        this.board = board;
        recordsList = new ArrayList<>(500);
        repetitionsMap = new HashMap<>(500);
        History.log.debug("История инициализирована");

        notation.put(TypeFigure.KING, 'K');
        notation.put(TypeFigure.QUEEN, 'Q');
        notation.put(TypeFigure.ROOK, 'R');
        notation.put(TypeFigure.BISHOP, 'B');
        notation.put(TypeFigure.KNIGHT, 'N');
        notation.put(TypeFigure.PAWN, 'P');

        try {
            addRecord(null);
        } catch (ChessException e) {
            History.log.error("Возникло исключение в истории {}", e.getMessage());
            throw new ChessError(EXCEPTION_IN_HISTORY, e);
        }
    }

    /**
     * Добавляет в список записей запись текущего состояния доски и все нужные дополнительные
     * приписки
     *
     * @param lastMove последний ход или null, если его не было
     * @return Строка - только что добавленная запись
     */
    public String addRecord(Move lastMove) throws ChessException {
        this.lastMove = lastMove;

        String record = convertBoardToStringForsytheEdwards();
        recordsList.add(record);

        repetitionsMap.put(record, repetitionsMap.getOrDefault(record, 0) + 1);

        History.log.debug("Запись {} добавлена в историю", record);
        whiteStep = !whiteStep;
        return record;
    }

    public void checkAndAddPieceMoveCount(Move move) throws ChessException {
        if (move.getMoveType() == MoveType.ATTACK
                || move.getMoveType() == MoveType.EN_PASSANT
                || board.getFigure(move.getTo()).getType() == TypeFigure.PAWN) pieceMoveCount = 0;
        else ++pieceMoveCount;
    }

    /** @return Строка - запись в виде нотации Форсайта-Эдвардса */
    private String convertBoardToStringForsytheEdwards() throws ChessException {
        StringBuilder record = new StringBuilder(70);

        record.append(getConvertingFigurePosition());

        record.append(' ').append(whiteStep ? 'w' : 'b');

        String castlingPossibility = getCastlingPossibility();
        if (!"".equals(castlingPossibility)) record.append(' ').append(castlingPossibility);

        record.append(getPawnEnPassantPossibility());

        return record.toString();
    }

    /** @return Строка - часть записи отвечающая за позиционирование фигур на доске */
    private String getConvertingFigurePosition() throws ChessException {
        StringBuilder result = new StringBuilder();
        Figure currentFigure = null;

        for (int y = 0; y < Board.BOARD_SIZE; y++) {
            int emptySlots = 0;
            for (int x = 0; x < Board.BOARD_SIZE; x++) {

                currentFigure = board.getFigure(new Cell(x, y));

                if (currentFigure == null) emptySlots++;
                else {
                    if (emptySlots != 0) result.append(emptySlots);
                    Character notationFigureChar = notation.get(currentFigure.getType());
                    result.append(
                            currentFigure.getColor() == Color.WHITE
                                    ? notationFigureChar
                                    : Character.toLowerCase(notationFigureChar));
                    emptySlots = 0;
                }
            }

            if (emptySlots != 0) result.append(emptySlots);
            result.append('/');
        }

        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

    /** @return Строка - часть записи отвечающая, то можно ли использовать рокировки */
    private String getCastlingPossibility() throws ChessException {
        StringBuilder result = new StringBuilder(4);
        Figure shortRook;
        Figure longRook;
        Figure whiteKing = board.getFigure(Cell.parse("e1"));
        if (whiteKing != null && !whiteKing.wasMoved()) {
            shortRook = board.getFigure(Cell.parse("h1"));
            longRook = board.getFigure(Cell.parse("a1"));
            if (shortRook != null && !shortRook.wasMoved()) result.append('K');
            if (longRook != null && !longRook.wasMoved()) result.append('Q');
        }

        Figure blackKing = board.getFigure(Cell.parse("e8"));
        if (blackKing != null && !blackKing.wasMoved()) {
            shortRook = board.getFigure(Cell.parse("h8"));
            longRook = board.getFigure(Cell.parse("a8"));
            if (shortRook != null && !shortRook.wasMoved()) result.append('k');
            if (longRook != null && !longRook.wasMoved()) result.append('q');
        }
        return result.toString();
    }

    /**
     * @return Строка - часть записи (c пробелом вначале) отвечающая за то, доступно ли взятие на
     *     проходе следующим ходом
     */
    private String getPawnEnPassantPossibility() throws ChessException {
        StringBuilder result = new StringBuilder();
        if (lastMove != null && lastMove.getMoveType() == MoveType.LONG_MOVE) {
            result.append(' ').append(lastMove.getTo().toString().charAt(0));
            result.append(board.getFigure(lastMove.getTo()).getColor() == Color.WHITE ? '3' : '6');
        }
        return result.toString();
    }

    public int getPieceMoveCount() {
        return pieceMoveCount;
    }

    /** @return Строка - последняя запись в списке */
    public String getLastRecord() {
        return recordsList.get(recordsList.size() - 1);
    }

    /** @return true - если было минимум repetition-кратных повторений, false - если было меньше */
    public boolean checkRepetitions(int repetition) {
        for (Integer rep : repetitionsMap.values()) if (rep >= repetition) return true;
        return false;
    }

    public Move getLastMove() {
        return lastMove;
    }

    @Override
    public Iterator<String> iterator() {
        return recordsList.iterator();
    }
}
