package io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

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

public class PVSVerifiedNullMoveWithTT extends NullMoveMTDFCompatible {

    public PVSVerifiedNullMoveWithTT(
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
        final int est = -pvs(false, -beta, -alfa, maxDepth, true);
        updater.updateResult(mainMove, est);
        gs.moveSystem.undoMove();
        return est;
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
                            maxDepth,
                            true);
            updater.updateResult(mainMove, est);
            gs.moveSystem.undoMove();
        } catch (final ChessError ignore) {
        }
    }

    private int pvs(final boolean isMyMove, int alfa, int beta, int depth, boolean verify)
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

        if (depth <= 0 || isTerminalNode(allMoves)) return quiesce(isMyMove, alfa, beta, depth);

        if (entry == null) SearchImprovements.prioritySort(allMoves);

        Iterator<Move> it = allMoves.iterator();
        Move move = it.next();
        int estimation;

        final boolean isAllowNullMove =
                isAllowNullMove(isMyMove ? myColor : enemyColor) && (!verify || depth > 1);
        boolean failHigh = false;

        if (isAllowNullMove) {
            isPrevNullMove = true;
            // TODO: слишком медленно
            final List<Move> enemyMoves =
                    gs.board.getAllPreparedMoves(gs, isMyMove ? enemyColor : myColor);
            SearchImprovements.prioritySort(enemyMoves);
            final Move nullMove = enemyMoves.get(0);
            // null-move:
            gs.moveSystem.move(nullMove, true, false);
            estimation = -pvs(isMyMove, -beta, -beta + 1, depth - DEPTH_REDUCTION - 1, verify);
            gs.moveSystem.undoMove();
            if (estimation >= beta) {
                if (verify) {
                    --depth;
                    verify = false;
                    failHigh = true;
                } else return beta;
            }
        } else isPrevNullMove = false;

        // for first move:
        gs.moveSystem.move(move);
        // Если будет обнаружена позиция Цугцванга, повторить поиск с начальной глубиной:
        do {
            // first move:
            estimation = -pvs(!isMyMove, -beta, -alfa, depth - 1, verify);
            if (estimation > alfa) alfa = estimation;
            gs.moveSystem.undoMove();

            while (alfa < beta && it.hasNext()) {
                move = it.next();
                gs.moveSystem.move(move);
                // null-window search:
                estimation = -pvs(!isMyMove, -alfa - 1, -alfa, depth - 1, verify);
                if (alfa < estimation && estimation < beta)
                    estimation = -pvs(!isMyMove, -beta, -alfa, depth - 1, verify);
                gs.moveSystem.undoMove();
                if (estimation > alfa) alfa = estimation;
            }

            if (failHigh && alfa < beta) {
                ++depth;
                failHigh = false;
                verify = true;
                // for first move:
                it = allMoves.iterator();
                move = it.next();
                gs.moveSystem.move(move);
            } else break;
        } while (true);

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
