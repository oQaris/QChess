package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class History implements Iterable<String> {
    private static final Logger log = LoggerFactory.getLogger(History.class);

    private final Map<Character, Character> notation = new HashMap<>();
    private final List<String> recordsList;
    private final Board board;
    private boolean whiteStep = true;
    private Move prevMove;

    public History(Board board) throws ChessException {
        this.board = board;
        recordsList = new ArrayList<>(200);
        log.debug("История инициализирована");

        notation.put('♔', 'K');
        notation.put('♕', 'Q');
        notation.put('♖', 'R');
        notation.put('♗', 'B');
        notation.put('♘', 'N');
        notation.put('♙', 'P');
        notation.put('♚', 'k');
        notation.put('♛', 'q');
        notation.put('♜', 'r');
        notation.put('♝', 'b');
        notation.put('♞', 'n');
        notation.put('♟', 'p');

        addRecord();
    }

    /**
     * Добавляет в список записей запись текущего состояния доски и все нужные дополнительные приписки
     *
     * @return Строка - только что добавленная запись
     * @throws ChessException если convertBoardToStringForsytheEdwards() выбросит это исключение
     */
    public String addRecord() throws ChessException {
        String record = convertBoardToStringForsytheEdwards();
        recordsList.add(record);
        log.debug("Запись {} добавлена в историю", record);
        whiteStep = !whiteStep;
        return record;
    }

    /**
     * @return Строка - последняя запись в списке
     */
    public String getLastRecord() {
        return recordsList.get(recordsList.size() - 1);
    }

    /**
     * @return Строка - запись в виде нотации Форсайта-Эдвардса
     * @throws ChessException если getConvertingFigurePosition(), getCastlingPossibility() или
     * getPawnEnPassantPossibility() выбросит это исключение
     */
    private String convertBoardToStringForsytheEdwards() throws ChessException {
        StringBuilder record = new StringBuilder(70);

        record.append(getConvertingFigurePosition());

        record.append(' ').append(whiteStep ? 'w' : 'b');

        String castlingPossibility = getCastlingPossibility();
        if(!"".equals(castlingPossibility)) {
            record.append(' ').append(castlingPossibility);
        }

        record.append(getPawnEnPassantPossibility());

        return record.toString();
    }

    /**
     * @return Строка - часть записи отвечающая за позиционирование фигур на доске
     * @throws ChessException если getFigure() выбросит это исключение
     */
    private String getConvertingFigurePosition() throws ChessException {
        StringBuilder result = new StringBuilder();
        Figure currentFigure = null;

        for(int y = 0; y < Board.BOARD_SIZE; y++) {
            int emptySlots = 0;
            for(int x = 0; x < Board.BOARD_SIZE; x++) {

                currentFigure = board.getFigure(y, x);

                if(currentFigure == null) {
                    emptySlots++;
                } else {
                    if(emptySlots != 0) {
                        result.append(emptySlots);
                    }
                    result.append(notation.get(currentFigure.getCharIcon()));
                    emptySlots = 0;
                }
            }

            if(emptySlots != 0) {
                result.append(emptySlots);
            }
            result.append('/');
        }

        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

    /**
     * @return Строка - часть записи отвечающая, то можно ли использовать рокировки
     * @throws ChessException если getFigure() выбросит это исключение
     */
    private String getCastlingPossibility() throws ChessException {
        StringBuilder result = new StringBuilder(4);
        Figure shortRook = null;
        Figure longRook = null;
        Figure whiteKing = board.getFigure(Cell.parse("e1"));
        if(whiteKing != null && !whiteKing.wasMoved()) {
            shortRook = board.getFigure(Cell.parse("h1"));
            longRook = board.getFigure(Cell.parse("a1"));
            if(shortRook != null && !shortRook.wasMoved()) {
                result.append('K');
            }
            if(longRook != null && !longRook.wasMoved()) {
                result.append('Q');
            }
        }

        Figure blackKing = board.getFigure(Cell.parse("e8"));
        if(blackKing != null && !blackKing.wasMoved()) {
            shortRook = board.getFigure(Cell.parse("h8"));
            longRook = board.getFigure(Cell.parse("a8"));
            if(shortRook != null && !shortRook.wasMoved()) {
                result.append('k');
            }
            if(longRook != null && !longRook.wasMoved()) {
                result.append('q');
            }
        }
        return result.toString();
    }

    /**
     * @return Строка - часть записи (c пробелом вначале) отвечающая за то, доступно ли взятие на проходе следующим ходом
     * @throws ChessException если getFigure() выбросит это исключение
     */
    private String getPawnEnPassantPossibility() throws ChessException {
        StringBuilder result = new StringBuilder();
        if(prevMove != null && prevMove.getMoveType() == MoveType.LONG_MOVE) {
            result.append(' ').append(prevMove.getTo().toString().charAt(0));
            result.append(board.getFigure(prevMove.getTo()).isWhite()? '3' : '6');
        }
        return result.toString();
    }

    /**
     * @return true - если было repetition-кратное повторение, false - если не было
     */
    public boolean checkThreefoldRepetition(int repetition) {
        Set<String> set = new HashSet<>(recordsList);
        return recordsList.size() - set.size() >= repetition - 1;
    }

    public Move getPrevMove() {
        return prevMove;
    }

    public void setPrevMove(Move prevMove) {
        this.prevMove = prevMove;
    }

    @Override
    public Iterator<String> iterator() {
        return recordsList.iterator();
    }
}
