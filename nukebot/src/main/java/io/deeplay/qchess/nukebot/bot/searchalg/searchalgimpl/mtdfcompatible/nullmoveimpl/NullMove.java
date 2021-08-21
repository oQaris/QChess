package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;

public abstract class NullMove extends SearchAlgorithm {

    public static final int DEPTH_REDUCTION = 2;

    protected NullMove(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(resultUpdater, mainMove, moveVersion, gs, color, evaluationFunc, maxDepth);
    }

    protected boolean isAllowNullMove(
            final Color color,
            final boolean isCheckToColor,
            final boolean isCheckToEnemyColor,
            final boolean isStalemateToCurrentEnemy) {
        return !isStalemateToCurrentEnemy
                && gs.board.getFigureCount(color.inverse()) > 9
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
        return !isPrevNullMove
                && isAllowNullMove(
                        color,
                        gs.endGameDetector.isCheck(color),
                        gs.endGameDetector.isCheck(color.inverse()),
                        gs.endGameDetector.isStalemate(color.inverse()));
    }

    protected boolean isNotCapture(final Move move) {
        return switch (move.getMoveType()) {
            case ATTACK, TURN_INTO_ATTACK, EN_PASSANT -> false;
            default -> true;
        };
    }
}
