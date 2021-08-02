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
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.qbot.strategy.CounterStrategy;
import io.deeplay.qchess.qbot.strategy.MatrixStrategy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BotsTest {
    private static final Logger logger = LoggerFactory.getLogger(BotsTest.class);

    private static final int COUNT = 100;
    private static final Map<EndGameType, Integer> gameResultsWhite = initGameResults();
    private static final Map<EndGameType, Integer> gameResultsBlack = initGameResults();
    private static final AtomicInteger doneTasks = new AtomicInteger(0);

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
        Thread.sleep(100);  // чтоб успели обновиться gameResults

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
                        + " Draw with peace move count: {}\n"
                        + " Draw with repetitions: {}\n"
                        + " Draw with not enough material: {}\n"
                        + "Checkmate to QBot: {}\n"
                        + "Checkmate to opponent: {}\n"
                        + "Stalemate to QBot: {}\n"
                        + "Stalemate to opponent: {}",
                d,
                d1,
                d2,
                d3,
                gameResultsBlack.get(CHECKMATE_TO_BLACK) + gameResultsWhite.get(CHECKMATE_TO_WHITE),
                gameResultsBlack.get(CHECKMATE_TO_WHITE) + gameResultsWhite.get(CHECKMATE_TO_BLACK),
                gameResultsBlack.get(STALEMATE_TO_BLACK) + gameResultsWhite.get(STALEMATE_TO_WHITE),
                gameResultsBlack.get(STALEMATE_TO_WHITE)
                        + gameResultsWhite.get(STALEMATE_TO_BLACK));
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
            Player firstPlayer = new QBot(gs, myColor, 1, new MatrixStrategy());
            Player secondPlayer = new QBot(gs, myColor.inverse(), 3, new CounterStrategy());
            // Player secondPlayer = NNNBotFactory.getNNNBot(gs, myColor.inverse());
            try {
                Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }
            resultsOutput.computeIfPresent(gs.endGameDetector.getGameResult(), (k, v) -> v + 1);
            System.out.println("Games completed: " + (doneTasks.incrementAndGet()) + "/" + COUNT);
        }
    }
}
