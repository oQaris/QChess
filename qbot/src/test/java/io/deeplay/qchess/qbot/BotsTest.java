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
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BotsTest {
    private static final Logger logger = LoggerFactory.getLogger(BotsTest.class);

    private static final int COUNT = 50;
    private static final Object mutexDoneTask = new Object();
    private static volatile int doneTasks;
    private static volatile int drawCount;
    private static volatile int drawWithPeaceMoveCount;
    private static volatile int drawWithRepetitions;
    private static volatile int drawWithNotEnoughMaterialForCheckmate;
    private static volatile int checkmateToNNNBot;
    private static volatile int checkmateToOpponent;
    private static volatile int stalemateToNNNBot;
    private static volatile int stalemateToOpponent;

    @Test
    void testGame() {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= COUNT; i++) {
            executor.execute(i % 2 == 0 ? new Game(Color.WHITE) : new Game(Color.BLACK));
        }
        while (doneTasks != COUNT) Thread.onSpinWait();

        long timeInSec = (System.currentTimeMillis() - startTime) / 1000;
        executor.shutdown();

        logger.info("<------------------------------------------------------>");
        logger.info("Time: {} min {} sec", timeInSec / 60, timeInSec % 60);
        logger.info(
                "Draw count: {}\n"
                        + "Draw with peace move count: {}\n"
                        + "Draw with repetitions: {}\n"
                        + "Draw with not enough material for checkmate: {}\n"
                        + "Checkmate to NNNBot: {}\n"
                        + "Checkmate to opponent: {}\n"
                        + "Stalemate to NNNBot: {}\n"
                        + "Stalemate to opponent: {}",
                drawCount,
                drawWithPeaceMoveCount,
                drawWithRepetitions,
                drawWithNotEnoughMaterialForCheckmate,
                checkmateToNNNBot,
                checkmateToOpponent,
                stalemateToNNNBot,
                stalemateToOpponent);
    }

    private static class Game implements Runnable {

        private final Color NNNBotColor;
        private GameSettings gs;
        private Selfplay game;

        public Game(Color NNNBotColor) {
            this.NNNBotColor = NNNBotColor;
        }

        @Override
        public void run() {
            gs = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer;
            Player secondPlayer;
            if (NNNBotColor == Color.WHITE) {
                firstPlayer = new QBot(gs, Color.WHITE, 2);
                secondPlayer = new RandomBot(gs, Color.BLACK);
            } else {
                firstPlayer = new RandomBot(gs, Color.WHITE);
                secondPlayer = new QBot(gs, Color.BLACK, 2);
            }

            try {
                game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }

            synchronized (mutexDoneTask) {
                ++doneTasks;
                updateEndGameStatistics();
                logger.info("Games completed: {}/{}", doneTasks, COUNT);
            }
        }

        private void updateEndGameStatistics() {
            synchronized (mutexDoneTask) {
                if (gs.endGameDetector.isDraw()) {
                    ++drawCount;
                    if (gs.endGameDetector.isDrawWithPeaceMoves()) ++drawWithPeaceMoveCount;
                    if (gs.endGameDetector.isDrawWithRepetitions()) ++drawWithRepetitions;
                    if (gs.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate())
                        ++drawWithNotEnoughMaterialForCheckmate;
                } else if (gs.endGameDetector.isCheckmate(
                        game.getCurrentPlayerToMove().getColor())) {
                    if (game.getCurrentPlayerToMove().getColor() == NNNBotColor)
                        ++checkmateToNNNBot;
                    else ++checkmateToOpponent;
                } else {
                    if (game.getCurrentPlayerToMove().getColor() == NNNBotColor)
                        ++stalemateToNNNBot;
                    else ++stalemateToOpponent;
                }
            }
        }
    }
}
