package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.NegaScoutAlfaBetaPruning;

public abstract class SearchAlgorithmFactory {

    public static SearchAlgorithm getSearchAlgorithm(
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        return new NegaScoutAlfaBetaPruning(updater, mainMove, gs, color, evaluationFunc, maxDepth);
    }
}
