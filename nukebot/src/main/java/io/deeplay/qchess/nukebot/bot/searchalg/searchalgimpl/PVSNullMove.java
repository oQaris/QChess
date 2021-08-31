package io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.CommonEvaluationConstructor;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgErrorCode;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgException;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaNullMoveAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.Iterator;
import java.util.List;

public class PVSNullMove extends NegaNullMoveAlfaBeta {

    public PVSNullMove(
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
                    -super.negaNullMoveSearch(
                            false,
                            EvaluationFunc.MIN_ESTIMATION,
                            EvaluationFunc.MAX_ESTIMATION,
                            maxDepth,
                            false);
            updateResult(est);
            undoMove();
        } catch (final ChessError e) {
            throw new SearchAlgException(SearchAlgErrorCode.SEARCH_ALG, e);
        }
    }

    @Override
    public int negaNullMoveSearch(
            final boolean isMyMove,
            int alfa,
            final int beta,
            final int depth,
            boolean isPrevNullMove)
            throws ChessError {
        final List<Move> allMoves = getLegalMoves(isMyMove ? myColor : enemyColor);

        if (depth <= 0 || isTerminalNode(allMoves))
            return getEvaluation(allMoves, isMyMove, alfa, beta, depth);

        prioritySort(allMoves);

        final boolean isAllowNullMove =
                isAllowNullMove(isMyMove ? myColor : enemyColor, isPrevNullMove);
        if (isAllowNullMove) {
            isPrevNullMove = true;
            final List<Move> enemyMoves = getLegalMoves(isMyMove ? enemyColor : myColor);
            prioritySort(enemyMoves);
            final Move nullMove = enemyMoves.get(0);
            // null-move:
            makeMove(nullMove, true, false);
            final int estimation =
                    -super.negaNullMoveSearch(
                            isMyMove, -beta, -beta + 1, depth - DEPTH_REDUCTION - 1, true);
            undoMove();
            if (estimation >= beta) return estimation;
        } else isPrevNullMove = false;

        final Iterator<Move> it = allMoves.iterator();
        Move move = it.next();

        // first move:
        makeMove(move);
        int estimation =
                -super.negaNullMoveSearch(!isMyMove, -beta, -alfa, depth - 1, isPrevNullMove);
        if (estimation > alfa) alfa = estimation;
        undoMove();

        while (alfa < beta && it.hasNext()) {
            move = it.next();
            makeMove(move);
            // null-window search:
            estimation =
                    -super.negaNullMoveSearch(
                            !isMyMove, -alfa - 1, -alfa, depth - 1, isPrevNullMove);
            if (alfa < estimation && estimation < beta)
                estimation =
                        -super.negaNullMoveSearch(
                                !isMyMove, -beta, -alfa, depth - 1, isPrevNullMove);
            undoMove();
            if (estimation > alfa) alfa = estimation;
        }

        return alfa;
    }
}
