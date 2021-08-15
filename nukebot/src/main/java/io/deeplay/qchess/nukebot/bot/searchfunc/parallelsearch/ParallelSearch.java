package io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.SearchAlgorithmFactory;
import io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.features.SearchImprovements;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Параллельный поиск с заданной функцией оценки и одной из реализаций конкретного алгоритма */
public class ParallelSearch extends SearchFunc implements Updater {

    private final Object mutexTheBest = new Object();
    private final ExecutorService executor;
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
        executor = Executors.newFixedThreadPool(availableProcessorsCount);
    }

    @Override
    public Move findBest() throws ChessError {
        final long startTime = System.currentTimeMillis();

        final List<Move> allMoves = gs.board.getAllPreparedMoves(gs, myColor);
        SearchImprovements.prioritySort(allMoves);

        theBestEstimation = EvaluationFunc.MIN_ESTIMATION;

        // TODO: а че каждый раз новый создавать надо???
        // final int availableProcessorsCount = Runtime.getRuntime().availableProcessors();
        // this.executor = Executors.newFixedThreadPool(availableProcessorsCount);

        for (final Move move : allMoves) {
            final GameSettings gsParallel = gs;
            // final GameSettings gsParallel = new GameSettings(gs);
            final var searchAlgorithm =
                    SearchAlgorithmFactory.getMTDFCompatibleAlgorithm(
                            this, move, gsParallel, myColor, evaluationFunc, maxDepth);

            searchAlgorithm.run();
            // searchAlgorithm.MTDFStart(0, maxDepth, 5000);
            // executor.execute(searchAlgorithm);
        }

        /*while (theBestEstimation == EvaluationFunc.MIN_ESTIMATION) Thread.onSpinWait();
        final long time = System.currentTimeMillis() - startTime;
        try {
            executor.awaitTermination(TIME_TO_MOVE + 10000 - time, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();*/

        return theBestMove;
    }

    @Override
    public void updateResult(final Move move, final int estimation) {
        if (estimation > theBestEstimation) {
            synchronized (mutexTheBest) {
                if (estimation > theBestEstimation) {
                    theBestEstimation = estimation;
                    theBestMove = move;
                }
            }
        }
    }

    private boolean timesUp(final long startTimeMillis, final long maxTimeMillis) {
        return System.currentTimeMillis() - startTimeMillis > maxTimeMillis;
    }
}
