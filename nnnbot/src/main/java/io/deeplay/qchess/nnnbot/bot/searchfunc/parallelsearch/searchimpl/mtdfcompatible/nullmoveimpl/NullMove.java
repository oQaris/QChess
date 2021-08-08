package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible.MTDFSearch;

public abstract class NullMove extends MTDFSearch {

    public static final int DEPTH_REDUCTION = 2;

    protected boolean isPrevNullMove;

    protected NullMove(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
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
