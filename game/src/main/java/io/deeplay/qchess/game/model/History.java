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
import java.util.Iterator;
import java.util.Map;

public class History implements Iterable<BoardState> {
    /** Среднее из максимальных число ходов за 1 партию */
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
    private final Map<BoardState, Integer> repetitionsMap;
    /** Используется как стек */
    private final Deque<BoardState> recordsList;

    private Move lastMove;
    /** Двигалась ли фигура до последнего хода (фигура, которая совершила этот последний ход) */
    private boolean hasMovedBeforeLastMove;
    /** Исключая пешку при взятии на проходе */
    private Figure removedFigure;

    private int peaceMoveCount;
    private boolean isWhiteMove = true;
    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    private int isWhiteCastlingPossibility = 3;
    /** 0 - нет возможности рокироваться, 1 - левая рокировка возможна, 2 - правая, 3 - обе */
    private int isBlackCastlingPossibility = 3;

    /** Минимум состояний доски в истории ходов, которое необходимо сохранить после чистки */
    private int minBoardStateToSave;

    /**
     * Родитель текущей истории или null, если текущая история является корнем (обычно это история
     * основной партии, не симуляции бота)
     */
    private History parentHistory;

    public History(final GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        repetitionsMap = new HashMap<>(AVERAGE_MAXIMUM_MOVES);
        recordsList = new ArrayDeque<>(AVERAGE_MAXIMUM_MOVES);
    }

    /** Создает новую историю с ссылкой на предыдущую */
    public History(
            final History history, final GameSettings gameSettings, final int averageMaxMoves) {
        this.gameSettings = gameSettings;
        repetitionsMap = new HashMap<>(averageMaxMoves + 2); // +2 extra moves (ну мало ли что)
        recordsList = new ArrayDeque<>(averageMaxMoves + 2);
        parentHistory = history;
        recordsList.push(history.recordsList.peek());
        restore();
    }

