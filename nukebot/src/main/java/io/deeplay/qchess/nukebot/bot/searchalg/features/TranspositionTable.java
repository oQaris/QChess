package io.deeplay.qchess.nukebot.bot.searchalg.features;

import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranspositionTable {

    private static final int MAX_NODES = 1000000;

    private final Map<BoardState, TTEntry> entries = new ConcurrentHashMap<>(MAX_NODES);

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
        final TTEntry entry = new TTEntry(allMoves, depth);

        if (result <= alfaOrigin) entry.upperBound = result;
        if (result >= betaOrigin) entry.lowerBound = result;
        if (alfaOrigin < result && result < betaOrigin)
            entry.lowerBound = entry.upperBound = result;

        entries.put(boardState, entry);
    }

    public static class TTEntry {

        public List<Move> allMoves;
        public int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        public int upperBound = EvaluationFunc.MAX_ESTIMATION;
        public int depth;

        public TTEntry(final List<Move> allMoves, final int depth) {
            this.allMoves = allMoves;
            this.depth = depth;
        }
    }
}
