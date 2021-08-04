package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import java.util.Comparator;
import java.util.List;

public class NegaScoutAlfaBetaPruning extends ParallelSearch {

    private static final Comparator<Move> movesPriority =
            (m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel;

    public NegaScoutAlfaBetaPruning(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public double run(boolean isMyMove, int depth) throws ChessError {
        return negascout(
                isMyMove, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth);
    }

    private double negascout(boolean isMyMove, double alfa, double beta, int depth)
            throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves)) return getEvaluation(allMoves, isMyMove, depth);

        double score = EvaluationFunc.MIN_ESTIMATION;
        double n = beta;

        for (Move move : allMoves) {
            gs.moveSystem.move(move);
            double cur = -negascout(!isMyMove, -n, -alfa, depth - 1);
            if (cur > score) {
                if (n == beta || depth <= 2) {
                    score = cur;
                } else {
                    score = -negascout(!isMyMove, -beta, -cur, depth - 1);
                }
            }
            if (score > alfa) alfa = score;
            gs.moveSystem.undoMove();
            if (beta <= alfa) return alfa;
            n = alfa + 1;
        }
        return score;
    }

    private double pvs(boolean isMyMove, double alfa, double beta, int depth) throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves)) return getEvaluation(allMoves, isMyMove, depth);

        double estimation;
        double a = alfa;
        double b = beta;
        int i = 1;
        for (Move move : allMoves) {
            gs.moveSystem.move(move);
            estimation = -pvs(!isMyMove, -b, -a, depth - 1);
            if (estimation > a && estimation < beta && i > 1 && depth < maxDepth - 1)
                a = -pvs(!isMyMove, -beta, -estimation, depth - 1);
            if (estimation > a) a = estimation;
            gs.moveSystem.undoMove();
            if (a >= beta) return a;
            b = a + 1;
            ++i;
        }

        return a;
    }
}
