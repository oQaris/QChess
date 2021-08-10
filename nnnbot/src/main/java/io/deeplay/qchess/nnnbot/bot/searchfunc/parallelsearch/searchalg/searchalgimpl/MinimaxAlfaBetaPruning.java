package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.SearchImprovements;
import java.util.List;

/**
 * Реализует алгоритм поиска минимаксом, поэтому желательно использовать функцию оценки не зависящую
 * от цвета игрока (должна быть с нулевой суммой, т.е. для текущего игрока возвращать максимум, а
 * для противника минимум)
 */
public class MinimaxAlfaBetaPruning extends SearchAlgorithm {

    public MinimaxAlfaBetaPruning(
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(updater, mainMove, gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public void run() {
        try {
            gs.moveSystem.move(mainMove);
            final int est =
                    minimax(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth);
            updater.updateResult(mainMove, est);
            gs.moveSystem.undoMove();
        } catch (ChessError ignore) {
        }
    }

    /**
     * @param isMyMove true, если это максимизирующий игрок, false - минимизирующий
     * @param alfa лучшая оценка из гарантированных для текущего игрока
     * @param beta лучшая оценка из гарантированных для противника
     * @return лучшая оценка из гарантированных для максимизирующего игрока
     */
    private int minimax(boolean isMyMove, int alfa, int beta, int depth) throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves)) return getEvaluation(allMoves, isMyMove, depth);

        int optEstimation =
                isMyMove ? EvaluationFunc.MIN_ESTIMATION : EvaluationFunc.MAX_ESTIMATION;
        int estimation;

        SearchImprovements.prioritySort(allMoves);

        for (Move move : allMoves) {
            gs.moveSystem.move(move);
            estimation = minimax(!isMyMove, alfa, beta, depth - 1);
            gs.moveSystem.undoMove();

            if (isMyMove) {
                if (estimation > optEstimation) optEstimation = estimation;
                if (optEstimation > alfa) alfa = optEstimation;
            } else {
                if (estimation < optEstimation) optEstimation = estimation;
                if (optEstimation < beta) beta = optEstimation;
            }
            if (beta <= alfa) break;
        }

        return optEstimation;
    }
}
