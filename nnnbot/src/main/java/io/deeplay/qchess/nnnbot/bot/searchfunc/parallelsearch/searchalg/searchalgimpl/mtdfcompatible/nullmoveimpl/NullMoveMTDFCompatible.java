package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.tt.TranspositionTable;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible.MTDFSearch;

public abstract class NullMoveMTDFCompatible extends MTDFSearch {

    public static final int DEPTH_REDUCTION = 2;

    protected boolean isPrevNullMove;

    protected NullMoveMTDFCompatible(
            final TranspositionTable table,
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(table, updater, mainMove, gs, color, evaluationFunc, maxDepth);
    }

    protected boolean isAllowNullMove(Color color) {
        Color enemyColor = color.inverse();
        return !isPrevNullMove
                && !gs.endGameDetector.isStalemate(enemyColor)
                && gs.board.getFigureCount(enemyColor) > 9
                && !gs.endGameDetector.isCheck(color)
                && !gs.endGameDetector.isCheck(enemyColor);
        /*
         * TODO: (улучшить) null-move запрещен, если выполнено одно из следующих условий:
         *  1. Противник имеет только короля и пешки
         *  2. У противника осталось мало материала
         *  3. Осталось мало материала на доске
         *  4. Число ходов превышает.
         */
    }
}
