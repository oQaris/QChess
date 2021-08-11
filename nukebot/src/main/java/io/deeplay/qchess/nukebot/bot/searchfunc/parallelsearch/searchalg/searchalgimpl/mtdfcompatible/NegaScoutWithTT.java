package io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.features.TranspositionTable.TTEntry;
import java.util.Iterator;
import java.util.List;

public class NegaScoutWithTT extends MTDFSearch {

    public NegaScoutWithTT(
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
    public int alfaBetaWithTT(final int alfa, final int beta, final int depth) throws ChessError {
        gs.moveSystem.move(mainMove);
        final int est = -negascout(false, -beta, -alfa, depth);
        updater.updateResult(mainMove, est);
        gs.moveSystem.undoMove();
        return est;
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
            updater.updateResult(mainMove, est);
            gs.moveSystem.undoMove();
        } catch (ChessError ignore) {
        }
    }

    private int negascout(final boolean isMyMove, int alfa, int beta, final int depth)
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

        if (depth <= 0 || isTerminalNode(allMoves))
            return isMyMove
                    ? getEvaluation(allMoves, true, depth)
                    : -getEvaluation(allMoves, false, depth);

        if (entry == null) SearchImprovements.prioritySort(allMoves);

        final Iterator<Move> it = allMoves.iterator();
        Move move = it.next();
        // first move:
        gs.moveSystem.move(move);
        int estimation = -negascout(!isMyMove, -beta, -alfa, depth - 1);
        if (estimation > alfa) alfa = estimation;
        gs.moveSystem.undoMove();

        while (alfa < beta && it.hasNext()) {
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

        table.store(entry, allMoves, alfa, boardState, alfaOrigin, betaOrigin, depth);

        return alfa;
    }
}
