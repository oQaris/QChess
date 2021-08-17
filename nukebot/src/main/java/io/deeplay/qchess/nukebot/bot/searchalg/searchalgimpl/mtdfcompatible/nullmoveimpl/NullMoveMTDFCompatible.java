package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.MTDFSearch;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;

public abstract class NullMoveMTDFCompatible extends MTDFSearch {

    public static final int DEPTH_REDUCTION = 2;

    protected boolean isPrevNullMove;

    protected NullMoveMTDFCompatible(
            final TranspositionTable table,
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(table, resultUpdater, mainMove, moveVersion, gs, color, evaluationFunc, maxDepth);
    }

    protected boolean isAllowNullMove(final Color color) {
        final Color enemyColor = color.inverse();
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
