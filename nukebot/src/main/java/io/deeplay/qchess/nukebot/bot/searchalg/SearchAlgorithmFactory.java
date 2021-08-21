package io.deeplay.qchess.nukebot.bot.searchalg;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.NegamaxAlfaBetaPruning;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.MTDFSearch;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl.UltimateQuintessence;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;

public abstract class SearchAlgorithmFactory {

    public static SearchAlgorithm getSearchAlgorithm(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        return new NegamaxAlfaBetaPruning(
                resultUpdater, mainMove, moveVersion, gs, color, evaluationFunc, maxDepth);
    }

    public static MTDFSearch getMTDFCompatibleAlgorithm(
            final TranspositionTable table,
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        return new UltimateQuintessence(
                table, resultUpdater, mainMove, moveVersion, gs, color, evaluationFunc, maxDepth);
    }
}
