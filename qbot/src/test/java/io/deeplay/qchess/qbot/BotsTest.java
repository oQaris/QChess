package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.CHECKMATE_TO_BLACK;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.CHECKMATE_TO_WHITE;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_PEACE_MOVE_COUNT;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_REPETITIONS;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.STALEMATE_TO_BLACK;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.STALEMATE_TO_WHITE;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BotsTest {
    private static final Logger logger = LoggerFactory.getLogger(BotsTest.class);

    private static final int COUNT = 10;
    private static final Map<EndGameType, Integer> gameResultsWhite = initGameResults();
    private static final Map<EndGameType, Integer> gameResultsBlack = initGameResults();
    private static final AtomicLong meanMedianFirst = new AtomicLong(0);
    private static final AtomicLong meanMedianSecond = new AtomicLong(0);
    private static final AtomicInteger doneTasks = new AtomicInteger(0);
    private static final AtomicLong countNode = new AtomicLong(0);
    private static final AtomicLong countAB = new AtomicLong(0);

    private static Map<EndGameType, Integer> initGameResults() {
        return new ConcurrentHashMap<>(
                Map.of(
                        CHECKMATE_TO_BLACK, 0,
                        CHECKMATE_TO_WHITE, 0,
                        STALEMATE_TO_BLACK, 0,
                        STALEMATE_TO_WHITE, 0,
                        DRAW_WITH_NOT_ENOUGH_MATERIAL, 0,
                        DRAW_WITH_PEACE_MOVE_COUNT, 0,
                        DRAW_WITH_REPETITIONS, 0));
    }

    @Test
    void testGame() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= COUNT; i++) {
            executor.execute(
                    i % 2 == 0
                            ? new Game(Color.WHITE, gameResultsWhite)
                            : new Game(Color.BLACK, gameResultsBlack));
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        long timeInSec = (System.currentTimeMillis() - startTime) / 1000;

        int d1 =
                gameResultsBlack.get(DRAW_WITH_PEACE_MOVE_COUNT)
                        + gameResultsWhite.get(DRAW_WITH_PEACE_MOVE_COUNT);
        int d2 =
                gameResultsBlack.get(DRAW_WITH_REPETITIONS)
                        + gameResultsWhite.get(DRAW_WITH_REPETITIONS);
        int d3 =
                gameResultsBlack.get(DRAW_WITH_NOT_ENOUGH_MATERIAL)
                        + gameResultsWhite.get(DRAW_WITH_NOT_ENOUGH_MATERIAL);
        int d = d1 + d2 + d3;

        logger.warn("\n<------------------------------------------------------>");
        logger.warn("\nTime: {} min {} sec", timeInSec / 60, timeInSec % 60);
        logger.warn(
                "\nDraw count: {}\n"
                        + " Draw with peace move count:    {}\n"
                        + " Draw with repetitions:         {}\n"
                        + " Draw with not enough material: {}\n"
                        + "Checkmate to firstPlayer:  {}\n"
                        + "Checkmate to secondPlayer: {}\n"
                        + "Stalemate to firstPlayer:  {}\n"
                        + "Stalemate to secondPlayer: {}",
                d,
                d1,
                d2,
                d3,
                gameResultsBlack.get(CHECKMATE_TO_BLACK) + gameResultsWhite.get(CHECKMATE_TO_WHITE),
                gameResultsBlack.get(CHECKMATE_TO_WHITE) + gameResultsWhite.get(CHECKMATE_TO_BLACK),
                gameResultsBlack.get(STALEMATE_TO_BLACK) + gameResultsWhite.get(STALEMATE_TO_WHITE),
                gameResultsBlack.get(STALEMATE_TO_WHITE)
                        + gameResultsWhite.get(STALEMATE_TO_BLACK));
        /*logger.warn("Всего нодов: {}", countNode.get());
        logger.warn("Всего отсечений: {}", countAB.get());*/
        logger.warn("Mean Median firstPlayer: {}", meanMedianFirst.get());
        logger.warn("Mean Median secondPlayer: {}", meanMedianSecond.get());
        Assertions.assertTrue(true);
    }

    private static class Game implements Runnable {
        private final Color myColor;
        private final Map<EndGameType, Integer> resultsOutput;

        public Game(Color myColor, Map<EndGameType, Integer> resultsOutput) {
            this.myColor = myColor;
            this.resultsOutput = resultsOutput;
        }

        @Override
        public void run() {
            GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            TimeWrapper firstPlayer =
                    new TimeWrapper(new QBot(gs, myColor, 3, new PestoStrategy()));
            TimeWrapper secondPlayer = new TimeWrapper(new RandomBot(gs, myColor.inverse()));
            // Player secondPlayer = NNNBotFactory.getNNNBot(gs, myColor.inverse());
            try {
                Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }
            /*countNode.addAndGet(firstPlayer.countNode);
            countAB.addAndGet(firstPlayer.countAB);*/
            resultsOutput.computeIfPresent(gs.endGameDetector.getGameResult(), (k, v) -> v + 1);
            meanMedianFirst.addAndGet(firstPlayer.getMedian() / COUNT);
            meanMedianSecond.addAndGet(secondPlayer.getMedian() / COUNT);
            System.out.println("Games completed: " + (doneTasks.incrementAndGet()) + "/" + COUNT);
            logger.info("== First ==");
            logger.info("Mean: " + firstPlayer.getMean());
            logger.info("Median: " + firstPlayer.getMedian());
            logger.info("Mode: " + firstPlayer.getMode());
            logger.info("Max: " + firstPlayer.getMax());
            logger.info("Min: " + firstPlayer.getMin());
            // firstPlayer.printGraph();
            logger.info("== Second ==");
            logger.info("Mean: " + secondPlayer.getMean());
            logger.info("Median: " + secondPlayer.getMedian());
            logger.info("Mode: " + secondPlayer.getMode());
            logger.info("Max: " + secondPlayer.getMax());
            logger.info("Min: " + secondPlayer.getMin());
        }
    }
}
