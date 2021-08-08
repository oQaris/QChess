package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.SearchImprovements;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTableWithFlag.TTEntry;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTableWithFlag.TTEntry.TTEntryFlag;
import java.util.Iterator;
import java.util.List;

public class PVSNullMoveWithTT extends NullMoveWithTT {

    public PVSNullMoveWithTT(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public int alfaBetaWithMemory(boolean isMyMove, int alfa, int beta, int depth)
            throws ChessError {
        return isMyMove
                ? pvs(true, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth)
                : -pvs(false, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth);
    }

    @Override
    public int run(int depth) throws ChessError {
        return -pvs(false, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth);
    }

    public int pvs(boolean isMyMove, int alfa, int beta, int depth) throws ChessError {
        int alfaOrigin = alfa;

        BoardState boardState = gs.history.getLastBoardState();
        TTEntry entry = table.find(boardState);
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TTEntryFlag.EXACT) return entry.estimation;
            if (entry.flag == TTEntryFlag.UPPERBOUND) {
                if (entry.estimation < beta) beta = entry.estimation;
            } else if (entry.estimation > alfa) alfa = entry.estimation;

            if (beta <= alfa) return entry.estimation;
        }

        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        if (depth <= 0 || isTerminalNode(allMoves))
            return isMyMove
                    ? getEvaluation(allMoves, true, depth)
                    : -getEvaluation(allMoves, false, depth);

        SearchImprovements.prioritySort(allMoves);

        Iterator<Move> it = allMoves.iterator();
        Move move = it.next();
        int estimation;

        boolean isAllowNullMove = isAllowNullMove(isMyMove ? myColor : enemyColor);
        if (isAllowNullMove) {
            isPrevNullMove = true;
            // TODO: слишком медленно
            List<Move> enemyMoves =
                    gs.board.getAllPreparedMoves(gs, isMyMove ? enemyColor : myColor);
            SearchImprovements.prioritySort(enemyMoves);
            Move nullMove = enemyMoves.get(0);
            // null-move:
            gs.moveSystem.move(nullMove);
            estimation = -pvs(isMyMove, -beta, -beta + 1, depth - DEPTH_REDUCTION - 1);
            gs.moveSystem.undoMove();
            if (estimation >= beta) return beta;
        } else isPrevNullMove = false;

        // first move:
        gs.moveSystem.move(move);
        estimation = -pvs(!isMyMove, -beta, -alfa, depth - 1);
        if (estimation > alfa) alfa = estimation;
        gs.moveSystem.undoMove();

        while (alfa < beta && it.hasNext()) {
            move = it.next();
            gs.moveSystem.move(move);
            // null-window search:
            estimation = -pvs(!isMyMove, -alfa - 1, -alfa, depth - 1);
            if (alfa < estimation && estimation < beta)
                estimation = -pvs(!isMyMove, -beta, -alfa, depth - 1);
            gs.moveSystem.undoMove();
            if (estimation > alfa) alfa = estimation;
        }

        table.store(entry, alfa, boardState, alfaOrigin, beta, depth);

        return alfa;
    }
}
