package io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.features.MoveSorter;
import io.deeplay.qchess.nukebot.bot.features.components.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Параллельный поиск на основе {@link ExecutorService} */
public class ParallelExecutorsSearch<T extends SearchAlgorithm<? super T>> extends SearchFunc<T>
        implements ResultUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ParallelExecutorsSearch.class);

    private final Random random = new Random();

    private final TranspositionTable table = new TranspositionTable();

    private final Object mutexTheBest = new Object();
    private final T alg;
    /** Используется, чтобы незавершенные потоки с прошлых ходов случайно не сломали текущий */
    private volatile int moveVersion;
    /** Лучшая оценка для текущего цвета myColor */
    private volatile int theBestEstimation;
    /** Лучший ход для текущего цвета myColor */
    private volatile Move theBestMove;
    /** Лучшая глубина для текущего цвета myColor, на которой была основана оценка */
    private volatile int theBestMaxDepth;

    public ParallelExecutorsSearch(final T alg) {
        super(alg);
        this.alg = alg;
    }

    private ParallelExecutorsSearch(
            final Move mainMove,
            final GameSettings gs,
            final int maxDepth,
            final int moveVersion,
            final ParallelExecutorsSearch<T> searchFunc) {
        super(mainMove, gs, maxDepth, moveVersion, searchFunc);
        alg = searchFunc.alg;
        alg.setSettings(mainMove, gs, maxDepth, moveVersion);
    }

    @Override
    public Move findBest() throws ChessError {
        final long startTime = System.currentTimeMillis();

        final List<Move> allMoves = gs.board.getAllPreparedMoves(gs, myColor, table);
        MoveSorter.moveTypeSort(allMoves);

        theBestEstimation = EvaluationFunc.MIN_ESTIMATION;
        theBestMove = allMoves.get(random.nextInt(allMoves.size()));
        theBestMaxDepth = -1;

        final int availableProcessorsCount = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor =
                Executors.newFixedThreadPool(Math.min(allMoves.size(), availableProcessorsCount));

        for (final Move move : allMoves) {
            final GameSettings gsParallel = new GameSettings(gs, maxDepth);
            alg.setSettings(move, gsParallel, maxDepth, moveVersion);
            alg.run();
//            executor.execute(
//                    new ParallelExecutorsSearch<>(move, gsParallel, maxDepth, moveVersion, this));
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
            final Move move, final int estimation, final int startDepth, final int moveVersion) {
        if (moveVersion != this.moveVersion) return;
        if (startDepth > theBestMaxDepth || estimation > theBestEstimation) {
            synchronized (mutexTheBest) {
                // 2 одинаковые проверки нужны, чтобы лишний раз не синхронизировать
                if (moveVersion != this.moveVersion) return;
                if (startDepth > theBestMaxDepth || estimation > theBestEstimation) {
                    theBestEstimation = estimation;
                    theBestMove = move;
                    theBestMaxDepth = startDepth;
                }
            }
        }
    }

    @Override
    public boolean isInvalidMoveVersion(final int myMoveVersion) {
        return moveVersion != myMoveVersion;
    }

    @Override
    public void run() {
        alg.run();
    }
}
