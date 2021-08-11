package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.Updater;
import java.util.List;

public abstract class SearchAlgorithm implements Runnable {

    public final EvaluationFunc evaluationFunc;
    public final Color myColor;
    public final Color enemyColor;
    public final int maxDepth;

    protected final Updater updater;
    protected final Move mainMove;
    protected final GameSettings gs;

    protected SearchAlgorithm(
            final Updater updater,
            final Move mainMove,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        this.updater = updater;
        this.mainMove = mainMove;
        this.gs = gs;
        this.evaluationFunc = evaluationFunc;
        this.myColor = color;
        this.enemyColor = color.inverse();
        this.maxDepth = maxDepth;
    }

    protected boolean isTerminalNode(final List<Move> allMoves) {
        return allMoves.isEmpty() || gs.endGameDetector.isDraw();
    }

    public int getEvaluation(final List<Move> allMoves, final boolean isMyMove, final int depth)
            throws ChessError {
        Color enemyColor = myColor.inverse();
        if (isMyMove) { // allMoves are mine
            if (gs.endGameDetector.isStalemate(allMoves))
                return EvaluationFunc.MIN_ESTIMATION + 1000 - depth;

            if (gs.endGameDetector.isStalemate(enemyColor)) {
                if (gs.endGameDetector.isCheck(enemyColor)) {
                    return EvaluationFunc.MAX_ESTIMATION - 1000 + depth;
                }
                return 0;
            }
        } else { // allMoves are enemy's
            if (gs.endGameDetector.isStalemate(allMoves)) {
                if (gs.endGameDetector.isCheck(enemyColor)) {
                    return EvaluationFunc.MAX_ESTIMATION - 1000 + depth;
                }
                return 0;
            }

            if (gs.endGameDetector.isStalemate(myColor))
                return EvaluationFunc.MIN_ESTIMATION + 1000 - depth;
        }

        // Проверка на ничью должна быть после проверок на пат и мат
        if (gs.endGameDetector.isDraw()) return 0;

        return evaluationFunc.getHeuristics(gs, myColor);
    }
}
