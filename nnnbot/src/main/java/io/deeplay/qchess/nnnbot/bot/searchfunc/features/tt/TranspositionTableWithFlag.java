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
            List<Move> allMoves,
            TTEntry entry,
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

    /**
     * @deprecated Везде используется одинаковый код из {@link #store(List, TTEntry, int,
     *     BoardState, int, int, int) этого метода}, т.к. ТТ не используется без отсечений
     */
    @Deprecated
    public void store(BoardState boardState, TTEntry entry) {
        entries.put(boardState, entry);
    }

    public static class TTEntry {

        public int estimation;
        public int depth;
        public TTEntryFlag flag = TTEntryFlag.EXACT;
        public List<Move> allMoves;

        public TTEntry(List<Move> allMoves, int estimation, int depth) {
            this.estimation = estimation;
            this.depth = depth;
            this.allMoves = allMoves;
        }

        public enum TTEntryFlag {
            EXACT,
            LOWERBOUND,
            UPPERBOUND
        }
    }
}
