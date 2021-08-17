package io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithmFactory;
import io.deeplay.qchess.nukebot.bot.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.MTDFSearch;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Параллельный поиск на основе {@link ExecutorService} */
public class ParallelExecutorsSearch extends SearchFunc implements ResultUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ParallelExecutorsSearch.class);

    private final Object mutexTheBest = new Object();
    /** Используется, чтобы незавершенные потоки с прошлых ходов случайно не сломали текущий */
    private volatile int moveVersion;
    /** Лучшая оценка для текущего цвета myColor */
    private volatile int theBestEstimation;
    /** Лучший ход для текущего цвета myColor */
    private volatile Move theBestMove;
    /** Лучшая глубина для текущего цвета myColor, на которой была основана оценка */
    private volatile int theBestMaxDepth;

    public ParallelExecutorsSearch(
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public Move findBest() throws ChessError {
        final long startTime = System.currentTimeMillis();

        final List<Move> allMoves = gs.board.getAllPreparedMoves(gs, myColor);
        SearchImprovements.prioritySort(allMoves);

        theBestEstimation = EvaluationFunc.MIN_ESTIMATION;
        theBestMove = allMoves.get(0);
        theBestMaxDepth = -1;

        final int availableProcessorsCount = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor =
                Executors.newFixedThreadPool(Math.min(allMoves.size(), availableProcessorsCount));

        for (final Move move : allMoves) {
            final GameSettings gsParallel = new GameSettings(gs, maxDepth);
            final MTDFSearch searchAlgorithm =
                    SearchAlgorithmFactory.getMTDFCompatibleAlgorithm(
                            this, move, moveVersion, gsParallel, myColor, evaluationFunc, maxDepth);

            // executor.execute(() -> searchAlgorithm.MTDFStart(0, maxDepth));
            executor.execute(searchAlgorithm);
        }
        executor.shutdown();

        final long time = System.currentTimeMillis() - startTime;
        try {
            final boolean allIsComplete =
                    executor.awaitTermination(TIME_TO_MOVE - time, TimeUnit.MILLISECONDS);
            if (!allIsComplete)
                synchronized (mutexTheBest) {
                    ++moveVersion;
                }
        } catch (final InterruptedException e) {
            logger.error("Ошибка в функции поиска: {}", e.getMessage());
        }

        return theBestMove;
    }

    @Override
    public void updateResult(
            final Move move, final int estimation, final int maxDepth, final int moveVersion) {
        if (moveVersion != this.moveVersion) return;
        if (maxDepth > theBestMaxDepth || estimation > theBestEstimation) {
            synchronized (mutexTheBest) {
                // 2 одинаковые проверки нужны, чтобы лишний раз не синхронизировать
                if (moveVersion != this.moveVersion) return;
                if (maxDepth > theBestMaxDepth || estimation > theBestEstimation) {
                    theBestEstimation = estimation;
                    theBestMove = move;
                    theBestMaxDepth = maxDepth;
                }
            }
        }
    }

    @Override
    public boolean isInvalidMoveVersion(final int myMoveVersion) {
        return moveVersion != myMoveVersion;
    }
}
