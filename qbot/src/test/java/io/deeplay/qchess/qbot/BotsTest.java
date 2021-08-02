package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.*;

class BotsTest {
    private static final Logger logger = LoggerFactory.getLogger(BotsTest.class);

    private static final int COUNT = 100;
    private static final Map<EndGameType, Integer> gameResultsWhite = initGameResults();
    private static final Map<EndGameType, Integer> gameResultsBlack = initGameResults();
    private static final AtomicInteger doneTasks = new AtomicInteger(0);

    private static Map<EndGameType, Integer> initGameResults() {
        return new ConcurrentHashMap<>(
                Map.of(
                        EndGameType.CHECKMATE_TO_BLACK, 0,
                        EndGameType.CHECKMATE_TO_WHITE, 0,
                        EndGameType.STALEMATE_TO_BLACK, 0,
                        EndGameType.STALEMATE_TO_WHITE, 0,
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

        int d1 = gameResultsBlack.get(DRAW_WITH_PEACE_MOVE_COUNT) + gameResultsWhite.get(DRAW_WITH_PEACE_MOVE_COUNT);
        int d2 = gameResultsBlack.get(DRAW_WITH_REPETITIONS) + gameResultsWhite.get(DRAW_WITH_REPETITIONS);
        int d3 = gameResultsBlack.get(DRAW_WITH_NOT_ENOUGH_MATERIAL) + gameResultsWhite.get(DRAW_WITH_NOT_ENOUGH_MATERIAL);
        int d = d1 + d2 + d3;

        logger.info("\n<------------------------------------------------------>");
        logger.info("Time: {} min {} sec", timeInSec / 60, timeInSec % 60);
        logger.info(
                "Draw count: {}\n"
                        + " Draw with peace move count: {}\n"
                        + " Draw with repetitions: {}\n"
                        + " Draw with not enough material: {}\n"
                        + "Checkmate to QBot: {}\n"
                        + "Checkmate to opponent: {}\n"
                        + "Stalemate to QBot: {}\n"
                        + "Stalemate to opponent: {}",
                d, d1, d2, d3,
                gameResultsBlack.get(CHECKMATE_TO_BLACK) + gameResultsWhite.get(CHECKMATE_TO_WHITE),
                gameResultsBlack.get(CHECKMATE_TO_WHITE) + gameResultsWhite.get(CHECKMATE_TO_BLACK),
                gameResultsBlack.get(STALEMATE_TO_BLACK) + gameResultsWhite.get(STALEMATE_TO_WHITE),
                gameResultsBlack.get(STALEMATE_TO_WHITE) + gameResultsWhite.get(STALEMATE_TO_BLACK));
    }

    private static class Game implements Runnable {
        private final Color QBotColor;
        private final Map<EndGameType, Integer> resultsOutput;

        public Game(Color QBotColor, Map<EndGameType, Integer> resultsOutput) {
            this.QBotColor = QBotColor;
            this.resultsOutput = resultsOutput;
        }

        @Override
        public void run() {
            GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new QBot(gs, QBotColor, 1);
            Player secondPlayer = new QBot(gs, QBotColor.inverse(), 3);
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
