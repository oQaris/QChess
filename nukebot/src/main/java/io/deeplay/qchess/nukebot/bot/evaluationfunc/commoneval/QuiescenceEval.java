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

    /** Поднимает границу для очищения истории */
    private void upMinBoardStateToSave() {
        alg.getGameSettings().history.setMinBoardStateToSave(alg.getMaxDepth() + 64);
    }

    /** Опускает границу для очищения истории */
    private void downMinBoardStateToSave() {
        alg.getGameSettings().history.setMinBoardStateToSave(alg.getMaxDepth());
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
        if (alg.getLastWrapper().isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

        if (allMoves == null) {
            allMoves = alg.getLastWrapper().getLegalMoves(isMyMove ? myColor : enemyColor);
            if (alg.getLastWrapper().isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;
            alg.getLastWrapper().prioritySort(allMoves);
        }

        // --------------- Улучшение оценки --------------- //

        if (alg.getLastWrapper().isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

        {
            int standPat =
                    super.getEvaluation(
                            isCheckToMe, isCheckToEnemy, allMoves, isMyMove, alfa, beta, depth);
            if (!isMyMove) standPat = -standPat;

            if (alg.getLastWrapper().isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;
            if (standPat >= beta) return beta;
            if (alfa < standPat) alfa = standPat;
        }

        if (alg.getLastWrapper().isTerminalNode(allMoves)) return alfa;

        // --------------- Проведение взятий до потери пульса --------------- //

        for (final Move move : allMoves) {
            if (MoveSystem.isNotCapture(move)) continue;

            upMinBoardStateToSave();
            alg.getLastWrapper().makeMove(move);
            final int score =
                    -getEvaluation(
                            alg.getLastWrapper().isCheck(myColor),
                            alg.getLastWrapper().isCheck(enemyColor),
                            null,
                            !isMyMove,
                            -beta,
                            -alfa,
                            depth - 1);
            alg.getLastWrapper().undoMove();
            downMinBoardStateToSave();

            if (alg.getLastWrapper().isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

            if (score >= beta) {
                alfa = beta;
                break;
            }
            if (score > alfa) alfa = score;
        }

        return alfa;
    }
}
