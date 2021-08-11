package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible.MTDFSearch;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible.NegaScoutWithTT;

public abstract class SearchAlgorithmFactory {

    public static SearchAlgorithm getSearchAlgorithm(
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        final TranspositionTable table = new TranspositionTable();
        return new NegaScoutWithTT(table, updater, mainMove, gs, color, evaluationFunc, maxDepth);
    }

    public static MTDFSearch getMTDFCompatibleAlgorithm(
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        final TranspositionTable table = new TranspositionTable();
        return new NegaScoutWithTT(table, updater, mainMove, gs, color, evaluationFunc, maxDepth);
    }
}
