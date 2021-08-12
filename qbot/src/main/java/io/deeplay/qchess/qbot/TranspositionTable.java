package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.model.BoardState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranspositionTable {

    private final Map<BoardState, TTEntry> entries = new ConcurrentHashMap<>(500000);

    /** @return вхождение состояния игры или null, если такое состояние еще не встречалось */
    public TTEntry find(BoardState boardState) {
        // return entries.get(boardState);
        return null;
    }

    /**
     * Кладет результат во вхождение, если оно есть, иначе создает новое и помещает в ТТ
     *
     * @param entry вхождение
     * @param boardState состояние доски
     */
    public void store(TTEntry entry, final BoardState boardState) {
        entries.put(boardState, entry);
    }

    public static class TTEntry {
        public Flag flag;
        public int value;
        public int depth;
        public TTEntry(int value, int depth, Flag flag) {
            this.value = value;
            this.depth = depth;
            this.flag = flag;
        }

        enum Flag {
            EXACT,
            LOWERBOUND,
            UPPERBOUND
        }
    }
}
