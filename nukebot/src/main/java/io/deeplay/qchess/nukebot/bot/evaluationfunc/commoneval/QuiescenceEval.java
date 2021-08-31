package io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import java.util.List;

/** Симулирует все атакующие ходы (если они есть) и считает оценку доски */
public class QuiescenceEval extends SimpleEval {

    private final SearchAlgorithm<?> alg;
    private final Color myColor;
    private final Color enemyColor;

    public QuiescenceEval(final SearchAlgorithm<?> alg, final EvaluationFunc evaluationFunc) {
        super(alg, evaluationFunc);
        this.alg = alg;
        myColor = alg.getMyColor();
        enemyColor = alg.getEnemyColor();
    }

    /**
     * Симулирует все атакующие ходы (если они есть) и считает оценку доски
     *
     * @return лучшая оценка доски для текущего игрока
     */
    @Override
    public int getEvaluation(
            final boolean isCheckToMe,
            final boolean isCheckToEnemy,
            List<Move> allMoves,
            final boolean isMyMove,
            int alfa,
            final int beta,
            final int depth)
            throws ChessError {
        if (alg.isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

        if (allMoves == null) {
            allMoves = alg.getLegalMoves(isMyMove ? myColor : enemyColor);
            if (alg.isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;
            alg.prioritySort(allMoves);
        }

        // --------------- Улучшение оценки --------------- //

        if (alg.isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

        {
            int standPat =
                    super.getEvaluation(
                            isCheckToMe, isCheckToEnemy, allMoves, isMyMove, alfa, beta, depth);
            if (!isMyMove) standPat = -standPat;

            if (alg.isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;
            if (standPat >= beta) return beta;
            if (alfa < standPat) alfa = standPat;
        }

        if (alg.isTerminalNode(allMoves)) return alfa;

        // --------------- Проведение взятий до потери пульса --------------- //

        for (final Move move : allMoves) {
            if (MoveSystem.isNotCapture(move)) continue;

            alg.makeMove(move);
            final int score =
                    -getEvaluation(
                            alg.isCheck(myColor),
                            alg.isCheck(enemyColor),
                            null,
                            !isMyMove,
                            -beta,
                            -alfa,
                            depth - 1);
            alg.undoMove();

            if (alg.isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

            if (score >= beta) {
                alfa = beta;
                break;
            }
            if (score > alfa) alfa = score;
        }

        return alfa;
    }
}
