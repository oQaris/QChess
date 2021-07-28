package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BotsTest {
    private static final Logger logger = LoggerFactory.getLogger(BotsTest.class);

    private static final int COUNT = 50;
    private static final Object mutexDoneTask = new Object();
    private static volatile int doneTasks;
    private static volatile int drawWithPeaceMoveCount;
    private static volatile int drawWithRepetitions;
    private static volatile int drawWithNotEnoughMaterial;
    private static volatile int checkmateToNNNBot;
    private static volatile int checkmateToOpponent;
    private static volatile int stalemateToNNNBot;
    private static volatile int stalemateToOpponent;

    @Test
    void testGame() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= COUNT; i++) {
            executor.execute(i % 2 == 0 ? new Game(Color.WHITE) : new Game(Color.BLACK));
        }

        executor.awaitTermination(9999, TimeUnit.DAYS);
        executor.shutdown();
        long timeInSec = (System.currentTimeMillis() - startTime) / 1000;

        logger.info("<------------------------------------------------------>");
        logger.info("Time: {} min {} sec", timeInSec / 60, timeInSec % 60);
        logger.info(
                "Draw count: {}\n"
                        + " Draw with peace move count: {}\n"
                        + " Draw with repetitions: {}\n"
                        + " Draw with not enough material: {}\n"
                        + "Checkmate to NNNBot: {}\n"
                        + "Checkmate to opponent: {}\n"
                        + "Stalemate to NNNBot: {}\n"
                        + "Stalemate to opponent: {}",
                drawWithPeaceMoveCount
                        + drawWithPeaceMoveCount
                        + drawWithRepetitions
                        + drawWithNotEnoughMaterial,
                drawWithPeaceMoveCount,
                drawWithRepetitions,
                drawWithNotEnoughMaterial,
                checkmateToNNNBot,
                checkmateToOpponent,
                stalemateToNNNBot,
                stalemateToOpponent);
    }

    private static class Game implements Runnable {
        private final Color QBotColor;
        private Selfplay game;

        public Game(Color QBotColor) {
            this.QBotColor = QBotColor;
        }

        @Override
        public void run() {
            GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer;
            Player secondPlayer;

            if (QBotColor == Color.WHITE) {
                firstPlayer = new QBot(gs, Color.WHITE);
                secondPlayer = new RandomBot(gs, Color.BLACK);
            } else {
                firstPlayer = new RandomBot(gs, Color.WHITE);
                secondPlayer = new QBot(gs, Color.BLACK);
            }

            try {
                game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }

            // Обновление статистики игр
            synchronized (mutexDoneTask) {
                if (gs.endGameDetector.isDraw()) {
                    if (gs.endGameDetector.isDrawWithPeaceMoves()) ++drawWithPeaceMoveCount;
                    if (gs.endGameDetector.isDrawWithRepetitions()) ++drawWithRepetitions;
                    if (gs.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate())
                        ++drawWithNotEnoughMaterial;
                } else if (gs.endGameDetector.isCheckmate(
                    game.getCurrentPlayerToMove().getColor())) {
                    if (game.getCurrentPlayerToMove().getColor() == QBotColor)
                        ++checkmateToNNNBot;
                    else ++checkmateToOpponent;
                } else {
                    if (game.getCurrentPlayerToMove().getColor() == QBotColor)
                        ++stalemateToNNNBot;
                    else ++stalemateToOpponent;
                }
                System.out.println("Games completed: "+(++doneTasks)+"/"+COUNT);
            }
        }
    }
}
