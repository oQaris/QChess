package io.deeplay.qchess.nukebot.bot.searchalg.features;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.features.ITranspositionTable;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: сделать и использовать везде интерфейс
public class TranspositionTable implements ITranspositionTable {

    private static final int MAX_NODES = 1000000;

    /** Хранит вхождения игровых состояний */
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
        store(allMoves, null, 0, 0, 0, result, boardState, alfaOrigin, betaOrigin, depth);
    }

    public void store(
            final List<Move> allMoves,
            final List<Move> attackMoves,
            final int isAllowNullMove,
            final int isCheckToColor,
            final int isCheckToEnemyColor,
            final int result,
            final BoardState boardState,
            final int alfaOrigin,
            final int betaOrigin,
            final int depth) {
        final TTEntry entry =
                new TTEntry(
                        allMoves,
                        attackMoves,
                        depth,
                        isAllowNullMove,
                        isCheckToColor,
                        isCheckToEnemyColor);

        if (result <= alfaOrigin) entry.upperBound = result;
        if (result >= betaOrigin) entry.lowerBound = result;
        if (alfaOrigin < result && result < betaOrigin)
            entry.lowerBound = entry.upperBound = result;

        entries.put(boardState, entry);
    }

    @Override
    public boolean isCheckTo(
            final GameSettings gs, final BoardState boardState, final Color color) {
        final TTEntry entry = entries.get(boardState);
        if (entry == null) return gs.endGameDetector.isCheck(color);
        if (color == Color.WHITE) return entry.isCheckToWhite == 1;
        return entry.isCheckToBlack == 1;
    }

    public static class TTEntry {

        public List<Move> allMoves;
        public List<Move> attackMoves;
        public int isAllowNullMove;
        public int isCheckToWhite;
        public int isCheckToBlack;
        public int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        public int upperBound = EvaluationFunc.MAX_ESTIMATION;
        public int depth;

        public TTEntry(
                final List<Move> allMoves,
                final List<Move> attackMoves,
                final int depth,
                final int isAllowNullMove,
                final int isCheckToWhite,
                final int isCheckToBlack) {
            this.allMoves = allMoves;
            this.attackMoves = attackMoves;
            this.depth = depth;
            this.isAllowNullMove = isAllowNullMove;
            this.isCheckToWhite = isCheckToWhite;
            this.isCheckToBlack = isCheckToBlack;
        }
    }
}
