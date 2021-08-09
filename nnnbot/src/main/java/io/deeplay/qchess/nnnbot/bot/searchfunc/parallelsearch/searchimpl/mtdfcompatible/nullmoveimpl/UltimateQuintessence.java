package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchimpl.mtdfcompatible.nullmoveimpl;

import static io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc.QUARTER_PAWN_COST;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.SearchImprovements;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTableWithFlag;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTableWithFlag.TTEntry;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.tt.TranspositionTableWithFlag.TTEntry.TTEntryFlag;
import java.util.Iterator;
import java.util.List;

/** Лучший из лучших */
public class UltimateQuintessence extends NullMove {

    private static final int MULTI_CUT_REDUCTION = 2;

    private final TranspositionTableWithFlag table = new TranspositionTableWithFlag();

    public UltimateQuintessence(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public int alfaBetaWithMemory(boolean isMyMove, int alfa, int beta, int depth)
            throws ChessError {
        return isMyMove
                ? uq(
                        true,
                        EvaluationFunc.MIN_ESTIMATION,
                        EvaluationFunc.MAX_ESTIMATION,
                        depth,
                        true)
                : -uq(
                        false,
                        EvaluationFunc.MIN_ESTIMATION,
                        EvaluationFunc.MAX_ESTIMATION,
                        depth,
                        true);
    }

    @Override
    public int run(int depth) throws ChessError {
        return -uq(
                false, EvaluationFunc.MIN_ESTIMATION, EvaluationFunc.MAX_ESTIMATION, depth, true);
    }

    public int uq(final boolean isMyMove, int alfa, int beta, int depth, boolean verify)
            throws ChessError {

        // --------------- Поиск в ТТ --------------- //

        final int alfaOrigin = alfa;
        final BoardState boardState = gs.history.getLastBoardState();
        TTEntry entry = table.find(boardState);
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TTEntryFlag.EXACT) return entry.estimation;
            if (entry.flag == TTEntryFlag.UPPERBOUND) {
                if (entry.estimation < beta) beta = entry.estimation;
            } else if (entry.estimation > alfa) alfa = entry.estimation;

            if (beta <= alfa) return entry.estimation;
        }

        // --------------- Получение всех ходов из ТТ или создание новых --------------- //

        final List<Move> allMoves;
        if (entry != null) allMoves = entry.allMoves;
        else allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        // --------------- Условие выхода из рекурсии --------------- //

        if (depth <= 0 || isTerminalNode(allMoves))
            // return quiesce(isMyMove, alfa, beta, depth, false);
            return isMyMove
                    ? getEvaluation(allMoves, true, depth)
                    : -getEvaluation(allMoves, false, depth);

        // --------------- Verified Null-Move --------------- //

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
            // TODO: try Aspiration Window
            // final int est = -uq(isMyMove, -beta, -beta + 1, depth - DEPTH_REDUCTION - 1, verify);

            // aspiration window:
            int est =
                    -uq(
                            isMyMove,
                            -beta - QUARTER_PAWN_COST,
                            -beta + QUARTER_PAWN_COST,
                            depth - DEPTH_REDUCTION - 1,
                            verify);

            gs.moveSystem.undoMove();

