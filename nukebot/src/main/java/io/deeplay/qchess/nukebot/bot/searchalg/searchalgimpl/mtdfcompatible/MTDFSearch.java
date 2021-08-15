package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;

public abstract class MTDFSearch extends SearchAlgorithm {

    protected final TranspositionTable table;

    public MTDFSearch(
            final TranspositionTable table,
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(resultUpdater, mainMove, moveVersion, gs, color, evaluationFunc, maxDepth);
        this.table = table;
    }

    public void MTDFStart(int firstGuess, final int depth, final long maxTimeMillis) {
        try {
            gs.moveSystem.move(mainMove);
            final long startTimeMillis = System.currentTimeMillis();
            for (int d = 1; d <= depth; ++d) {
                firstGuess = MTDF(firstGuess, d);
                resultUpdater.updateResult(mainMove, firstGuess, d, moveVersion);
                if (timesUp(startTimeMillis, maxTimeMillis)) break;
            }
            gs.moveSystem.undoMove();
        } catch (final ChessError ignore) {
        }
    }

    private boolean timesUp(final long startTimeMillis, final long maxTimeMillis) {
        return System.currentTimeMillis() - startTimeMillis > maxTimeMillis;
    }

    private int MTDF(final int firstGuess, final int depth) throws ChessError {
        int est = firstGuess;
        int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        int upperBound = EvaluationFunc.MAX_ESTIMATION;
        int beta;
        do {
            if (est == lowerBound) beta = est + 1;
            else beta = est;
            est = alfaBetaWithTT(beta - 1, beta, depth);
            if (est < beta) upperBound = est;
            else lowerBound = est;
        } while (lowerBound < upperBound);
        return est;
    }

    public abstract int alfaBetaWithTT(final int alfa, final int beta, final int depth)
            throws ChessError;
}
