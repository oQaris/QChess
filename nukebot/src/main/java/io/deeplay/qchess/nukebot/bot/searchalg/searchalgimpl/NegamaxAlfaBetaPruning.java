package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.CommonEvaluationConstructor;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgErrorCode;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgException;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.List;

/** Реализует алгоритм поиска негамаксом с альфа-бета отсечениями */
public class NegamaxAlfaBetaPruning extends NegaAlfaBeta {

    public NegamaxAlfaBetaPruning(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color myColor,
            final CommonEvaluationConstructor commonEvaluationConstructor,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(
                resultUpdater,
                mainMove,
                moveVersion,
                gs,
                myColor,
                commonEvaluationConstructor,
                evaluationFunc,
                maxDepth);
    }

    @Override
    public void run() {
        try {
            makeMove(mainMove);
            final int est =
                    -super.negaSearch(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth);
            updateResult(est);
            undoMove();
        } catch (final ChessError e) {
            throw new SearchAlgException(SearchAlgErrorCode.SEARCH_ALG, e);
        }
    }

    @Override
    public int negaSearch(final boolean isMyMove, int alfa, final int beta, final int depth)
            throws ChessError {
        final List<Move> allMoves = getLegalMoves(isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves)) {
            final int est = getEvaluation(allMoves, isMyMove, alfa, beta, depth);
            return isMyMove ? est : -est;
        }

        prioritySort(allMoves);

        int optEstimation = EvaluationFunc.MIN_ESTIMATION;

        for (final Move move : allMoves) {
            makeMove(move);
            final int estimation = -super.negaSearch(!isMyMove, -beta, -alfa, depth - 1);
            undoMove();

            if (estimation > optEstimation) optEstimation = estimation;
            if (optEstimation > alfa) alfa = optEstimation;
            if (beta <= alfa) break;
        }

        return optEstimation;
    }
}