            if (est >= beta) {
                if (verify) {
                    --depth;
                    verify = false;
                    failHigh = true;
                } else return beta;
            }
        } else isPrevNullMove = false;

        // --------------- Сортировка по приоритетам --------------- //

        // TODO: сделать эвристику History
        if (entry == null) SearchImprovements.prioritySort(allMoves);

        // --------------- PVS с расширенным окном --------------- //

        boolean doResearch;
        do { // если будет обнаружена позиция Цугцванга, повторить поиск с начальной глубиной:
            doResearch = false;

            Iterator<Move> it = allMoves.iterator();
            Move move = it.next();

            // first move:
            gs.moveSystem.move(move);
            final int firstEst = -uq(!isMyMove, -beta, -alfa, depth - 1, verify);
            if (firstEst > alfa) alfa = firstEst;
            gs.moveSystem.undoMove();

            while (alfa < beta && it.hasNext()) {
                move = it.next();
                gs.moveSystem.move(move);

                // null-window search:
                int est = -uq(!isMyMove, -alfa - 1, -alfa, depth - 1, verify);
                if (alfa < est && est < beta) est = -uq(!isMyMove, -beta, -alfa, depth - 1, verify);

                // aspiration window:
                /*int alfaBound = -alfa - QUARTER_PAWN_COST;
                if (alfaBound <= alfa) alfaBound = -alfa - 1;
                int est = -uq(!isMyMove, alfaBound, -alfa + QUARTER_PAWN_COST, depth - 1, verify);
                if (alfa < est && est < beta) est = -uq(!isMyMove, -beta, -alfa, depth - 1, verify);*/

                gs.moveSystem.undoMove();
                if (est > alfa) alfa = est;
            }

            if (failHigh && alfa < beta) {
                ++depth;
                failHigh = false;
                verify = true;
                doResearch = true;
            }
        } while (doResearch);

        // --------------- Кеширование результата в ТТ --------------- //

        table.store(entry, allMoves, alfa, boardState, alfaOrigin, beta, depth);

        return alfa;
    }

    /**
     * Симулирует все атакующие ходы и считает оценку доски
     *
     * @return лучшая оценка доски
     */
    private int quiesce( // TODO: вынести в отдельный класс
            final boolean isMyMove, int alfa, int beta, final int depth, final boolean theLast)
            throws ChessError {

        // --------------- Поиск в ТТ --------------- //

        final int alfaOrigin = alfa;
        final BoardState boardState = gs.history.getLastBoardState();
        TTEntry entry = table.find(boardState);
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TTEntryFlag.EXACT) return entry.estimation;
            if (entry.flag == TTEntryFlag.UPPERBOUND) {
                if (entry.estimation < beta) beta = entry.estimation;
            } else if (entry.estimation > alfa) alfa = entry.estimation;

            if (beta <= alfa) return entry.estimation;
        }

        // --------------- Получение всех ходов из ТТ или создание новых --------------- //

        final List<Move> allMoves;
        if (entry != null) allMoves = entry.allMoves;
        else allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        final int standPat =
                isMyMove
                        ? getEvaluation(allMoves, true, depth)
                        : -getEvaluation(allMoves, false, depth);

        // --------------- Условие выхода из рекурсии --------------- //

        if (standPat >= beta) return beta;
        if (alfa < standPat) alfa = standPat;
        if (theLast || isTerminalNode(allMoves)) return alfa;

        // --------------- Проведение взятий до потери пульса --------------- //

        final Iterator<Move> attackMoves =
                allMoves.stream()
                        .filter(
                                move ->
                                        switch (move.getMoveType()) {
                                            case ATTACK, EN_PASSANT, TURN_INTO_ATTACK -> true;
                                            default -> false;
                                        })
                        .sorted(SearchImprovements.movesPriority) // TODO: заменить
                        .iterator();

        if (!attackMoves.hasNext()) { // попытка избавиться от Horizon effect
            /*for (final Move move : allMoves) {
                gs.moveSystem.move(move);
                final int score = -quiesce(!isMyMove, -beta, -alfa, depth - 1, true);
                gs.moveSystem.undoMove();

                if (score >= beta) {
                    alfa = beta;
                    break;
                }
                if (score > alfa) alfa = score;
            }*/
        } else { // проведение взятий
            do {
                gs.moveSystem.move(attackMoves.next());
                final int score = -quiesce(!isMyMove, -beta, -alfa, depth - 1, false);
                gs.moveSystem.undoMove();

                if (score >= beta) {
                    alfa = beta;
                    break;
                }
                if (score > alfa) alfa = score;
            } while (attackMoves.hasNext());
        }

        // --------------- Кеширование результата в ТТ --------------- //

        table.store(entry, allMoves, alfa, boardState, alfaOrigin, beta, depth);

        return alfa;
    }
}
