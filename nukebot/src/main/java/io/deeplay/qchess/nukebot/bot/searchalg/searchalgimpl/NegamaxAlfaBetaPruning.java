package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.List;

/**
 * Реализует алгоритм поиска негамаксом, поэтому желательно использовать функцию оценки не зависящую
 * от цвета игрока (должна быть с нулевой суммой, т.е. для текущего игрока возвращать максимум, а
 * для противника минимум)
 */
public class NegamaxAlfaBetaPruning extends SearchAlgorithm {

    public NegamaxAlfaBetaPruning(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(resultUpdater, mainMove, gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public void run() {
        try {
            gs.moveSystem.move(mainMove);
            final int est =
                    -negamax(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth);
            resultUpdater.updateResult(mainMove, est, maxDepth);
            gs.moveSystem.undoMove();
        } catch (final ChessError ignore) {
        }
    }

    /**
     * @param isMyMove true, если это максимизирующий игрок, false - минимизирующий
     * @param alfa лучшая оценка из гарантированных для текущего игрока
     * @param beta лучшая оценка из гарантированных для противника
     * @return лучшая оценка из гарантированных для текущего игрока
     */
    private int negamax(final boolean isMyMove, int alfa, final int beta, final int depth)
            throws ChessError {
        final List<Move> allMoves =
                gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves))
            return isMyMove
                    ? getEvaluation(allMoves, true, depth)
                    : -getEvaluation(allMoves, false, depth);

        int optEstimation = EvaluationFunc.MIN_ESTIMATION;

        SearchImprovements.prioritySort(allMoves);

        for (final Move move : allMoves) {
            gs.moveSystem.move(move);
            final int estimation = -negamax(!isMyMove, -beta, -alfa, depth - 1);
            gs.moveSystem.undoMove();

            if (estimation > optEstimation) optEstimation = estimation;
            if (optEstimation > alfa) alfa = optEstimation;
            if (beta <= alfa) break;
        }

        return optEstimation;
    }
}
