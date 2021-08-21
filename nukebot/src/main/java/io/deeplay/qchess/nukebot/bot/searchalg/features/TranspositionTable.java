package io.deeplay.qchess.nukebot.bot.searchalg.features;

import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: сделать интерфейс
public class TranspositionTable {

    private static final int MAX_NODES = 1000000;

    /** [Сторона, чей ход] -> [откуда] -> [куда] */
    // private final AtomicInteger[][][] moveHistory = new AtomicInteger[2][64][64];
    private final int[][][] moveHistory = new int[2][64][64];
    /** [Сторона, чей ход] -> [откуда] -> [куда] */
    // private final AtomicInteger[][][] butterfly = new AtomicInteger[2][64][64];
    private final int[][][] butterfly = new int[2][64][64];
    /** Хранит вхождения игровых состояний */
    private final Map<BoardState, TTEntry> entries = new ConcurrentHashMap<>(MAX_NODES);

    {
        for (int color = 0; color < 2; ++color)
            for (int y = 0; y < 64; ++y)
                for (int x = 0; x < 64; ++x) {
                    // moveHistory[color][y][x] = new AtomicInteger(1);
                    // butterfly[color][y][x] = new AtomicInteger(1);
                    moveHistory[color][y][x] = 1;
                    butterfly[color][y][x] = 1;
                }
    }

    /* TODO:
     *  AtomicInteger почти останавливает вычисления к середине игры, тем не менее требуются
     *  дополнительные тесты
     */

    public void addMoveHistory(final int color, final int y, final int x, final int value) {
        // moveHistory[color][y][x].addAndGet(value);
        moveHistory[color][y][x] += value;
    }

    public int getMoveHistory(final int color, final int y, final int x) {
        // return moveHistory[color][y][x].get();
        return moveHistory[color][y][x];
    }

    public void addButterfly(final int color, final int y, final int x, final int value) {
        // butterfly[color][y][x].addAndGet(value);
        butterfly[color][y][x] += value;
    }

    public int getButterfly(final int color, final int y, final int x) {
        // return butterfly[color][y][x].get();
        return butterfly[color][y][x];
    }

    /** @return вхождение состояния игры или null, если такое состояние еще не встречалось */
    public TTEntry find(final BoardState boardState) {
        return entries.get(boardState);
    }

    /**
     * Создает новое вхождение и добавляет его в ТТ, либо заменяет, если оно уже есть
     *
     * @param allMoves все возможные текущие ходы
     * @param result лучшая оценка
     * @param boardState состояние доски
     * @param alfaOrigin первоначальная альфа в отсечениях
     * @param betaOrigin первоначальная бета в отсечениях
     * @param depth текущая глубина
     */
    public void store(
            final List<Move> allMoves,
            final int result,
            final BoardState boardState,
            final int alfaOrigin,
            final int betaOrigin,
            final int depth) {
        store(allMoves, null, 0, result, boardState, alfaOrigin, betaOrigin, depth);
    }

    public void store(
            final List<Move> allMoves,
            final List<Move> attackMoves,
            final int isAllowNullMove,
            final int result,
            final BoardState boardState,
            final int alfaOrigin,
            final int betaOrigin,
            final int depth) {
        final TTEntry entry = new TTEntry(allMoves, attackMoves, depth, isAllowNullMove);

        if (result <= alfaOrigin) entry.upperBound = result;
        if (result >= betaOrigin) entry.lowerBound = result;
        if (alfaOrigin < result && result < betaOrigin)
            entry.lowerBound = entry.upperBound = result;

        entries.put(boardState, entry);
    }

    public static class TTEntry {

        public List<Move> allMoves;
        public List<Move> attackMoves;
        public int isAllowNullMove;
        public int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        public int upperBound = EvaluationFunc.MAX_ESTIMATION;
        public int depth;

        public TTEntry(
                final List<Move> allMoves,
                final List<Move> attackMoves,
                final int depth,
                final int isAllowNullMove) {
            this.allMoves = allMoves;
            this.attackMoves = attackMoves;
            this.depth = depth;
            this.isAllowNullMove = isAllowNullMove;
        }
    }
}
