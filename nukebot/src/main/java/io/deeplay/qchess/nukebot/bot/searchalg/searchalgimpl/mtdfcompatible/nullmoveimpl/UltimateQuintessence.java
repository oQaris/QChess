package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable.TTEntry;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Лучший из лучших */
public class UltimateQuintessence extends NullMoveMTDFCompatible {

    private static final int LMR_REDUCE_ONE = 6;
    private static final int LMR_REDUCE_TWO = 32;
    private static final int LMR_REDUCE_THREE = 64;

    public UltimateQuintessence(
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
        return -uq(false, -beta, -alfa, depth, true, false);
    }

    @Override
    public void run() {
        try {
            gs.moveSystem.move(mainMove);
            final int est =
                    -uq(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth,
                            true,
                            false);
            resultUpdater.updateResult(mainMove, est, maxDepth, moveVersion);
            gs.moveSystem.undoMove();
        } catch (final ChessError ignore) {
        }
    }

    /** @return true, если разрешено сократить глубину для хода move */
    private boolean isAllowLMR(final Move move) {
        return isNotCapture(move)
                && gs.board.getFigureUgly(move.getFrom()).figureType != FigureType.PAWN;
    }

    public int uq(
            final boolean isMyMove,
            int alfa,
            int beta,
            int depth,
            boolean verify,
            boolean isPrevNullMove)
            throws ChessError {
        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        // --------------- Поиск в ТТ --------------- //

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

        // --------------- Получение всех ходов из ТТ или создание новых --------------- //

        final List<Move> allMoves;
        if (entry != null && entry.allMoves != null) allMoves = entry.allMoves;
        else allMoves = gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        // --------------- Условие выхода из рекурсии --------------- //

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        if (depth <= 0 || isTerminalNode(allMoves)) return quiesce(isMyMove, alfa, beta, depth);

        // --------------- Verified Null-Move --------------- //

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        final boolean isCheckToWhite =
                entry != null && entry.isCheckToWhite != 0
                        ? entry.isCheckToWhite == 1
                        : gs.endGameDetector.isCheck(Color.WHITE);
        final boolean isCheckToBlack =
                entry != null && entry.isCheckToBlack != 0
                        ? entry.isCheckToBlack == 1
                        : gs.endGameDetector.isCheck(Color.BLACK);
        final boolean isCheckToMe = myColor == Color.WHITE ? isCheckToWhite : isCheckToBlack;
        final boolean isCheckToEnemy = enemyColor == Color.WHITE ? isCheckToWhite : isCheckToBlack;
        final boolean isCheckToThisSide = isMyMove ? isCheckToMe : isCheckToEnemy;
        final boolean isCheckToOtherSide = isMyMove ? isCheckToEnemy : isCheckToMe;

        final boolean isAllowNullMove =
                isAllowNullMove(
                                isMyMove ? myColor : enemyColor,
                                isPrevNullMove,
                                isCheckToThisSide,
                                isCheckToOtherSide)
                        && (!verify || depth > 1);
        boolean failHigh = false;

        if (isAllowNullMove) {
            isPrevNullMove = true;

            final List<Move> enemyMoves =
                    gs.board.getAllPreparedMoves(gs, isMyMove ? enemyColor : myColor);
            SearchImprovements.allSorts(gs.board, enemyMoves);
            final Move nullMove = enemyMoves.get(0);

            // null-move:
            gs.moveSystem.move(nullMove);

            // null-window search:
            final int est =
                    -uq(isMyMove, -beta, -beta + 1, depth - DEPTH_REDUCTION - 1, verify, true);

            gs.moveSystem.undoMove();

            if (resultUpdater.isInvalidMoveVersion(moveVersion))
                return EvaluationFunc.MIN_ESTIMATION;

            if (est >= beta) {
                if (verify) {
                    --depth;
                    verify = false;
                    failHigh = true;
                } else return beta;
            }
        } else isPrevNullMove = false;

        // --------------- Сортировка по приоритетам --------------- //

        if (entry == null || entry.allMoves == null)
            SearchImprovements.allSorts(gs.board, allMoves);

        // --------------- PVS --------------- //

        final int initDepth = depth;
        int countNotFail = 0;

        boolean doResearch;
        do { // если будет обнаружена позиция Цугцванга, повторить поиск с начальной глубиной:
            doResearch = false;

            if (resultUpdater.isInvalidMoveVersion(moveVersion))
                return EvaluationFunc.MIN_ESTIMATION;

            final Iterator<Move> it = allMoves.iterator();
            Move move = it.next();

            // first move:
            gs.moveSystem.move(move);
            int est = -uq(!isMyMove, -beta, -alfa, depth - 1, verify, isPrevNullMove);
            if (est > alfa) alfa = est;
            gs.moveSystem.undoMove();

            if (resultUpdater.isInvalidMoveVersion(moveVersion))
                return EvaluationFunc.MIN_ESTIMATION;

            while (it.hasNext()) {

                if (beta <= est) {
                    alfa = beta;
                    break;
                }

                ++countNotFail;

                // --------------- PVS --------------- //

                move = it.next();

                final boolean isAllowLMR =
                        depth != maxDepth
                                && !isCheckToThisSide
                                && countNotFail >= LMR_REDUCE_ONE
                                && isAllowLMR(move);
                if (isAllowLMR) {
                    if (countNotFail >= LMR_REDUCE_THREE) depth = initDepth - 3;
                    else if (countNotFail >= LMR_REDUCE_TWO) depth = initDepth - 2;
                    else depth = initDepth - 1;
                }

                gs.moveSystem.move(move);

                // null-window search:
                est = -uq(!isMyMove, -alfa - 1, -alfa, depth - 1, verify, isPrevNullMove);
                if (alfa < est && est < beta)
                    est = -uq(!isMyMove, -beta, -alfa, depth - 1, verify, isPrevNullMove);

                gs.moveSystem.undoMove();
                if (est > alfa) alfa = est;

                if (resultUpdater.isInvalidMoveVersion(moveVersion))
                    return EvaluationFunc.MIN_ESTIMATION;
            }

            if (failHigh && alfa < beta) {
                ++depth;
                failHigh = false;
                verify = true;
                doResearch = true;
            }
        } while (doResearch);

        // --------------- Кеширование результата в ТТ --------------- //

        table.store(
                allMoves,
                null,
                isAllowNullMove ? 1 : 2,
                isCheckToWhite ? 1 : 2,
                isCheckToBlack ? 1 : 2,
                alfa,
                boardState,
                alfaOrigin,
                betaOrigin,
                depth);

        return alfa;
    }

