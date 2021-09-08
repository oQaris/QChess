package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.CommonEvaluationConstructor;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgErrorCode;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgException;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaVerifiedNullMoveAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.Iterator;
import java.util.List;

public class PVSVerifiedNullMove extends NegaVerifiedNullMoveAlfaBeta {

    public PVSVerifiedNullMove(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color myColor,
            final CommonEvaluationConstructor commonEvaluationConstructor,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(
                resultUpdater,
                mainMove,
                moveVersion,
                gs,
                myColor,
                commonEvaluationConstructor,
                evaluationFunc,
                maxDepth);
    }

    @Override
    public void run() {
        try {
            makeMove(mainMove);
            final int est =
                    -super.negaVNMSearch(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth,
                            true,
                            false);
            updateResult(est);
            undoMove();
        } catch (final ChessError e) {
            throw new SearchAlgException(SearchAlgErrorCode.SEARCH_ALG, e);
        }
    }

    @Override
    public int negaVNMSearch(
            final boolean isMyMove,
            int alfa,
            final int beta,
            int depth,
            boolean verify,
            boolean isPrevNullMove)
            throws ChessError {
        final List<Move> allMoves = getLegalMoves(isMyMove ? myColor : enemyColor);

        if (depth <= 0 || isTerminalNode(allMoves))
            return getEvaluation(allMoves, isMyMove, alfa, beta, depth);

        prioritySort(allMoves);

        final boolean isAllowNullMove =
                isAllowNullMove(isMyMove ? myColor : enemyColor, isPrevNullMove, verify, depth);
        boolean failHigh = false;

        if (isAllowNullMove) {
            isPrevNullMove = true;
            final List<Move> enemyMoves = getLegalMoves(isMyMove ? enemyColor : myColor);
            prioritySort(enemyMoves);
            final Move nullMove = enemyMoves.get(0);
            // null-move:
            makeMove(nullMove, true, false);
            final int estimation =
                    -super.negaVNMSearch(
                            isMyMove, -beta, -beta + 1, depth - DEPTH_REDUCTION - 1, verify, true);
            undoMove();
            if (estimation >= beta) {
                if (verify) {
                    --depth;
                    verify = false;
                    failHigh = true;
                } else return estimation;
            }
        } else isPrevNullMove = false;

        boolean doResearch;
        do { // если будет обнаружена позиция Цугцванга, повторить поиск с начальной глубиной:
            doResearch = false;

            final Iterator<Move> it = allMoves.iterator();
            Move move = it.next();

            // first move:
            makeMove(move);
            int estimation =
                    -super.negaVNMSearch(
                            !isMyMove, -beta, -alfa, depth - 1, verify, isPrevNullMove);
            if (estimation > alfa) alfa = estimation;
            undoMove();

            while (alfa < beta && it.hasNext()) {
                move = it.next();
                makeMove(move);
                // null-window search:
                estimation =
                        -super.negaVNMSearch(
                                !isMyMove, -alfa - 1, -alfa, depth - 1, verify, isPrevNullMove);
                if (alfa < estimation && estimation < beta)
                    estimation =
                            -super.negaVNMSearch(
                                    !isMyMove, -beta, -alfa, depth - 1, verify, isPrevNullMove);
                undoMove();
                if (estimation > alfa) alfa = estimation;
            }

            if (failHigh && alfa < beta) {
                ++depth;
                failHigh = false;
                verify = true;
                doResearch = true;
            }
        } while (doResearch);

        return alfa;
    }
}
