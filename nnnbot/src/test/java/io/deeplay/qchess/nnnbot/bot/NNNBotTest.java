package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class NNNBotTest {

    private static final Logger logger = LoggerFactory.getLogger(NNNBotTest.class);

    private static final int COUNT = 1;

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

    private static volatile double averageTimeToMove;
    private static volatile double minTimeToMove = Double.MAX_VALUE;
    private static volatile double maxTimeToMove = Double.MIN_VALUE;

    private static String time;

    @Ignore
    @Test
    public void speedTest() throws ChessError {
        GameSettings gs = new GameSettings(BoardFilling.STANDARD);
        Random rand = new Random();
        int count = 1000000;
        int i = count;
        double time = 0;
        double time2 = 0;
        while (--i >= 0) {
            try {
                gs.moveSystem.move(
                        new Move(
                                MoveType.QUIET_MOVE,
                                new Cell(rand.nextInt(8), rand.nextInt(8)),
                                new Cell(rand.nextInt(8), rand.nextInt(8))));
            } catch (ChessError e) {
                // e.printStackTrace();
            }

            long startTime = System.nanoTime();
            gs.moveSystem.getAllPreparedMoves(Color.WHITE);
            gs.moveSystem.getAllPreparedMoves(Color.BLACK);
            time += (double) (System.nanoTime() - startTime) / count;

            startTime = System.nanoTime();
            gs.board.getAllPreparedMoves(gs, Color.WHITE);
            gs.board.getAllPreparedMoves(gs, Color.BLACK);
            time2 += (double) (System.nanoTime() - startTime) / count;
        }
        System.out.println("fast: " + time);
        System.out.println("simple: " + time2);
    }

    @Ignore
    @Test
    public void testGame() {
        time = LocalDateTime.now().withNano(0).toString().replace('T', '~').replace(":", ";");
        MDC.put("time", time);
        NNNBotFactory.setTime(time);

        int availableProcessorsCount = Runtime.getRuntime().availableProcessors();
        logger.info("Number of available processors: {}", availableProcessorsCount);

        long startTime;
        if (COUNT == 1) {
            startTime = System.currentTimeMillis();
            new Game(COUNT & 1).run();

        } else {
            ExecutorService executor =
                    Executors.newFixedThreadPool(Math.min(availableProcessorsCount, COUNT));
            startTime = System.currentTimeMillis();

            for (int i = 1; i <= COUNT; ++i) {
                executor.execute(new Game(i));
            }
            while (!allTasksAreDone) Thread.onSpinWait();
            executor.shutdown();
        }

        long timeInSec = (System.currentTimeMillis() - startTime) / 1000;

        logger.info("<------------------------------------------------------>");
        logger.info("Game count: {}", COUNT);
        logger.info("Time: {} min {} sec", timeInSec / 60, timeInSec % 60);
        logger.info("Average time to move: {} sec", averageTimeToMove / COUNT);
        logger.info("Time to move (min - max): {} - {} sec", minTimeToMove, maxTimeToMove);
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

    /** Мат ладьёй за 1 ход */
    @Ignore
    @Test
    public void testCheckmate() throws ChessError, ChessException {
        GameSettings gs = new GameSettings(BoardFilling.EMPTY);
        gs.board.setFigure(new King(Color.BLACK, Cell.parse("a8")));
        gs.board.setFigure(new King(Color.WHITE, Cell.parse("h8")));
        gs.board.setFigure(new Rook(Color.WHITE, Cell.parse("c7")));
        gs.board.setFigure(new Rook(Color.WHITE, Cell.parse("d5")));
        System.out.println(gs.board);

        NNNBot bot = NNNBotFactory.getNNNBot(gs, Color.WHITE);
        Selfplay game = new Selfplay(gs, bot, bot);

        Move move = bot.getNextMove();
        game.move(move);
        System.err.println(move);

        Assert.assertTrue(gs.endGameDetector.isCheckmate(Color.BLACK));
    }

    private static class Game implements Runnable {

        private final int id;

        private final Color NNNBotColor;
        private GameSettings gs;
        private Selfplay game;

        public Game(int id) {
            this.NNNBotColor = id % 2 == 0 ? Color.WHITE : Color.BLACK;
            this.id = id;
        }

        @Override
        public void run() {
            gs = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer;
            Player secondPlayer;
            NNNBot nnnBot;
            if (NNNBotColor == Color.WHITE) {
                nnnBot = NNNBotFactory.getNNNBot(gs, Color.WHITE);
                firstPlayer = nnnBot;
                // secondPlayer = new QBot(gs, Color.BLACK, 2);
                secondPlayer = new RandomBot(gs, Color.BLACK);
            } else {
                // firstPlayer = new QBot(gs, Color.WHITE, 2);
                firstPlayer = new RandomBot(gs, Color.WHITE);
                nnnBot = NNNBotFactory.getNNNBot(gs, Color.BLACK);
                secondPlayer = nnnBot;
            }

            try {
                MDC.put("game", Integer.toString(id));
                game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }

            synchronized (mutexDoneTask) {
                ++doneTasks;
                averageTimeToMove += nnnBot.getAverageTimeToThink();
                minTimeToMove = Math.min(minTimeToMove, nnnBot.getMinTimeToThink());
                maxTimeToMove = Math.max(maxTimeToMove, nnnBot.getMaxTimeToThink());
                EndGameType egt = updateEndGameStatistics();
                MDC.put("time", time);
                logger.info("<------------------->");
                logger.info("Games completed: {}/{}", doneTasks, COUNT);
                logger.info(
                        "Average time to move by #{}: {} sec; move count: {}",
                        nnnBot.getId(),
                        nnnBot.getAverageTimeToThink(),
                        nnnBot.getMoveCount());
                logger.info(
                        "Time to move by #{} (min - max): {} - {} sec",
                        nnnBot.getId(),
                        nnnBot.getMinTimeToThink(),
                        nnnBot.getMaxTimeToThink());
                logger.info("End game type by #{}: {}", nnnBot.getId(), egt.msg);
                if (doneTasks == COUNT) allTasksAreDone = true;
            }
        }

        private EndGameType updateEndGameStatistics() {
            synchronized (mutexDoneTask) {
                if (gs.endGameDetector.isCheckmate(game.getCurrentPlayerToMove().getColor())) {
                    if (game.getCurrentPlayerToMove().getColor() == NNNBotColor) {
                        ++checkmateToNNNBot;
                        return EndGameType.CHECKMATE_TO_NNN_BOT;
                    } else {
                        ++checkmateToOpponent;
                        return EndGameType.CHECKMATE_TO_OPPONENT;
                    }
                } else if (gs.endGameDetector.isStalemate(
                        game.getCurrentPlayerToMove().getColor())) {
                    if (game.getCurrentPlayerToMove().getColor() == NNNBotColor) {
                        ++stalemateToNNNBot;
                        return EndGameType.STALEMATE_TO_NNN_BOT;
                    } else {
                        ++stalemateToOpponent;
                        return EndGameType.STALEMATE_TO_OPPONENT;
                    }
                } else if (gs.endGameDetector.isDraw()) {
                    ++drawCount;
                    if (gs.endGameDetector.isDrawWithPeaceMoves()) {
                        ++drawWithPeaceMoveCount;
                        return EndGameType.DRAW_WITH_PEACE_MOVE_COUNT;
                    }
                    if (gs.endGameDetector.isDrawWithRepetitions()) {
                        ++drawWithRepetitions;
                        return EndGameType.DRAW_WITH_REPETITIONS;
                    }
                    if (gs.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate()) {
                        ++drawWithNotEnoughMaterialForCheckmate;
                        return EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL_FOR_CHECKMATE;
                    }
                }
                return EndGameType.NOTHING;
            }
        }

        private enum EndGameType {
            NOTHING("NOTHING"),
            DRAW_WITH_PEACE_MOVE_COUNT("Draw with peace move count"),
            DRAW_WITH_REPETITIONS("Draw with repetitions"),
            DRAW_WITH_NOT_ENOUGH_MATERIAL_FOR_CHECKMATE(
                    "Draw with not enough material for checkmate"),
            CHECKMATE_TO_NNN_BOT("Checkmate to NNNBot"),
            CHECKMATE_TO_OPPONENT("Checkmate to opponent"),
            STALEMATE_TO_NNN_BOT("Stalemate to NNNBot"),
            STALEMATE_TO_OPPONENT("Stalemate to opponent");

            public final String msg;

            EndGameType(String msg) {
                this.msg = msg;
            }
        }
    }
}
