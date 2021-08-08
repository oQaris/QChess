package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.SearchFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.features.SearchImprovements;
import java.util.List;

/** Поиск с альфа-бета отсечением на заданную глубину */
public abstract class ParallelSearch implements SearchFunc {

    public final int maxDepth;

    public final GameSettings gs;
    public final EvaluationFunc evaluationFunc;
    public final Color myColor;
    public final Color enemyColor;

    protected ParallelSearch(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        this.gs = gs;
        this.evaluationFunc = evaluationFunc;
        this.myColor = color;
        this.enemyColor = color.inverse();
        this.maxDepth = maxDepth;
    }

    @Override
    public Move findBest() throws ChessError {
        final List<Move> allMoves = gs.board.getAllPreparedMoves(gs, myColor);
        Move theBest = null;
        int optEstimation = EvaluationFunc.MIN_ESTIMATION;

        SearchImprovements.prioritySort(allMoves);

        // TODO: запуск нескольких потоков для начальной глубины
        for (final Move move : allMoves) {
            gs.moveSystem.move(move);
            int estimation = run(maxDepth);
            gs.moveSystem.undoMove();
            if (estimation > optEstimation || theBest == null) {
                optEstimation = estimation;
                theBest = move;
            }
        }

        return theBest;
    }

    /** @return лучшая оценка для текущего цвета myColor */
    public abstract int run(int depth) throws ChessError;

    protected boolean timesUp(final long startTimeMillis, final long maxTimeMillis) {
        return System.currentTimeMillis() - startTimeMillis > maxTimeMillis;
    }

    protected boolean isTerminalNode(final List<Move> allMoves) {
        return allMoves.isEmpty() || gs.endGameDetector.isDraw();
    }

    protected int getEvaluation(final List<Move> allMoves, final boolean isMyMove, final int depth)
            throws ChessError {
        if (isMyMove) { // allMoves are mine
            if (gs.endGameDetector.isStalemate(allMoves))
                return EvaluationFunc.MIN_ESTIMATION - depth;

            if (gs.endGameDetector.isStalemate(enemyColor)) {
                if (gs.endGameDetector.isCheck(enemyColor)) {
                    return EvaluationFunc.MAX_ESTIMATION + depth;
                }
                return 0;
            }
        } else { // allMoves are enemy's
            if (gs.endGameDetector.isStalemate(allMoves)) {
                if (gs.endGameDetector.isCheck(enemyColor)) {
                    return EvaluationFunc.MAX_ESTIMATION + depth;
                }
                return 0;
            }

            if (gs.endGameDetector.isStalemate(myColor))
                return EvaluationFunc.MIN_ESTIMATION - depth;
        }

        // Проверка на ничью должна быть после проверок на пат и мат
        if (gs.endGameDetector.isDraw()) return 0;

        return evaluationFunc.getHeuristics(gs, myColor);
    }
}
