package io.deeplay.qchess.nnnbot.bot.searchfunc.features;

import java.util.Map;

/**
 * Использует возможные ходы вместо состояний игры, поэтому при повторении позиции одного игрока
 * возможна неправильная оценка состояния игры. TODO: проверить эффективность для состояний
 */
public class TranspositionTable {

    /** Integer используется для ускорения вычислений, возможны коллизии */
    private Map<Integer, TTEntry> entries;

    /**
     * @param allMovesHash хеш ходов
     * @return вхождение состояния игры или null, если такое состояние еще не встречалось
     */
    public TTEntry find(Integer allMovesHash) {
        return entries.get(allMovesHash);
    }

    /** @param allMovesHash хеш ходов */
    public void store(Integer allMovesHash, TTEntry entry) {
        entries.put(allMovesHash, entry);
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
