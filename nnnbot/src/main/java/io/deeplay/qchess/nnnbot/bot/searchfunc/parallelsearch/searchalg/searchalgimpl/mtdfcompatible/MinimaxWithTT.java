package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.tt.TranspositionTable;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.tt.TranspositionTable.TTEntry;
import java.util.List;

/** Минимакс с транспозиционной таблицей */
public class MinimaxWithTT extends MTDFSearch {

    public MinimaxWithTT(
            final TranspositionTable table,
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(table, updater, mainMove, gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public void run() {
        try {
            gs.moveSystem.move(mainMove);
            final int est =
                    alfaBetaWithTT(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth);
            updater.updateResult(mainMove, est);
            gs.moveSystem.undoMove();
        } catch (ChessError ignore) {
        }
    }

    @Override
    public int alfaBetaWithTT(boolean isMyMove, int alfa, int beta, int depth) throws ChessError {
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

        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        if (depth <= 0 || isTerminalNode(allMoves)) return getEvaluation(allMoves, isMyMove, depth);

        SearchImprovements.prioritySort(allMoves);

        int optEstimation;

        if (isMyMove) {
            optEstimation = EvaluationFunc.MIN_ESTIMATION;
            int a = alfa;
            for (Move move : allMoves) {
                if (optEstimation >= beta) break;
                gs.moveSystem.move(move);
                int est = alfaBetaWithTT(false, a, beta, depth - 1);
                // null-window search:
                // int est = alfaBetaWithMemory(false, beta - 1, beta, depth - 1);
                // std search:
                // if (a < est && est < beta) est = alfaBetaWithMemory(false, a, est, depth - 1);
                gs.moveSystem.undoMove();
                if (est > optEstimation) optEstimation = est;
                if (optEstimation > a) a = optEstimation;
            }
        } else {
            optEstimation = EvaluationFunc.MAX_ESTIMATION;
            int b = beta;
            for (Move move : allMoves) {
                if (optEstimation <= alfa) break;
                gs.moveSystem.move(move);
                int est = alfaBetaWithTT(true, alfa, b, depth - 1);
                // null-window search:
                // int est = alfaBetaWithMemory(true, b - 1, b, depth - 1);
                // std search:
                // if (alfa < est && est < b) est = alfaBetaWithMemory(true, alfa, est, depth - 1);
                gs.moveSystem.undoMove();
                if (est < optEstimation) optEstimation = est;
                if (optEstimation < b) b = optEstimation;
            }
        }

        table.store(entry, allMoves, optEstimation, boardState, alfaOrigin, betaOrigin, depth);

        return optEstimation;
    }
}
