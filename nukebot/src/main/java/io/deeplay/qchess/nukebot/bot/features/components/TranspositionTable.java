package io.deeplay.qchess.nukebot.bot.features.components;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.features.ITranspositionTable;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranspositionTable implements ITranspositionTable {

    private static final int MAX_NODES = 1000000;

    /** Хранит вхождения игровых состояний */
    private final Map<BoardState, TTEntry> entries = new ConcurrentHashMap<>(MAX_NODES);

    /**
     * Ищет вхождение состояния игры
     *
     * @param boardState состояние доски
     * @return вхождение состояния игры или null, если такое состояние еще не встречалось
     */
    public TTEntry find(final BoardState boardState) {
        return entries.get(boardState);
    }

    /**
     * Создает новое вхождение и добавляет его в ТТ, либо заменяет, если оно уже есть
     *
     * @param boardState состояние доски
     * @param allMoves все возможные текущие ходы
     */
    public void storeAllMoves(final BoardState boardState, final List<Move> allMoves) {
        entries.compute(
                boardState,
                (bs, en) -> {
                    if (en == null) en = new TTEntry();
                    en.allMoves = allMoves;
                    return en;
                });
    }

    /**
     * Создает новое вхождение и добавляет его в ТТ, либо заменяет, если оно уже есть
     *
     * @param boardState состояние доски
     * @param result лучшая оценка
     * @param alfaOrigin первоначальная альфа в отсечениях
     * @param betaOrigin первоначальная бета в отсечениях
     * @param depth текущая глубина
     */
    public void storeEstimation(
            final BoardState boardState,
            final int result,
            final int alfaOrigin,
            final int betaOrigin,
            final int depth) {
        entries.compute(
                boardState,
                (bs, en) -> {
                    if (en == null) en = new TTEntry();
                    en.depth = depth;
                    if (result <= alfaOrigin) en.upperBound = result;
                    if (result >= betaOrigin) en.lowerBound = result;
                    if (alfaOrigin < result && result < betaOrigin)
                        en.lowerBound = en.upperBound = result;
                    return en;
                });
    }

    @Override
    public boolean isCheckTo(
            final GameSettings gs, final BoardState boardState, final Color color) {
        final TTEntry entry =
                entries.compute(
                        boardState,
                        (bs, en) -> {
                            if (en == null) en = new TTEntry();
                            if (en.isCheckToWhite == null)
                                en.isCheckToWhite = gs.endGameDetector.isCheck(Color.WHITE);
                            if (en.isCheckToBlack == null)
                                en.isCheckToBlack = gs.endGameDetector.isCheck(Color.BLACK);
                            return en;
                        });
        return color == Color.WHITE ? entry.isCheckToWhite : entry.isCheckToBlack;
    }

    public static class TTEntry {

        // null == значение не определено

        public List<Move> allMoves;
        public Boolean isCheckToWhite;
        public Boolean isCheckToBlack;
        // Границы
        public int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        public int upperBound = EvaluationFunc.MAX_ESTIMATION;
        public int depth = // нужно, чтобы нельзя было использовать границы в ТТ по умолчанию
                EvaluationFunc.MIN_ESTIMATION;
    }
}
