package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_NOT_FOUND;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class History implements Iterable<String> {
    private static final Logger logger = LoggerFactory.getLogger(History.class);
    private static final int AVERAGE_MAXIMUM_MOVES = 300;
    private final Map<FigureType, Character> notation = new EnumMap<>(FigureType.class);
    private final Map<String, Integer> repetitionsMap = new HashMap<>(AVERAGE_MAXIMUM_MOVES);
    private final List<String> recordsList = new ArrayList<>(AVERAGE_MAXIMUM_MOVES);
    private final Deque<Move> moves = new ArrayDeque<>(AVERAGE_MAXIMUM_MOVES);
    private final GameSettings gameSettings;
    private boolean whiteStep = true;
    private Move lastMove;
    private int peaceMoveCount = 0;

    public History(GameSettings gameSettings) {
        this.gameSettings = gameSettings;

        notation.put(FigureType.KING, 'K');
        notation.put(FigureType.QUEEN, 'Q');
        notation.put(FigureType.ROOK, 'R');
        notation.put(FigureType.BISHOP, 'B');
        notation.put(FigureType.KNIGHT, 'N');
        notation.put(FigureType.PAWN, 'P');

        logger.debug("История инициализирована");
    }

    /**
     * Добавляет в список записей запись текущего состояния доски и все нужные дополнительные
     * приписки
     *
     * @param lastMove последний ход или null, если его не было
     * @return Строка - только что добавленная запись
     */
    public String addRecord(Move lastMove) throws ChessException, ChessError {
        this.lastMove = lastMove;
        if(lastMove!=null)
            moves.add(lastMove);

        String rec = convertBoardToStringForsythEdwards();
        recordsList.add(rec);

        repetitionsMap.put(rec, repetitionsMap.getOrDefault(rec, 0) + 1);
        logger.debug("Запись <{}> добавлена в историю", rec);

        whiteStep = !whiteStep;
        return rec;
    }

    public void checkAndAddPeaceMoveCount(Move move) throws ChessException {
        if (move.getMoveType() == MoveType.ATTACK
                || move.getMoveType() == MoveType.EN_PASSANT
                || gameSettings.board.getFigure(move.getTo()).getType() == FigureType.PAWN)
            peaceMoveCount = 0;
        else ++peaceMoveCount;
    }

    /** @return Строка - запись в виде нотации Форсайта-Эдвардса */
    private String convertBoardToStringForsythEdwards() throws ChessException, ChessError {
        StringBuilder rec = new StringBuilder(70);

        rec.append(getConvertingFigurePosition());

        rec.append(' ').append(whiteStep ? 'w' : 'b');

        String castlingPossibility = getCastlingPossibility();
        if (!"".equals(castlingPossibility)) rec.append(' ').append(castlingPossibility);

        rec.append(getPawnEnPassantPossibility());

        return rec.toString();
    }

    /** @return Строка - часть записи отвечающая за позиционирование фигур на доске */
    private String getConvertingFigurePosition() throws ChessException {
        StringBuilder result = new StringBuilder();
        Figure currentFigure;

        for (int y = 0; y < gameSettings.board.boardSize; y++) {
            int emptySlots = 0;
            for (int x = 0; x < gameSettings.board.boardSize; x++) {
                currentFigure = gameSettings.board.getFigure(new Cell(x, y));

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

    /** @return Строка - часть записи отвечающая, можно ли использовать рокировки */
    private String getCastlingPossibility() throws ChessError {
        return getCastlingPossibility(Color.WHITE) + getCastlingPossibility(Color.BLACK);
    }

    private String getCastlingPossibility(Color color) throws ChessError {
        String res = "";
        Figure king = gameSettings.board.findKing(color);
        if (king == null) throw new ChessError(KING_NOT_FOUND);
        Figure leftRook =
                gameSettings.board.findRook(king.getCurrentPosition(), color, new Cell(-1, 0));
        Figure rightRook =
                gameSettings.board.findRook(king.getCurrentPosition(), color, new Cell(1, 0));
        if (rightRook != null && !rightRook.wasMoved()) res = "k";
        if (leftRook != null && !leftRook.wasMoved()) res = res + "q";
        return color == Color.WHITE ? res.toUpperCase() : res;
    }

    /**
     * @return Строка - часть записи (c пробелом вначале) отвечающая за то, доступно ли взятие на
     *     проходе следующим ходом
     */
    private String getPawnEnPassantPossibility() throws ChessException {
        StringBuilder result = new StringBuilder();
        if (lastMove != null && lastMove.getMoveType() == MoveType.LONG_MOVE) {
            result.append(' ').append(lastMove.getTo().toString().charAt(0));
            result.append(
                    gameSettings.board.getFigure(lastMove.getTo()).getColor() == Color.WHITE
                            ? '3'
                            : '6');
        }
        return result.toString();
    }

    public int getPeaceMoveCount() {
        return peaceMoveCount;
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

    public Move undo(){
        lastMove = moves.pollLast();
        return lastMove;
    }

    @Override
    public Iterator<String> iterator() {
        return recordsList.iterator();
    }
}
