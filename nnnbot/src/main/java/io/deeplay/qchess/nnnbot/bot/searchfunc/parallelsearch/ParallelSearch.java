package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.SearchFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.SearchAlgorithmFactory;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.searchalg.features.SearchImprovements;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Параллельный поиск с заданной функцией оценки и одной из реализаций конкретного алгоритма */
public class ParallelSearch extends SearchFunc implements Updater {

    private final ExecutorService executor;
    private final Object mutexTheBest = new Object();
    /** Лучшая оценка для текущего цвета myColor */
    private volatile int theBestEstimation;
    /** Лучший ход для текущего цвета myColor */
    private volatile Move theBestMove;

    public ParallelSearch(
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);

        final int availableProcessorsCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(availableProcessorsCount);
    }

    @Override
    public Move findBest() throws ChessError {
        long startTime = System.currentTimeMillis();

        final List<Move> allMoves = gs.board.getAllPreparedMoves(gs, myColor);
        SearchImprovements.prioritySort(allMoves);

        for (final Move move : allMoves) {
            final GameSettings gsParallel = new GameSettings(gs);
            SearchAlgorithm searchAlgorithm =
                    SearchAlgorithmFactory.getSearchAlgorithm(
                            this, move, gsParallel, myColor, evaluationFunc, maxDepth);

            executor.execute(searchAlgorithm);
        }

        while (theBestMove == null) Thread.onSpinWait();
        long time = System.currentTimeMillis() - startTime;
        try {
            executor.awaitTermination(TIME_TO_MOVE - time, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();

        return theBestMove;
    }

    @Override
    public void updateResult(Move move, int estimation) {
        if (estimation > theBestEstimation) {
            synchronized (mutexTheBest) {
                if (estimation > theBestEstimation) {
                    theBestEstimation = estimation;
                    theBestMove = move;
                }
            }
        }
    }

    protected boolean timesUp(final long startTimeMillis, final long maxTimeMillis) {
        return System.currentTimeMillis() - startTimeMillis > maxTimeMillis;
    }
}
