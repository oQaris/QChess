package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable.TTEntry;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.List;

/** Минимакс с транспозиционной таблицей */
public class MinimaxWithTT extends MTDFSearch {

    public MinimaxWithTT(
            final TranspositionTable table,
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(table, resultUpdater, mainMove, moveVersion, gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public int alfaBetaWithTT(final int alfa, final int beta, final int depth) throws ChessError {
        return minimax(false, alfa, beta, depth);
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
            resultUpdater.updateResult(mainMove, est, maxDepth, moveVersion);
            gs.moveSystem.undoMove();
        } catch (final ChessError ignore) {
        }
    }

    private int minimax(final boolean isMyMove, int alfa, int beta, final int depth)
            throws ChessError {
        final BoardState boardState = gs.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);
        if (entry != null && entry.depth >= depth) {
            if (entry.lowerBound >= beta) return entry.lowerBound;
            if (entry.upperBound <= alfa) return entry.upperBound;
            if (entry.lowerBound > alfa) alfa = entry.lowerBound;
            if (entry.upperBound < beta) beta = entry.upperBound;
        }
        final int alfaOrigin = alfa;
        final int betaOrigin = beta;

        final List<Move> allMoves;
        if (entry != null) allMoves = entry.allMoves;
        else allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        if (depth <= 0 || isTerminalNode(allMoves)) return getEvaluation(allMoves, isMyMove, depth);

        if (entry == null) SearchImprovements.prioritySort(allMoves);

        int optEstimation;

        if (isMyMove) {
            optEstimation = EvaluationFunc.MIN_ESTIMATION;
            int a = alfa;
            for (final Move move : allMoves) {
                if (optEstimation >= beta) break;
                gs.moveSystem.move(move);
                final int est = minimax(false, a, beta, depth - 1);
                gs.moveSystem.undoMove();
                if (est > optEstimation) optEstimation = est;
                if (optEstimation > a) a = optEstimation;
            }
        } else {
            optEstimation = EvaluationFunc.MAX_ESTIMATION;
            int b = beta;
            for (final Move move : allMoves) {
                if (optEstimation <= alfa) break;
                gs.moveSystem.move(move);
                final int est = minimax(true, alfa, b, depth - 1);
                gs.moveSystem.undoMove();
                if (est < optEstimation) optEstimation = est;
                if (optEstimation < b) b = optEstimation;
            }
        }

        table.store(allMoves, optEstimation, boardState, alfaOrigin, betaOrigin, depth);

        return optEstimation;
    }
}
