package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class NNNBotTest {

    private static final Logger logger = LoggerFactory.getLogger(NNNBotTest.class);

    private static final int COUNT = 50;

    private static final Object mutexDoneTask = new Object();
    private static volatile int doneTasks;
    private static volatile boolean allTasksAreDone;

    private static volatile int drawCount;
    private static volatile int drawWithPeaceMoveCount;
    private static volatile int drawWithRepetitions;
    private static volatile int drawWithNotEnoughMaterialForCheckmate;

    private static volatile int checkmateToNNNBot;
    private static volatile int checkmateToOpponent;
    private static volatile int stalemateToNNNBot;
    private static volatile int stalemateToOpponent;

    private static String time;

    @Ignore
    @Test
    public void testHash() {
        Board board = new Board(BoardFilling.STANDARD);
        Random rand = new Random();
        int count = 1000000;
        int i = count;
        double time = 0;
        while (--i >= 0) {
            try {
                board.moveFigureUgly(
                        new Move(
                                MoveType.QUIET_MOVE,
                                new Cell(rand.nextInt(8), rand.nextInt(8)),
                                new Cell(rand.nextInt(8), rand.nextInt(8))));
            } catch (NullPointerException ignore) {
            }

            long startTime = System.nanoTime();
            board.hashCode();
            time += (double) (System.nanoTime() - startTime) / count;
        }
        System.out.println(time);
    }

    @Test
    public void testGame() {
        time = LocalTime.now().withNano(0).toString().replace(":", ";");
        MDC.put("time", time);

        new Random().setSeed(time.hashCode());

        int availableProcessorsCount = Runtime.getRuntime().availableProcessors();
        logger.info("Number of available processors: {}", availableProcessorsCount);

        ExecutorService executor =
                Executors.newFixedThreadPool(Math.min(availableProcessorsCount, COUNT));
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= COUNT; ++i) {
            executor.execute(i % 2 == 0 ? new Game(Color.WHITE) : new Game(Color.BLACK));
        }
        while (!allTasksAreDone) Thread.onSpinWait();

        long timeInSec = (System.currentTimeMillis() - startTime) / 1000;
        executor.shutdown();

        logger.info("<------------------------------------------------------>");
        logger.info("Time: {} min {} sec", timeInSec / 60, timeInSec % 60);
        logger.info("Game count: {}", COUNT);
        logger.info("<------------------->");
        logger.info("Draw count: {}", drawCount);
        logger.info("Draw with peace move count: {}", drawWithPeaceMoveCount);
        logger.info("Draw with repetitions: {}", drawWithRepetitions);
        logger.info(
                "Draw with not enough material for checkmate: {}",
                drawWithNotEnoughMaterialForCheckmate);
        logger.info("<------------------->");
        logger.info("Checkmate to NNNBot: {}", checkmateToNNNBot);
        logger.info("Checkmate to opponent: {}", checkmateToOpponent);
        logger.info("Stalemate to NNNBot: {}", stalemateToNNNBot);
        logger.info("Stalemate to opponent: {}", stalemateToOpponent);
        logger.info("<------------------->");
        logger.info(
                "NNNBot win + semi-win rate: {}% + {}%",
                checkmateToOpponent * 100 / COUNT, stalemateToOpponent * 100 / COUNT);
        logger.info(
                "Opponent win + semi-win rate: {}% + {}%",
                checkmateToNNNBot * 100 / COUNT, stalemateToNNNBot * 100 / COUNT);
        logger.info("Draw rate: {}%", drawCount * 100 / COUNT);
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
            NNNBot nnnBot;
            if (NNNBotColor == Color.WHITE) {
                nnnBot = NNNBotFactory.getNNNBot(time, gs, Color.WHITE);
                firstPlayer = nnnBot;
                secondPlayer = new RandomBot(gs, Color.BLACK);
            } else {
                firstPlayer = new RandomBot(gs, Color.WHITE);
                nnnBot = NNNBotFactory.getNNNBot(time, gs, Color.BLACK);
                secondPlayer = nnnBot;
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
                MDC.put("time", time);
                logger.info("Games completed: {}/{}", doneTasks, COUNT);
                logger.info(
                        "Ботом #{} взято лучших состояний: {}; не взято: {}",
                        nnnBot.getId(),
                        nnnBot.getGetCache(),
                        nnnBot.getNOTgetCache());
                logger.info(
                        "Ботом #{} взято оптимальных состояний: {}; не взято: {}",
                        nnnBot.getId(),
                        nnnBot.getGetCacheVirt(),
                        nnnBot.getNOTgetCacheVirt());
                logger.info(
                        "Ботом #{} положено оптимальных состояний: {}",
                        nnnBot.getId(),
                        nnnBot.getPutCache());
                logger.info(
                        "Average time to move by NNNBot #{}: {} sec; move count: {}",
                        nnnBot.getId(),
                        nnnBot.getAverageTimeToThink(),
                        nnnBot.getMoveCount());
                logger.info(
                        "Max time to move by NNNBot #{}: {} sec; Min time: {} sec",
                        nnnBot.getId(),
                        nnnBot.getMaxTimeToThink(),
                        nnnBot.getMinTimeToThink());
                if (doneTasks == COUNT) allTasksAreDone = true;
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