    /**
     * Симулирует все атакующие ходы и считает оценку доски
     *
     * @return лучшая оценка доски
     */
    private int quiesce( // TODO: вынести в отдельный класс
            final boolean isMyMove, int alfa, int beta, final int depth) throws ChessError {
        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        // --------------- Поиск в ТТ --------------- //

        final BoardState boardState = gs.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);
        if (entry != null && entry.depth >= depth) {
            if (entry.lowerBound >= beta) return beta;
            if (entry.upperBound <= alfa) return entry.upperBound;
            if (entry.lowerBound > alfa) alfa = entry.lowerBound;
            if (entry.upperBound < beta) beta = entry.upperBound;
        }
        final int alfaOrigin = alfa;
        final int betaOrigin = beta;

        // --------------- Получение всех ходов из ТТ или создание новых --------------- //

        List<Move> allMoves = entry != null ? entry.allMoves : null;
        final boolean areAttackMovesOrElseAll = entry != null && entry.attackMoves != null;
        final List<Move> attackMoves =
                areAttackMovesOrElseAll ? entry.attackMoves : new LinkedList<>();
        final List<Move> probablyAttackMoves =
                areAttackMovesOrElseAll
                        ? attackMoves
                        : gs.board.getAllPreparedMoves(gs, isMyMove ? myColor : enemyColor);

        final boolean isCheckToWhite =
                entry != null && entry.isCheckToWhite != 0
                        ? entry.isCheckToWhite == 1
                        : gs.endGameDetector.isCheck(Color.WHITE);
        final boolean isCheckToBlack =
                entry != null && entry.isCheckToBlack != 0
                        ? entry.isCheckToBlack == 1
                        : gs.endGameDetector.isCheck(Color.BLACK);
        final boolean isCheckToMe = myColor == Color.WHITE ? isCheckToWhite : isCheckToBlack;
        final boolean isCheckToEnemy = enemyColor == Color.WHITE ? isCheckToWhite : isCheckToBlack;

        // --------------- Условие выхода из рекурсии --------------- //

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        {
            int standPat =
                    getEvaluation(
                            isCheckToMe,
                            isCheckToEnemy,
                            allMoves != null ? allMoves : probablyAttackMoves,
                            allMoves != null || !areAttackMovesOrElseAll,
                            isMyMove,
                            depth,
                            table);
            if (!isMyMove) standPat = -standPat;

            if (standPat >= beta) return beta;
            if (alfa < standPat) alfa = standPat;
        }

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;
        if (isTerminalNode(probablyAttackMoves)) return alfa;

        // --------------- Проведение взятий до потери пульса --------------- //

        if (!areAttackMovesOrElseAll) {
            SearchImprovements.allSorts(gs.board, probablyAttackMoves);
            if (allMoves == null) allMoves = probablyAttackMoves;
        }

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        for (final Move move : probablyAttackMoves) {
            if (!areAttackMovesOrElseAll) {
                if (isNotCapture(move)) continue;
                attackMoves.add(move);
            }

            gs.moveSystem.move(move);
            final int score = -quiesce(!isMyMove, -beta, -alfa, depth - 1);
            gs.moveSystem.undoMove();

            if (resultUpdater.isInvalidMoveVersion(moveVersion))
                return EvaluationFunc.MIN_ESTIMATION;

            if (score >= beta) {
                alfa = beta;
                break;
            }
            if (score > alfa) alfa = score;
        }

        // --------------- Кеширование результата в ТТ --------------- //

        table.store(
                allMoves,
                attackMoves,
                0,
                isCheckToWhite ? 1 : 2,
                isCheckToBlack ? 1 : 2,
                alfa,
                boardState,
                alfaOrigin,
                betaOrigin,
                depth);

        return alfa;
    }
}
