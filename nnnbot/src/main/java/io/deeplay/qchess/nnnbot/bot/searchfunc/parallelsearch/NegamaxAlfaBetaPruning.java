package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import java.util.Comparator;
import java.util.List;

/**
 * Реализует алгоритм поиска негамаксом, поэтому желательно использовать функцию оценки не зависящую
 * от цвета игрока (должна быть с нулевой суммой, т.е. для текущего игрока возвращать максимум, а
 * для противника минимум)
 */
public class NegamaxAlfaBetaPruning extends ParallelSearch {

    private static final Comparator<Move> movesPriority =
            (m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel;

    public NegamaxAlfaBetaPruning(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public double run(boolean isMyMove, int depth) throws ChessError {
        return negamax(
                isMyMove, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth);
    }

    /**
     * @param isMyMove true, если это максимизирующий игрок, false - минимизирующий
     * @param alfa лучшая оценка из гарантированных для текущего игрока
     * @param beta лучшая оценка из гарантированных для противника
     * @return лучшая оценка из гарантированных для текущего игрока
     */
    public double negamax(boolean isMyMove, double alfa, double beta, int depth) throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves)) return getEvaluation(allMoves, isMyMove, depth);

        double optEstimation = EvaluationFunc.MIN_ESTIMATION;
        double estimation;

        allMoves.sort(movesPriority);

        for (Move move : allMoves) {
            gs.moveSystem.move(move);
            estimation = -negamax(!isMyMove, -beta, -alfa, depth - 1);
            gs.moveSystem.undoMove();

            if (estimation > optEstimation) optEstimation = estimation;
            if (optEstimation > alfa) alfa = optEstimation;
            if (beta <= alfa) return optEstimation;
        }

        return optEstimation;
    }
}