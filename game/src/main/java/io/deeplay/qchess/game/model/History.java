package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_NOT_FOUND;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class History {
    private static final Logger logger = LoggerFactory.getLogger(History.class);
    private static final int AVERAGE_MAXIMUM_MOVES = 300;
    private static final Map<FigureType, Character> NOTATION = new EnumMap<>(FigureType.class);

    static {
        NOTATION.put(FigureType.KING, 'K');
        NOTATION.put(FigureType.QUEEN, 'Q');
        NOTATION.put(FigureType.ROOK, 'R');
        NOTATION.put(FigureType.BISHOP, 'B');
        NOTATION.put(FigureType.KNIGHT, 'N');
        NOTATION.put(FigureType.PAWN, 'P');
        if (NOTATION.size() != FigureType.values().length) {
            throw new ExceptionInInitializerError("NOTATION map не заполнен для всех случаев");
        }
    }

    private final GameSettings gameSettings;

    private final Map<BoardState, Integer> repetitionsMap = new HashMap<>(AVERAGE_MAXIMUM_MOVES);
    /** using like a stack */
    private final Deque<BoardState> recordsList = new ArrayDeque<>(AVERAGE_MAXIMUM_MOVES);

    private Move lastMove;
    private boolean hasMovedBeforeLastMove;
    /** Исключая пешку при взятии на проходе */
    private Figure removedFigure;

    private int peaceMoveCount = 0;

    private boolean isWhiteCastlingPossibility = true;
    private boolean isBlackCastlingPossibility = true;

    public History(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    /** Копирует всю историю */
    public History(History history, GameSettings gameSettings) {
        this(gameSettings);
        this.repetitionsMap.putAll(history.repetitionsMap);
        this.recordsList.addAll(history.recordsList);
        this.lastMove = history.lastMove;
        this.hasMovedBeforeLastMove = history.hasMovedBeforeLastMove;
        this.removedFigure =
                Figure.build(
                        history.removedFigure.getType(),
                        history.removedFigure.getColor(),
                        new Cell(
                                history.removedFigure.getCurrentPosition().getColumn(),
                                history.removedFigure.getCurrentPosition().getRow()));
        this.peaceMoveCount = history.peaceMoveCount;
        this.isWhiteCastlingPossibility = history.isWhiteCastlingPossibility;
        this.isBlackCastlingPossibility = history.isBlackCastlingPossibility;
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

        String rec = convertBoardToStringForsythEdwards();
        BoardState boardState =
                new BoardState(
                        rec, lastMove, peaceMoveCount, hasMovedBeforeLastMove, removedFigure);
        recordsList.push(boardState);

        repetitionsMap.put(boardState, repetitionsMap.getOrDefault(boardState, 0) + 1);
        logger.debug("Запись <{}> добавлена в историю", rec);

        return rec;
    }

    public boolean isHasMovedBeforeLastMove() {
        return hasMovedBeforeLastMove;
    }

    public void setHasMovedBeforeLastMove(boolean hasMoved) {
        hasMovedBeforeLastMove = hasMoved;
    }

    public Figure getRemovedFigure() {
        return removedFigure;
    }

    public void setRemovedFigure(Figure removedFigure) {
        this.removedFigure = removedFigure;
    }

    public void checkAndAddPeaceMoveCount(Move move) throws ChessException {
        if (move.getMoveType() == MoveType.ATTACK
                || move.getMoveType() == MoveType.EN_PASSANT
                || move.getMoveType() == MoveType.TURN_INTO_ATTACK
                || gameSettings.board.getFigure(move.getTo()).getType() == FigureType.PAWN)
            peaceMoveCount = 0;
        else ++peaceMoveCount;
    }

    /** @return Строка - запись в виде нотации Форсайта-Эдвардса */
    private String convertBoardToStringForsythEdwards() throws ChessException, ChessError {
        StringBuilder rec = new StringBuilder(70);

        rec.append(getConvertingFigurePosition());
        rec.append(' ').append(recordsList.size() % 2 == 0 ? 'w' : 'b');

        String castlingPossibility = getCastlingPossibility();
        if (!"".equals(castlingPossibility)) rec.append(' ').append(castlingPossibility);

        rec.append(getPawnEnPassantPossibility());
        return rec.toString();
    }

    /** @return Строка - часть записи отвечающая за позиционирование фигур на доске */
    private String getConvertingFigurePosition() throws ChessException {
        StringBuilder result = new StringBuilder();
        Figure currentFigure;

        for (int y = 0; y < gameSettings.board.boardSize; ++y) {
            int emptySlots = 0;

            for (int x = 0; x < gameSettings.board.boardSize; ++x) {
                currentFigure = gameSettings.board.getFigure(new Cell(x, y));

                if (currentFigure == null) ++emptySlots;
                else {
                    if (emptySlots != 0) result.append(emptySlots);
                    Character notationFigureChar = NOTATION.get(currentFigure.getType());
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
        if (color == Color.WHITE && !isWhiteCastlingPossibility) return res;
        if (color == Color.BLACK && !isBlackCastlingPossibility) return res;

        Figure king = gameSettings.board.findKing(color);
        if (king == null)
            return "";
            //throw new ChessError(KING_NOT_FOUND);
        if (king.wasMoved()) return res;
        Figure leftRook =
                gameSettings.board.findRook(king.getCurrentPosition(), color, new Cell(-1, 0));
        Figure rightRook =
                gameSettings.board.findRook(king.getCurrentPosition(), color, new Cell(1, 0));
        if (rightRook != null && !rightRook.wasMoved()) res += "k";
        if (leftRook != null && !leftRook.wasMoved()) res += "q";

        if (res.isEmpty()) {
            if (color == Color.WHITE) isWhiteCastlingPossibility = false;
            else isBlackCastlingPossibility = false;
        }
        return color == Color.WHITE ? res.toUpperCase() : res;
    }

    /**
     * @return Строка - часть записи (c пробелом вначале) отвечающая за то, доступно ли взятие на
     *     проходе следующим ходом
     */
    private String getPawnEnPassantPossibility() throws ChessException {
        StringBuilder result = new StringBuilder();
        if (lastMove != null && lastMove.getMoveType() == MoveType.LONG_MOVE) {
            result.append(' ').append((char) (lastMove.getTo().getColumn() + 'a'));
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

    /** @return Строка - последняя запись в списке или null, если записей нет */
    public String getLastRecord() {
        BoardState lastBoardState = recordsList.peek();
        if (lastBoardState == null) return null;
        return lastBoardState.forsythEdwards;
    }

    /** @return последнее состояние доски */
    public BoardState getLastBoardState() {
        return recordsList.peek();
    }

    /** @return true - если было минимум repetition-кратных повторений, false - если было меньше */
    public boolean checkRepetitions(int repetition) {
        for (Integer rep : repetitionsMap.values()) if (rep >= repetition) return true;
        return false;
    }

    public Move getLastMove() {
        return lastMove;
    }

    /**
     * Отменяет последний ход в истории
     *
     * @return отмененное состояние доски
     */
    public BoardState undo() {
        BoardState lastBoardState = recordsList.pop();
        BoardState prevLastBoardState = recordsList.peek();
        if (prevLastBoardState == null) return lastBoardState;
        hasMovedBeforeLastMove = prevLastBoardState.hasMovedBeforeLastMove;
        removedFigure = prevLastBoardState.removedFigure;
        lastMove = prevLastBoardState.lastMove;
        peaceMoveCount = prevLastBoardState.peaceMoveCount;
        repetitionsMap.put(lastBoardState, repetitionsMap.getOrDefault(lastBoardState, 1) - 1);
        return lastBoardState;
    }

    /**
     * Возвращает последний ход в историю БЕЗ ПРОВЕРОК !!!
     *
     * @param boardState ход, который станет последним
     */
    public void redo(BoardState boardState) {
        hasMovedBeforeLastMove = boardState.hasMovedBeforeLastMove;
        removedFigure = boardState.removedFigure;
        lastMove = boardState.lastMove;
        peaceMoveCount = boardState.peaceMoveCount;
        recordsList.push(boardState);
        repetitionsMap.put(boardState, repetitionsMap.getOrDefault(boardState, 0) + 1);
    }
}
