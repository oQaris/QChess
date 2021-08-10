package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.tt;

import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {

    // TODO: use ConcurrentMap
    private final Map<BoardState, TTEntry> entries = new HashMap<>(500000);

    /** @return вхождение состояния игры или null, если такое состояние еще не встречалось */
    public TTEntry find(BoardState boardState) {
        return entries.get(boardState);
    }

    public void store(BoardState boardState, TTEntry entry) {
        entries.put(boardState, entry);
    }

    public static class TTEntry {
        public int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        public int upperBound = EvaluationFunc.MAX_ESTIMATION;
        public int depth;

        public TTEntry(int depth) {
            this.depth = depth;
        }
    }
}
