package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.ParallelSearch;

public abstract class MTDFSearch extends ParallelSearch {

    public MTDFSearch(GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    public int MTDFStart(boolean isMyMove, int firstGuess, int depth) throws ChessError {
        // firstGuess = 0;
        for (int d = 1; d <= depth; ++d) {
            firstGuess = MTDF(isMyMove, firstGuess, d);
            // if (timesUp()) break;
        }
        return firstGuess;
    }

    private int MTDF(boolean isMyMove, int firstGuess, int depth) throws ChessError {
        int estimation = firstGuess;
        int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        int upperBound = EvaluationFunc.MAX_ESTIMATION;
        int beta;
        while (lowerBound < upperBound) {
            if (estimation == lowerBound) beta = estimation + 1;
            else beta = estimation;
            estimation = alfaBetaWithMemory(isMyMove, beta - 1, beta, depth);
            if (estimation < beta) upperBound = estimation;
            else lowerBound = estimation;
        }
        return estimation;
    }

    public abstract int alfaBetaWithMemory(boolean isMyMove, int alfa, int beta, int depth)
            throws ChessError;
}
