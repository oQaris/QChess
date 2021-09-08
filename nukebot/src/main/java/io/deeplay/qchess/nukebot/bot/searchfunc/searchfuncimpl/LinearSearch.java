package io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.List;

/** Простой линейный поиск без параллельных вычислений */
public class LinearSearch<T extends SearchAlgorithm<? super T>> extends SearchFunc<T> {

    private final T alg;
    /** Лучшая оценка для текущего цвета myColor */
    private int theBestEstimation;
    /** Лучший ход для текущего цвета myColor */
    private Move theBestMove;
    /** Лучшая глубина для текущего цвета myColor, на которой была основана оценка */
    private int theBestMaxDepth;

    private long startTime;

    public LinearSearch(final T alg) {
        super(alg);
        this.alg = alg;
    }

    @Override
    public Move findBest() throws ChessError {
        startTime = System.currentTimeMillis();

        final List<Move> allMoves = getLegalMoves(myColor);
        prioritySort(allMoves);

        theBestEstimation = EvaluationFunc.MIN_ESTIMATION;

        for (final Move move : allMoves) {
            alg.setSettings(move, gs, maxDepth, moveVersion, this);
            alg.run();
        }

        return theBestMove;
    }

    @Override
    public void updateResult(
            final Move move, final int estimation, final int startDepth, final int moveVersion) {
        if (startDepth > theBestMaxDepth || estimation > theBestEstimation) {
            theBestEstimation = estimation;
            theBestMove = move;
            theBestMaxDepth = startDepth;
        }
    }

    @Override
    public boolean isInvalidMoveVersion(final int myMoveVersion) {
        return SearchFunc.timesUp(startTime);
    }

    @Override
    public void run() {
        alg.run();
    }
}
