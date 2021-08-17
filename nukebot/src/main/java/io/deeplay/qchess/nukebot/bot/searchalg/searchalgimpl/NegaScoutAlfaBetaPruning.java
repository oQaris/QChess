package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.Iterator;
import java.util.List;

/**
 * Реализует алгоритм поиска негаскаутом с нулевым окном, поэтому желательно использовать функцию
 * оценки не зависящую от цвета игрока (должна быть с нулевой суммой, т.е. для текущего игрока
 * возвращать максимум, а для противника минимум)
 */
public class NegaScoutAlfaBetaPruning extends SearchAlgorithm {

    public NegaScoutAlfaBetaPruning(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(resultUpdater, mainMove, moveVersion, gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public void run() {
        try {
            gs.moveSystem.move(mainMove);
            final int est =
                    -negascout(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth);
            resultUpdater.updateResult(mainMove, est, maxDepth, moveVersion);
            gs.moveSystem.undoMove();
        } catch (final ChessError ignore) {
        }
    }

    private int negascout(final boolean isMyMove, int alfa, final int beta, final int depth)
            throws ChessError {
        final List<Move> allMoves =
                gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);
        if (depth <= 0 || isTerminalNode(allMoves))
            return isMyMove
                    ? getEvaluation(allMoves, true, depth)
                    : -getEvaluation(allMoves, false, depth);

        SearchImprovements.prioritySort(allMoves);

        final Iterator<Move> it = allMoves.iterator();
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
                final int est = -negascout(!isMyMove, -beta, -estimation, depth - 1);
                if (est > estimation) estimation = est;
            }
            gs.moveSystem.undoMove();
            if (estimation > alfa) alfa = estimation;
        }

        return alfa;
    }
}
