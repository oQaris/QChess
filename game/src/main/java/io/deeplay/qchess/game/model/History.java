package io.deeplay.qchess.game.model;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.KING_NOT_FOUND;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class History {
    private static final int AVERAGE_MAXIMUM_MOVES = 100;
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
    private boolean isWhiteMove = true;
    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    private int isWhiteCastlingPossibility = 3;
    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    private int isBlackCastlingPossibility = 3;

    /** Минимум состояний доски в истории ходов, которое необходимо сохранить после чистки */
    private int minBoardStateToSave;

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
        if (history.removedFigure != null) {
            Cell removedFigurePosition = history.removedFigure.getCurrentPosition();
            this.removedFigure =
                    Figure.build(
                            history.removedFigure.figureType,
                            history.removedFigure.getColor(),
                            new Cell(removedFigurePosition.column, removedFigurePosition.row));
        }
        this.peaceMoveCount = history.peaceMoveCount;
        this.isWhiteMove = history.isWhiteMove;
        this.isWhiteCastlingPossibility = history.isWhiteCastlingPossibility;
        this.isBlackCastlingPossibility = history.isBlackCastlingPossibility;
    }

    /**
     * Добавляет в список записей запись текущего состояния доски
     *
     * @param lastMove последний ход или null, если его не было
     */
    public void addRecord(Move lastMove) throws ChessError {
        this.lastMove = lastMove;
        isWhiteMove = !isWhiteMove;

        byte[] boardSnapshot = gameSettings.board.fastSnapshot();

        isWhiteCastlingPossibility = gameSettings.board.isCastlingPossible(Color.WHITE);
        isBlackCastlingPossibility = gameSettings.board.isCastlingPossible(Color.BLACK);

        BoardState boardState =
                new BoardState(
                        boardSnapshot,
                        gameSettings.board.hashCode(),
                        lastMove,
                        peaceMoveCount,
                        hasMovedBeforeLastMove,
                        removedFigure,
                        isWhiteMove,
                        isWhiteCastlingPossibility,
                        isBlackCastlingPossibility);

        recordsList.push(boardState);
        repetitionsMap.put(boardState, repetitionsMap.getOrDefault(boardState, 0) + 1);
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

    public void checkAndAddPeaceMoveCount(Move move) {
        if (move.getMoveType() == MoveType.ATTACK) peaceMoveCount = 0;
        else if (move.getMoveType() == MoveType.EN_PASSANT
                || move.getMoveType() == MoveType.TURN_INTO
                || move.getMoveType() == MoveType.TURN_INTO_ATTACK
                || gameSettings.board.getFigureUgly(move.getTo()).figureType == FigureType.PAWN) {
            // clearHistory(minBoardStateToSave);
            peaceMoveCount = 0;
        } else ++peaceMoveCount;
    }

    /**
     * Устанавливает минимум состояний доски в истории ходов, которое необходимо сохранить после
     * чистки
     */
    public void setMinBoardStateToSave(int minBoardStateToSave) {
        this.minBoardStateToSave = minBoardStateToSave;
    }

    /**
     * Чистить историю от ненужных состояний доски
     *
     * @param minBoardStateToSave минимум состояний доски в истории ходов, которое необходимо
     *     сохранить после чистки
     */
    public void clearHistory(int minBoardStateToSave) {
        int stateCountToClear = recordsList.size() - minBoardStateToSave;
        if (stateCountToClear <= 0) return;

        for (int i = 0; i < stateCountToClear; ++i) {
            BoardState boardState = recordsList.pollLast();
            int boardStateCount = repetitionsMap.remove(boardState);
            if (boardStateCount > 1) repetitionsMap.put(boardState, boardStateCount - 1);
        }
    }

    /**
     * @return текущая доска в записи FEN
     * @deprecated TODO: неправильно работает и не используется
     */
    @Deprecated
    public String getBoardToStringForsythEdwards() throws ChessError {
        StringBuilder rec = new StringBuilder(70);

        rec.append(getConvertingFigurePosition());
        rec.append(' ').append(isWhiteMove ? 'w' : 'b');

        String castlingPossibility = getCastlingPossibility();
        if (!"".equals(castlingPossibility)) rec.append(' ').append(castlingPossibility);

        rec.append(getPawnEnPassantPossibility());
        return rec.toString();
    }

    /** @return Строка - часть записи отвечающая за позиционирование фигур на доске */
    private String getConvertingFigurePosition() {
        StringBuilder result = new StringBuilder();
        Figure currentFigure;

        for (int y = 0; y < gameSettings.board.boardSize; ++y) {
            int emptySlots = 0;

            for (int x = 0; x < gameSettings.board.boardSize; ++x) {
                currentFigure = gameSettings.board.getFigureUgly(new Cell(x, y));

                if (currentFigure == null) ++emptySlots;
                else {
                    if (emptySlots != 0) result.append(emptySlots);
                    Character notationFigureChar = NOTATION.get(currentFigure.figureType);
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
        if (color == Color.WHITE && isWhiteCastlingPossibility == 0) return res;
        if (color == Color.BLACK && isBlackCastlingPossibility == 0) return res;

        Figure king = gameSettings.board.findKing(color);
        if (king == null) throw new ChessError(KING_NOT_FOUND);
        if (king.wasMoved()) return res;
        if (gameSettings.board.isNotRightRookStandardMoved(color)) res += "k";
        if (gameSettings.board.isNotLeftRookStandardMoved(color)) res += "q";

        if (res.isEmpty()) {
            if (color == Color.WHITE) isWhiteCastlingPossibility = 0;
            else isBlackCastlingPossibility = 0;
        }
        return color == Color.WHITE ? res.toUpperCase() : res;
    }

    /**
     * @return Строка - часть записи (c пробелом вначале) отвечающая за то, доступно ли взятие на
     *     проходе следующим ходом
     */
    private String getPawnEnPassantPossibility() {
        StringBuilder result = new StringBuilder();
        if (lastMove != null && lastMove.getMoveType() == MoveType.LONG_MOVE) {
            result.append(' ').append((char) (lastMove.getTo().column + 'a'));
            result.append(
                    gameSettings.board.getFigureUgly(lastMove.getTo()).getColor() == Color.WHITE
                            ? '3'
                            : '6');
        }
        return result.toString();
    }

    public int getPeaceMoveCount() {
        return peaceMoveCount;
    }

    /** @return последнее состояние доски */
    public BoardState getLastBoardState() {
        return recordsList.peek();
    }

    /**
     * @return true - если было минимум repetition-кратных повторений последней доски, false - если
     *     было меньше
     */
    public boolean checkRepetitions(int repetition) {
        final BoardState lastState = recordsList.peek();
        if (lastState == null) return false;
        return repetitionsMap.get(recordsList.peek()) >= repetition;
    }

    public Move getLastMove() {
        return lastMove;
    }

    /** Отменяет последний ход в истории */
    public void undo() {
        BoardState lastBoardState = recordsList.pop();
        BoardState prevLastBoardState = recordsList.peek();
        if (prevLastBoardState == null) return;
        hasMovedBeforeLastMove = prevLastBoardState.hasMovedBeforeLastMove;
        removedFigure = prevLastBoardState.removedFigure;
        lastMove = prevLastBoardState.lastMove;
        peaceMoveCount = prevLastBoardState.peaceMoveCount;
        isWhiteMove = prevLastBoardState.isWhiteMove;
        isWhiteCastlingPossibility = prevLastBoardState.isWhiteCastlingPossibility;
        isBlackCastlingPossibility = prevLastBoardState.isBlackCastlingPossibility;
        int boardStateCount = repetitionsMap.remove(lastBoardState);
        if (boardStateCount > 1) repetitionsMap.put(lastBoardState, boardStateCount - 1);
    }

    /** Берет последние данные из истории и обновляет текущие */
    public void restore() {
        BoardState lastBoardState = recordsList.peek();
        if (lastBoardState == null) return;
        hasMovedBeforeLastMove = lastBoardState.hasMovedBeforeLastMove;
        removedFigure = lastBoardState.removedFigure;
        lastMove = lastBoardState.lastMove;
        peaceMoveCount = lastBoardState.peaceMoveCount;
        isWhiteMove = lastBoardState.isWhiteMove;
        isWhiteCastlingPossibility = lastBoardState.isWhiteCastlingPossibility;
        isBlackCastlingPossibility = lastBoardState.isBlackCastlingPossibility;
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
        isWhiteMove = boardState.isWhiteMove;
        isWhiteCastlingPossibility = boardState.isWhiteCastlingPossibility;
        isBlackCastlingPossibility = boardState.isBlackCastlingPossibility;
        recordsList.push(boardState);
        repetitionsMap.put(boardState, repetitionsMap.getOrDefault(boardState, 0) + 1);
    }
}
