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

public class PVSVerifiedNullMoveWithTT extends NullMoveWithTT {

    public PVSVerifiedNullMoveWithTT(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public int alfaBetaWithMemory(boolean isMyMove, int alfa, int beta, int depth)
            throws ChessError {
        return isMyMove
                ? pvs(
                        true,
                        EvaluationFunc.MIN_ESTIMATION,
                        EvaluationFunc.MAX_ESTIMATION,
                        depth,
                        true)
                : -pvs(
                        false,
                        EvaluationFunc.MIN_ESTIMATION,
                        EvaluationFunc.MAX_ESTIMATION,
                        depth,
                        true);
    }

    @Override
    public int run(int depth) throws ChessError {
        return -pvs(
                false, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth, true);
    }

    public int pvs(boolean isMyMove, int alfa, int beta, int depth, boolean verify)
            throws ChessError {
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
                    ? quiesce(true, alfa, beta, depth)
                    : -quiesce(false, -beta, -alfa, depth);

        SearchImprovements.prioritySort(allMoves);

        Iterator<Move> it = allMoves.iterator();
        Move move = it.next();
        int estimation;

        boolean isAllowNullMove =
                isAllowNullMove(isMyMove ? myColor : enemyColor) && (!verify || depth > 1);
        boolean failHigh = false;

        if (isAllowNullMove) {
            isPrevNullMove = true;
            // TODO: слишком медленно
            List<Move> enemyMoves =
                    gs.board.getAllPreparedMoves(gs, isMyMove ? enemyColor : myColor);
            SearchImprovements.prioritySort(enemyMoves);
            Move nullMove = enemyMoves.get(0);
            // null-move:
            gs.moveSystem.move(nullMove);
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

        table.store(entry, alfa, boardState, alfaOrigin, beta, depth);

        return alfa;
    }

    /**
     * Симулирует все атакующие ходы и считает оценку доски
     *
     * @return лучшая оценка доски
     */
    private int quiesce(boolean isMyMove, int alfa, int beta, int depth) throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        int standPat = getEvaluation(allMoves, isMyMove, depth);
        if (standPat >= beta) return beta;
        if (alfa < standPat) alfa = standPat;

        if (isTerminalNode(allMoves)) return alfa;

        Iterator<Move> attackMoves =
                allMoves.parallelStream()
                        .filter(
                                move ->
                                        switch (move.getMoveType()) {
                                            case ATTACK, EN_PASSANT, TURN_INTO_ATTACK -> true;
                                            default -> false;
                                        })
                        .iterator();

        while (attackMoves.hasNext()) {
            gs.moveSystem.move(attackMoves.next());
            int score = -quiesce(!isMyMove, -beta, -alfa, depth - 1);
            gs.moveSystem.undoMove();

            if (score >= beta) return beta;
            if (score > alfa) alfa = score;
        }

        return alfa;
    }
}
