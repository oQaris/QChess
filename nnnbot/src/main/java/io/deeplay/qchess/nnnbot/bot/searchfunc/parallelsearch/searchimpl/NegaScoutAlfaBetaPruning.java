package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.SearchImprovements;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.ParallelSearch;
import java.util.Iterator;
import java.util.List;

/**
 * Реализует алгоритм поиска негаскаутом с нулевым окном, поэтому желательно использовать функцию
 * оценки не зависящую от цвета игрока (должна быть с нулевой суммой, т.е. для текущего игрока
 * возвращать максимум, а для противника минимум)
 */
public class NegaScoutAlfaBetaPruning extends ParallelSearch {

    public NegaScoutAlfaBetaPruning(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public int run(int depth) throws ChessError {
        return -negascout(
                false, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth);
    }

    private int negascout(boolean isMyMove, int alfa, int beta, int depth) throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves))
            return isMyMove
                    ? getEvaluation(allMoves, true, depth)
                    : -getEvaluation(allMoves, false, depth);

        SearchImprovements.prioritySort(allMoves);

        Iterator<Move> it = allMoves.iterator();
        Move move = it.next();
        // first move:
        gs.moveSystem.move(move);
        int estimation = -negascout(!isMyMove, -beta, -alfa, depth - 1);
        if (estimation > alfa) alfa = estimation;
        gs.moveSystem.undoMove();

        while (beta > alfa && it.hasNext()) {
            move = it.next();
            gs.moveSystem.move(move);
            // null-window search:
            estimation = -negascout(!isMyMove, -alfa - 1, -alfa, depth - 1);
            if (alfa < estimation && estimation < beta && depth > 1) {
                int est = -negascout(!isMyMove, -beta, -estimation, depth - 1);
                if (est > estimation) estimation = est;
            }
            gs.moveSystem.undoMove();
            if (estimation > alfa) alfa = estimation;
        }

        return alfa;
    }
}
