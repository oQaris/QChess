package io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt;

import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTableWithFlag.TTEntry.TTEntryFlag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranspositionTableWithFlag {

    // TODO: use ConcurrentMap
    private final Map<BoardState, TTEntry> entries = new HashMap<>(500000);

    /** @return вхождение состояния игры или null, если такое состояние еще не встречалось */
    public TTEntry find(BoardState boardState) {
        return entries.get(boardState);
    }

    /**
     * Кладет результат во вхождение, если оно есть, иначе создает новое и помещает в ТТ
     *
     * @param entry вхождение, возможно null
     * @param result лучшая оценка
     * @param boardState состояние доски
     * @param alfaOrigin первоначальная альфа в отсечениях
     * @param beta бета в отсечениях
     * @param depth текущая глубина
     */
    public void store(
            TTEntry entry,
            List<Move> allMoves,
            int result,
            BoardState boardState,
            int alfaOrigin,
            int beta,
            int depth) {
        if (entry == null) {
            entry = new TTEntry(allMoves, result, depth);
            entries.put(boardState, entry);
        } else {
            entry.depth = depth;
            entry.estimation = result;
            entry.allMoves = allMoves;
        }

        if (result <= alfaOrigin) entry.flag = TTEntryFlag.UPPERBOUND;
        else if (beta <= result) entry.flag = TTEntryFlag.LOWERBOUND;
        else entry.flag = TTEntryFlag.EXACT;
    }

    public static class TTEntry {

        public List<Move> allMoves;
        public int estimation;
        public int depth;
        public TTEntryFlag flag = TTEntryFlag.EXACT;

        public TTEntry(List<Move> allMoves, int estimation, int depth) {
            this.allMoves = allMoves;
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
