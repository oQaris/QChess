package io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import java.util.List;

/** Делает необходимые проверки и уточняет оценку доски */
public class SimpleEval implements CommonEvaluation {

    private final SearchAlgorithm<?> alg;
    private final EvaluationFunc evaluationFunc;
    private final Color myColor;
    private final Color enemyColor;

    public SimpleEval(final SearchAlgorithm<?> alg, final EvaluationFunc evaluationFunc) {
        this.alg = alg;
        this.evaluationFunc = evaluationFunc;
        myColor = alg.getMyColor();
        enemyColor = alg.getEnemyColor();
    }

    @Override
    public int getEvaluation(
            final boolean isCheckToMe,
            final boolean isCheckToEnemy,
            final List<Move> allMoves,
            final boolean isMyMove,
            final int alfa,
            final int beta,
            final int depth)
            throws ChessError {
        if (alg.getLastWrapper().isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

        if (isMyMove) { // allMoves are mine
            if (allMoves.isEmpty()) { // пат
                if (isCheckToMe) return EvaluationFunc.MIN_ESTIMATION + 1000 - depth;
                return -depth;
            }

            if (alg.getLastWrapper().isStalemate(enemyColor)) {
                if (isCheckToEnemy) return EvaluationFunc.MAX_ESTIMATION - 1000 + depth;
                return -depth; // расширяем ничью - чем глубже, тем лучше
            }
        } else { // allMoves are enemy's
            if (allMoves.isEmpty()) { // пат
                if (isCheckToEnemy) return EvaluationFunc.MAX_ESTIMATION - 1000 + depth;
                return -depth; // расширяем ничью - чем глубже, тем лучше
            }

            if (alg.getLastWrapper().isStalemate(myColor)) {
                if (isCheckToMe) return EvaluationFunc.MIN_ESTIMATION + 1000 - depth;
                return -depth;
            }
        }

        if (alg.getLastWrapper().isInvalidMoveVersion()) return EvaluationFunc.MIN_ESTIMATION;

        // Проверка на ничью должна быть после проверок на пат и мат
        if (alg.getLastWrapper().isDraw()) return -depth; // расширяем ничью - чем глубже, тем лучше

        return evaluationFunc.getHeuristics(alg.getLastWrapper().getGameSettings(), myColor);
    }
}
