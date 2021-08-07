package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTableWithFlag;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible.MTDFSearch;

public abstract class NullMoveWithTT extends MTDFSearch {

    public static final int DEPTH_REDUCTION = 2;

    protected final TranspositionTableWithFlag table = new TranspositionTableWithFlag();
    protected boolean isPrevNullMove;

    protected NullMoveWithTT(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    protected boolean isAllowNullMove(Color color) {
        return !isPrevNullMove
                && gs.board.getFigureCount(color) > 5
                && !gs.endGameDetector.isCheck(color);
        /*
         * TODO: (улучшить) null-move запрещен, если выполнено одно из следующих условий:
         *  1. Текущий игрок имеет только короля и пешки
         *  2. У текущего игрока осталось мало материала
         */
    }
}
