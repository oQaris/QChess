package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.TranspositionTable.TTEntry;
import java.util.Iterator;
import java.util.List;

public class PVSNullMoveWithTT extends NullMoveMTDFCompatible {

    public PVSNullMoveWithTT(
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
    public int alfaBetaWithTT(boolean isMyMove, int alfa, int beta, int depth) throws ChessError {
        return isMyMove
                ? pvs(true, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth)
                : -pvs(false, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth);
    }

    @Override
    public void run() {
        try {
            gs.moveSystem.move(mainMove);
            final int est =
                    -pvs(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth);
            updater.updateResult(mainMove, est);
            gs.moveSystem.undoMove();
        } catch (ChessError ignore) {
        }
    }

    public int pvs(boolean isMyMove, int alfa, int beta, int depth) throws ChessError {
        final BoardState boardState = gs.history.getLastBoardState();
        final TranspositionTable.TTEntry entry = table.find(boardState);
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

        if (depth <= 0 || isTerminalNode(allMoves)) return quiesce(isMyMove, alfa, beta, depth);

        if (entry == null) SearchImprovements.prioritySort(allMoves);

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
            gs.moveSystem.move(nullMove, true, false);
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

        table.store(entry, allMoves, alfa, boardState, alfaOrigin, betaOrigin, depth);

        return alfa;
    }

    /**
     * Симулирует все атакующие ходы и считает оценку доски
     *
     * @return лучшая оценка доски
     */
    private int quiesce(final boolean isMyMove, int alfa, final int beta, final int depth)
            throws ChessError {
        final BoardState boardState = gs.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);

        final List<Move> allMoves;
        if (entry != null) allMoves = entry.allMoves;
        else allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        final int standPat =
                isMyMove
                        ? getEvaluation(allMoves, true, depth)
                        : -getEvaluation(allMoves, false, depth);
        if (standPat >= beta) return beta;
        if (alfa < standPat) alfa = standPat;

        if (isTerminalNode(allMoves)) return alfa;

        final Iterator<Move> attackMoves =
                allMoves.stream()
                        .filter(
                                move ->
                                        switch (move.getMoveType()) {
                                            case ATTACK, EN_PASSANT, TURN_INTO_ATTACK -> true;
                                            default -> false;
                                        })
                        .iterator();

        while (attackMoves.hasNext()) {
            gs.moveSystem.move(attackMoves.next());
            final int score = -quiesce(!isMyMove, -beta, -alfa, depth - 1);
            gs.moveSystem.undoMove();

            if (score >= beta) return beta;
            if (score > alfa) alfa = score;
        }

        return alfa;
    }
}
