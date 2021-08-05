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

    protected volatile Move theBestMove;
    protected volatile double theBestEvaluation = Double.MIN_VALUE;
    protected volatile double theBestDepth;

    protected ParallelSearch(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        this.gs = gs;
        this.evaluationFunc = evaluationFunc;
        this.myColor = color;
        this.enemyColor = color.inverse();
        this.maxDepth = maxDepth;
        this.theBestDepth = maxDepth;
    }

    protected synchronized void tryUpdateTheBestEvaluation(
            Move move, double evaluation, int depth) {
        if (depth <= theBestDepth && evaluation > theBestEvaluation) {
            theBestEvaluation = evaluation;
            theBestMove = move;
            // TODO: top moves
        }
    }

    @Override
    public Move findBest() throws ChessError {
        List<Move> allMoves = gs.board.getAllPreparedMoves(gs, myColor);
        Move theBest = null;
        int optEstimation = EvaluationFunc.MIN_ESTIMATION;

        SearchImprovements.prioritySort(allMoves);

        // TODO: запуск нескольких потоков для начальной глубины
        for (Move move : allMoves) {
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

    protected boolean isTerminalNode(List<Move> allMoves) {
        return allMoves.isEmpty() || gs.endGameDetector.isDraw();
    }

    protected int getEvaluation(List<Move> allMoves, boolean isMyMove, int depth)
            throws ChessError {
        List<Move> allEnemyMoves;
        List<Move> allMyMoves;
        if (isMyMove) {
            allMyMoves = allMoves;
            if (gs.endGameDetector.isStalemate(allMyMoves))
                return EvaluationFunc.MIN_ESTIMATION - depth;

            if (gs.endGameDetector.isCheckmate(enemyColor))
                return EvaluationFunc.MAX_ESTIMATION + depth;
        } else {
            allEnemyMoves = allMoves;
            if (gs.endGameDetector.isCheckmate(allEnemyMoves, enemyColor))
                return EvaluationFunc.MAX_ESTIMATION + depth;

            if (gs.endGameDetector.isStalemate(myColor))
                return EvaluationFunc.MIN_ESTIMATION - depth;
        }

        if (gs.endGameDetector.isDraw()) return EvaluationFunc.MIN_ESTIMATION - depth;

        return evaluationFunc.getHeuristics(gs, myColor);
    }
}
