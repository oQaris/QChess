package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.SearchImprovements;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTable;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTable.TTEntry;
import java.util.Iterator;
import java.util.List;

/** Минимакс с транспозиционной таблицей и функцией MTDF в качестве проверки null-window */
public class MinimaxMTDF extends MTDFSearch {

    private final TranspositionTable table = new TranspositionTable(100000);

    public MinimaxMTDF(GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public int run(int depth) throws ChessError {
        return alfaBetaWithMemory(
                false, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth);
    }

    @Override
    public int alfaBetaWithMemory(boolean isMyMove, int alfa, int beta, int depth)
            throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        BoardState boardState = gs.history.getLastBoardState();
        TTEntry entry = table.find(boardState);
        if (entry != null) {
            if (entry.lowerBound >= beta) return entry.lowerBound;
            if (entry.upperBound <= alfa) return entry.upperBound;
            if (entry.lowerBound > alfa) alfa = entry.lowerBound;
            if (entry.upperBound < beta) beta = entry.upperBound;
        }

        if (depth <= 0 || isTerminalNode(allMoves)) return getEvaluation(allMoves, isMyMove, depth);

        SearchImprovements.prioritySort(allMoves);

        int optEstimation;

        if (isMyMove) {
            int a = alfa;
            Iterator<Move> it = allMoves.iterator();
            Move move = it.next();
            // first move: TODO: несколько первых ходов
            gs.moveSystem.move(move);
            optEstimation = alfaBetaWithMemory(false, alfa, beta, depth - 1);
            gs.moveSystem.undoMove();
            while (optEstimation < beta && it.hasNext()) {
                move = it.next();
                gs.moveSystem.move(move);
                // null-window search:
                // int est = alfaBetaWithMemory(false, beta - 1, beta, depth - 1);
                int est = MTDFStart(false, optEstimation, depth - 1);
                // std search:
                if (a < est && est < beta) est = alfaBetaWithMemory(false, a, beta, depth - 1);
                gs.moveSystem.undoMove();
                if (est > optEstimation) optEstimation = est;
                if (optEstimation > a) a = optEstimation;
            }
        } else {
            int b = beta;
            Iterator<Move> it = allMoves.iterator();
            Move move = it.next();
            // first move: TODO: несколько первых ходов
            gs.moveSystem.move(move);
            optEstimation = alfaBetaWithMemory(true, alfa, beta, depth - 1);
            gs.moveSystem.undoMove();
            while (optEstimation > alfa && it.hasNext()) {
                move = it.next();
                gs.moveSystem.move(move);
                // null-window search:
                // int est = alfaBetaWithMemory(true, b - 1, b, depth - 1);
                int est = MTDFStart(true, optEstimation, depth - 1);
                // std search:
                if (alfa < est && est < b) est = alfaBetaWithMemory(true, alfa, b, depth - 1);
                gs.moveSystem.undoMove();
                if (est < optEstimation) optEstimation = est;
                if (optEstimation < b) b = optEstimation;
            }
        }

        if (entry == null) {
            entry = new TTEntry();
            table.store(boardState, entry);
        }
        if (optEstimation <= alfa) entry.upperBound = optEstimation;
        if (optEstimation >= beta) entry.lowerBound = optEstimation;
        if (alfa < optEstimation && optEstimation < beta)
            entry.lowerBound = entry.upperBound = optEstimation;

        return optEstimation;
    }
}