    /**
     * Добавляет в список записей запись текущего состояния доски
     *
     * @param lastMove последний ход или null, если его не было
     */
    public void addRecord(final Move lastMove) throws ChessError {
        this.lastMove = lastMove;
        isWhiteMove = !isWhiteMove;

        final int[] boardSnapshot = gameSettings.board.fastSnapshot();

        isWhiteCastlingPossibility = gameSettings.board.isCastlingPossible(Color.WHITE);
        isBlackCastlingPossibility = gameSettings.board.isCastlingPossible(Color.BLACK);

        final BoardState boardState =
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

    /** Добавляет запись в историю, но не меняет цвет игрока, который будет делать следующий ход */
    public void addRecordButNotChangeMoveSide(final Move lastMove) throws ChessError {
        isWhiteMove = !isWhiteMove;
        addRecord(lastMove);
    }

    /**
     * @return true, если фигура двигалась до последнего хода (фигура, которая совершила этот
     *     последний ход)
     */
    public boolean isHasMovedBeforeLastMove() {
        return hasMovedBeforeLastMove;
    }

    /**
     * Устанавливает, двигалась ли фигура до последнего хода (фигура, которая совершила этот
     * последний ход)
     */
    public void setHasMovedBeforeLastMove(final boolean hasMoved) {
        hasMovedBeforeLastMove = hasMoved;
    }

    /** @return последняя взятая фигура или null, если последний ход не был атакующим */
    public Figure getRemovedFigure() {
        return removedFigure;
    }

    /** Устанавливает последнюю взятую фигуру */
    public void setRemovedFigure(final Figure removedFigure) {
        this.removedFigure = removedFigure;
    }

    /**
     * Добавляет 1 к мирным ходам, если ход move не был ходом пешки или взятием. Также очищает
     * историю, если возможно
     *
     * @param move сделанный ход
     * @param moveFigureType тип фигуры, которой был сделан ход
     * @param removedFigure фигура, которую взяли или null, если ход не атакующий
     */
    public void checkAndAddPeaceMoveCount(
            final Move move, final FigureType moveFigureType, final Figure removedFigure) {
        switch (move.getMoveType()) {
            case ATTACK, TURN_INTO, TURN_INTO_ATTACK, EN_PASSANT:
                if (parentHistory == null) clearHistory(minBoardStateToSave);
                peaceMoveCount = 0;
                break;
            default:
                if (moveFigureType == FigureType.PAWN) {
                    if (parentHistory == null) clearHistory(minBoardStateToSave);
                    peaceMoveCount = 0;
                } else ++peaceMoveCount;
                break;
        }
    }

    /**
     * @param move ход, после которого проверяется на возможность очищения
     * @param moveFigureType тип фигуры, которой нужно походить
     * @param removedFigure фигура, которую возьмут или null, если ход не атакующий
     * @return true, если история очистится после хода move
     */
    public boolean willHistoryClear(
            final Move move, final FigureType moveFigureType, final Figure removedFigure) {
        return parentHistory == null
                && switch (move.getMoveType()) {
                    case ATTACK, TURN_INTO, TURN_INTO_ATTACK, EN_PASSANT -> true;
                    default -> moveFigureType == FigureType.PAWN;
                };
    }

    /**
     * Устанавливает минимум состояний доски в истории ходов, которое необходимо сохранить после
     * чистки
     */
    public void setMinBoardStateToSave(final int minBoardStateToSave) {
        this.minBoardStateToSave = minBoardStateToSave;
    }

    /**
     * Чистит историю от ненужных состояний доски
     *
     * @param minBoardStateToSave минимум состояний доски в истории ходов, которое необходимо
     *     сохранить после чистки
     */
    public void clearHistory(final int minBoardStateToSave) {
        final int stateCountToClear = recordsList.size() - minBoardStateToSave;
        if (stateCountToClear <= 0) return;

        for (int i = 0; i < stateCountToClear; ++i) {
            final BoardState boardState = recordsList.pollLast();
            final Integer boardStateCount = repetitionsMap.remove(boardState);
            if (boardStateCount != null && boardStateCount > 1)
                repetitionsMap.put(boardState, boardStateCount - 1);
        }
    }

    /**
     * @return текущая доска в записи FEN
     * @deprecated TODO: неправильно работает и не используется
     */
    @Deprecated
    public String getBoardToStringForsythEdwards() throws ChessError {
        final StringBuilder rec = new StringBuilder(70);

        rec.append(getConvertingFigurePosition());
        rec.append(' ').append(isWhiteMove ? 'b' : 'w');

        final String castlingPossibility = getCastlingPossibility();
        if (!"".equals(castlingPossibility)) rec.append(' ').append(castlingPossibility);

        rec.append(getPawnEnPassantPossibility());
        return rec.toString();
    }

    /** @return Строка - часть записи отвечающая за позиционирование фигур на доске */
    private String getConvertingFigurePosition() {
        final StringBuilder result = new StringBuilder();
        Figure currentFigure;

        for (int y = 0; y < gameSettings.board.boardSize; ++y) {
            int emptySlots = 0;

            for (int x = 0; x < gameSettings.board.boardSize; ++x) {
                currentFigure = gameSettings.board.getFigureUgly(new Cell(x, y));

                if (currentFigure == null) ++emptySlots;
                else {
                    if (emptySlots != 0) result.append(emptySlots);
                    final Character notationFigureChar = NOTATION.get(currentFigure.figureType);
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

    /** @return Строка - часть записи отвечающая, можно ли использовать рокировки */
    private String getCastlingPossibility(final Color color) throws ChessError {
        String res = "";
        if (color == Color.WHITE && isWhiteCastlingPossibility == 0) return res;
        if (color == Color.BLACK && isBlackCastlingPossibility == 0) return res;

        final Figure king = gameSettings.board.findKing(color);
        if (king == null) throw new ChessError(KING_NOT_FOUND);
        if (king.wasMoved) return res;
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
        final StringBuilder result = new StringBuilder();
        if (lastMove != null && lastMove.getMoveType() == MoveType.LONG_MOVE) {
            result.append(' ').append((char) (lastMove.getTo().column + 'a'));
            result.append(
                    gameSettings.board.getFigureUgly(lastMove.getTo()).getColor() == Color.WHITE
                            ? '3'
                            : '6');
        }
        return result.toString();
    }

    /** @return число ходов без взятия и хода пешки */
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
    public boolean checkRepetitions(final int repetition) {
        final BoardState lastState = recordsList.peek();
        if (lastState == null) return false;
        return getRepetitions(lastState) >= repetition;
    }

    /** @return число повторений доски boardState */
    public int getRepetitions(final BoardState boardState) {
        return repetitionsMap.getOrDefault(boardState, 0)
                + (parentHistory == null ? 0 : parentHistory.getRepetitions(boardState));
    }

    /** @return последний ход */
    public Move getLastMove() {
        return lastMove;
    }

    /** Отменяет последний ход в истории */
    public void undo() {
        final BoardState lastBoardState = recordsList.pop();
        final BoardState prevLastBoardState = recordsList.peek();
        if (prevLastBoardState == null) return;
        hasMovedBeforeLastMove = prevLastBoardState.hasMovedBeforeLastMove;
        removedFigure = prevLastBoardState.removedFigure;
        lastMove = prevLastBoardState.lastMove;
        peaceMoveCount = prevLastBoardState.peaceMoveCount;
        isWhiteMove = prevLastBoardState.isWhiteMove;
        isWhiteCastlingPossibility = prevLastBoardState.isWhiteCastlingPossibility;
        isBlackCastlingPossibility = prevLastBoardState.isBlackCastlingPossibility;
        final Integer boardStateCount = repetitionsMap.remove(lastBoardState);
        if (boardStateCount != null && boardStateCount > 1)
            repetitionsMap.put(lastBoardState, boardStateCount - 1);
    }

    /** Берет последние данные из истории и обновляет текущие */
    public void restore() {
        final BoardState lastBoardState = recordsList.peek();
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
    public void redo(final BoardState boardState) {
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

    /** @return итератор от конца истории в начало */
    @Override
    public Iterator<BoardState> iterator() {
        return recordsList.iterator();
    }
}
