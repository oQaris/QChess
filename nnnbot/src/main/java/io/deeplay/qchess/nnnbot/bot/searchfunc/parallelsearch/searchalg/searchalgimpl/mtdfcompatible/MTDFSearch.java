package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.TranspositionTable;

public abstract class MTDFSearch extends SearchAlgorithm {

    protected final TranspositionTable table;

    public MTDFSearch(
            final TranspositionTable table,
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(updater, mainMove, gs, color, evaluationFunc, maxDepth);
        this.table = table;
    }

    public int MTDFStart(boolean isMyMove, int firstGuess, int depth, long maxTimeMillis)
            throws ChessError {
        long startTimeMillis = System.currentTimeMillis();
        for (int d = 1; d <= depth; ++d) {
            firstGuess = MTDF(isMyMove, firstGuess, d);
            if (timesUp(startTimeMillis, maxTimeMillis)) break;
        }
        return firstGuess;
    }

    private boolean timesUp(final long startTimeMillis, final long maxTimeMillis) {
        return System.currentTimeMillis() - startTimeMillis > maxTimeMillis;
    }

    private int MTDF(boolean isMyMove, int firstGuess, int depth) throws ChessError {
        int est = firstGuess;
        int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        int upperBound = EvaluationFunc.MAX_ESTIMATION;
        int beta;
        do {
            if (est == lowerBound) beta = est + 1;
            else beta = est;
            est = alfaBetaWithTT(isMyMove, beta - 1, beta, depth);
            if (est < beta) upperBound = est;
            else lowerBound = est;
        } while (lowerBound < upperBound);
        return est;
    }

    public abstract int alfaBetaWithTT(boolean isMyMove, int alfa, int beta, int depth)
            throws ChessError;
}
