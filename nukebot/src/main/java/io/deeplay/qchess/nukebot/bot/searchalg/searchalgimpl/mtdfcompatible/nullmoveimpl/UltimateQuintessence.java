package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.nullmoveimpl;

import static io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc.DOUBLE_QUEEN_MINUS_PAWN_COST;
import static io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc.QUARTER_PAWN_COST;
import static io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc.MG_QUEEN_COST;

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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/** Лучший из лучших */
public class UltimateQuintessence extends NullMoveMTDFCompatible {

    private static final int MY_SIDE = 1;
    private static final int ENEMY_SIDE = 0;

    /** [Сторона, чей ход][откуда][куда] */
    private final int[][][] moveHistory = new int[2][64][64];
    /** [Сторона, чей ход][откуда][куда] */
    private final int[][][] butterfly = new int[2][64][64];

    {
        for (int i = 0; i < 2; ++i)
            for (int y = 0; y < 64; ++y)
                for (int x = 0; x < 64; ++x) {
                    moveHistory[i][y][x] = 1;
                    butterfly[i][y][x] = 1;
                }
    }

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

        final boolean isAllowNullMove =
                isAllowNullMove(isMyMove ? myColor : enemyColor, isPrevNullMove)
                        && (!verify || depth > 1);
        boolean failHigh = false;

        if (isAllowNullMove) {
            isPrevNullMove = true;
            // TODO: слишком медленно
            final List<Move> enemyMoves =
                    gs.board.getAllPreparedMoves(gs, isMyMove ? enemyColor : myColor);
            SearchImprovements.allSort(
                    gs.board, enemyMoves, moveHistory, butterfly, isMyMove ? ENEMY_SIDE : MY_SIDE);
            final Move nullMove = enemyMoves.get(0);

            // null-move:
            gs.moveSystem.move(nullMove);

            // aspiration window:
            final int est =
                    -uq(
                            isMyMove,
                            -beta - QUARTER_PAWN_COST,
                            -beta + QUARTER_PAWN_COST,
                            depth - DEPTH_REDUCTION - 1,
                            verify,
                            true);

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
            SearchImprovements.allSort(
                    gs.board, allMoves, moveHistory, butterfly, isMyMove ? MY_SIDE : ENEMY_SIDE);

        // --------------- PVS --------------- //

        boolean doResearch;
        do { // если будет обнаружена позиция Цугцванга, повторить поиск с начальной глубиной:
            doResearch = false;

            if (resultUpdater.isInvalidMoveVersion(moveVersion))
                return EvaluationFunc.MIN_ESTIMATION;

            final ListIterator<Move> it = allMoves.listIterator();
            Move move = it.next();

            // first move:
            gs.moveSystem.move(move);
            int est = -uq(!isMyMove, -beta, -alfa, depth - 1, verify, isPrevNullMove);
            if (est > alfa) alfa = est;
            gs.moveSystem.undoMove();

            if (resultUpdater.isInvalidMoveVersion(moveVersion))
                return EvaluationFunc.MIN_ESTIMATION;

            while (it.hasNext()) {

                // --------------- Relative History Heuristic --------------- //

                if (beta <= est) {
                    // Эвристика истории:
                    if (isNotCapture(move)) {
                        final int side2move = isMyMove ? MY_SIDE : ENEMY_SIDE;
                        moveHistory[side2move][move.getFrom().toSquare()][
                                        move.getTo().toSquare()] +=
                                1 << depth;
                    }
                    alfa = beta;
                    break;
                } else {
                    // Эвристика бабочки:
                    if (isNotCapture(move)) {
                        final int side2move = isMyMove ? MY_SIDE : ENEMY_SIDE;
                        butterfly[side2move][move.getFrom().toSquare()][move.getTo().toSquare()] +=
                                1 << depth;
                    }
                }

                // --------------- PVS --------------- //

                move = it.next();
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

        table.store(allMoves, null, alfa, boardState, alfaOrigin, betaOrigin, depth);

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
            if (entry.lowerBound >= beta) return entry.lowerBound;
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

        // --------------- Условие выхода из рекурсии --------------- //

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        {
            int standPat =
                    getEvaluation(
                            allMoves != null ? allMoves : probablyAttackMoves,
                            allMoves != null || !areAttackMovesOrElseAll,
                            isMyMove,
                            depth);
            if (!isMyMove) standPat = -standPat;

            if (standPat >= beta) return beta;
            if (alfa < standPat) alfa = standPat;
        }

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;
        if (isTerminalNode(probablyAttackMoves)) return alfa;

        // --------------- Проведение взятий до потери пульса --------------- //

        if (!areAttackMovesOrElseAll) {
            SearchImprovements.allSort(
                    gs.board,
                    probablyAttackMoves,
                    moveHistory,
                    butterfly,
                    isMyMove ? MY_SIDE : ENEMY_SIDE);
            if (allMoves == null) allMoves = probablyAttackMoves;
        }

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        final boolean isNotEndgame = isNotEndgame();

        for (final Move move : probablyAttackMoves) {
            if (!areAttackMovesOrElseAll) {
                if (isNotCapture(move)) continue;
                attackMoves.add(move);
            }

            // --------------- Delta Pruning --------------- //

            int delta = MG_QUEEN_COST;
            if (isNotEndgame
                    && gs.board.getFigureUgly(move.getFrom()).figureType == FigureType.PAWN)
                delta = DOUBLE_QUEEN_MINUS_PAWN_COST;

            gs.moveSystem.move(move);
            final int score = -quiesce(!isMyMove, -beta, -alfa, depth - 1);
            gs.moveSystem.undoMove();

            if (resultUpdater.isInvalidMoveVersion(moveVersion))
                return EvaluationFunc.MIN_ESTIMATION;

            if (isNotEndgame && score < alfa - delta) return alfa;
            if (score >= beta) {
                alfa = beta;
                break;
            }
            if (score > alfa) alfa = score;
        }

        // --------------- Кеширование результата в ТТ --------------- //

        table.store(allMoves, attackMoves, alfa, boardState, alfaOrigin, betaOrigin, depth);

        return alfa;
    }

    /** @return true, если сейчас скорее всего не эндшпиль */
    public boolean isNotEndgame() {
        int count = 0;
        final int[] board = gs.board.fastSnapshotReference();
        for (int sq = 0; sq < 64; ++sq)
            if (board[sq] != FigureType.EMPTY_TYPE) {
                ++count;
                if (count > 10) return true;
            }
        return false;
    }
}
