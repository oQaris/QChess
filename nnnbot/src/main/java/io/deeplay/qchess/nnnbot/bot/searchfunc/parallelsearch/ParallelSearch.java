package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.SearchFunc;

import java.util.List;

/**
 * Поиск с альфа-бета отсечением на заданную глубину
 */
public abstract class ParallelSearch implements SearchFunc {

    public final int maxDepth;

    public final GameSettings gs;
    public final EvaluationFunc evaluationFunc;
    public final Color myColor;
    public final Color enemyColor;

    protected Move theBestMove;
    protected volatile double theBestEvaluation = Double.MIN_VALUE;

    protected ParallelSearch(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        this.gs = gs;
        this.evaluationFunc = evaluationFunc;
        this.myColor = color;
        this.enemyColor = color.inverse();
        this.maxDepth = maxDepth;
    }

    protected synchronized void tryUpdateTheBestEvaluation(Move move, double evaluation) {
        if (evaluation > theBestEvaluation) {
            theBestEvaluation = evaluation;
            theBestMove = move;
            // TODO: top moves
        }
    }

    @Override
    public Move findBest() throws ChessError {
        List<Move> allMoves = gs.moveSystem.getAllCorrectMovesSilence(myColor);
        Move theBest = null;
        double optEstimation = Double.NaN;

        // TODO: запуск нескольких потоков для начальной глубины
        for (Move move : allMoves) {
            gs.moveSystem.move(move);
            double estimation = getHeuristics(move, maxDepth);
            gs.moveSystem.undoMove();
            if (Double.isNaN(optEstimation) || estimation > optEstimation) {
                optEstimation = estimation;
                theBest = move;
            }
        }

        return theBest;
    }

    /**
     * Делает лучший виртуальный ход противника
     *
     * @return лучшая оценка для цвета color
     */
    public abstract double getHeuristics(Move move, int depth) throws ChessError;
}
