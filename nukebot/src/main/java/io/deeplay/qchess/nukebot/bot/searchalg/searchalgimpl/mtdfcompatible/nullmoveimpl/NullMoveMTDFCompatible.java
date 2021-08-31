package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.MTDFSearch;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;

public abstract class NullMoveMTDFCompatible extends MTDFSearch {

    public static final int DEPTH_REDUCTION = 3;

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

    protected boolean isAllowNullMove(
            final Color color,
            final boolean isPrevNullMove,
            final boolean isCheckToColor,
            final boolean isCheckToEnemyColor) {
        final Color enemyColor = color.inverse();
        return !isPrevNullMove
                && !gs.endGameDetector.isStalemate(enemyColor, table)
                && gs.board.getFigureCount(enemyColor) > 9
                && !isCheckToColor
                && !isCheckToEnemyColor;
        /*
         * TODO: (улучшить) null-move запрещен, если выполнено одно из следующих условий:
         *  1. Противник имеет только короля и пешки
         *  2. У противника осталось мало материала
         *  3. Осталось мало материала на доске
         *  4. Число ходов превышает.
         */
    }

    protected boolean isAllowNullMove(final Color color, final boolean isPrevNullMove) {
        return isAllowNullMove(
                color,
                isPrevNullMove,
                gs.endGameDetector.isCheck(color),
                gs.endGameDetector.isCheck(color.inverse()));
    }

    protected boolean isNotCapture(final Move move) {
        return switch (move.getMoveType()) {
            case ATTACK, TURN_INTO_ATTACK, EN_PASSANT -> false;
            default -> true;
        };
    }
}
