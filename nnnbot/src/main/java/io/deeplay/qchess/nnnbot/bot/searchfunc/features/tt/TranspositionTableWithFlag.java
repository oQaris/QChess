package io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt;

import io.deeplay.qchess.game.model.BoardState;
import java.util.HashMap;
import java.util.Map;

public class TranspositionTableWithFlag {

    private final Map<BoardState, TTEntry> entries;

    public TranspositionTableWithFlag(int size) {
        entries = new HashMap<>(size); // TODO: use ConcurrentMap
    }

    /** @return вхождение состояния игры или null, если такое состояние еще не встречалось */
    public TTEntry find(BoardState boardState) {
        return entries.get(boardState);
    }

    public void store(BoardState boardState, TTEntry entry) {
        entries.put(boardState, entry);
    }

    public static class TTEntry {

        public final int estimation;
        public final int depth;
        public TTEntryFlag flag = TTEntryFlag.EXACT;

        public TTEntry(int estimation, int depth) {
            this.estimation = estimation;
            this.depth = depth;
        }

        public enum TTEntryFlag {
            EXACT,
            LOWERBOUND,
            UPPERBOUND
        }
    }
}
