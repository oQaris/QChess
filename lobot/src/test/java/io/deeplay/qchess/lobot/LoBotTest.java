package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.lobot.strategy.FiguresCostSumEvaluateStrategy;
import io.deeplay.qchess.lobot.strategy.StaticPositionMatrixEvaluateStrategy;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoBotTest {
    private static final Logger logger = LoggerFactory.getLogger(LoBotTest.class);
    private static final int GAME_COUNT = 10;

    @Test
    public void testGame() {
        final ExecutorService executor = Executors.newCachedThreadPool();
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            // executor.execute(
            //        () -> {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new FiguresCostSumEvaluateStrategy(),
                            2,
                            TraversalAlgorithm.NEGASCOUT);
            final Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
            } catch (final ChessError e) {
                e.printStackTrace();
            }
            //        });
            logger.info("Game {} complete", i);
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        executor.shutdown();
    }

    @Test
    public void testGameNegascout() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new StaticPositionMatrixEvaluateStrategy(),
                            4,
                            TraversalAlgorithm.NEGASCOUT);
            final Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                final int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (final ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        logger.info("Draw: {}; Blackwin: {}; Whitewin: {}", results[0], results[1], results[2]);
    }

    @Test
    public void testGameMinimax() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new StaticPositionMatrixEvaluateStrategy(),
                            2,
                            TraversalAlgorithm.MINIMAX);
            final Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                final int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (final ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        logger.info("Draw: {}; Blackwin: {}; Whitewin: {}", results[0], results[1], results[2]);
    }

    @Test
    public void testGameExpectimax() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new StaticPositionMatrixEvaluateStrategy(),
                            2,
                            TraversalAlgorithm.EXPECTIMAX);
            final Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                final int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (final ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        logger.info("Draw: {}; Blackwin: {}; Whitewin: {}", results[0], results[1], results[2]);
    }

    @Test
    public void testGameWithTime() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(roomSettings, Color.WHITE, new FiguresCostSumEvaluateStrategy(), 2);
            final Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();

                final int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (final ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);

            logger.info("MAX TIME: {}", LoBot.MAX_TIME);
            logger.info("AVR TIME: {}", ((LoBot.FULL_TIME * 1.0) / LoBot.STEP_COUNT));
            logger.info("COUNT: {}\n", LoBot.STEP_COUNT);

            max += LoBot.MAX_TIME;
            avr += ((LoBot.FULL_TIME * 1.0) / LoBot.STEP_COUNT);
            LoBot.MAX_TIME = 0;
            LoBot.FULL_TIME = 0;
            LoBot.STEP_COUNT = 0;
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        logger.info("Draw: {}; Blackwin: {}; Whitewin: {}", results[0], results[1], results[2]);
        logger.info("AVR MAX: {}", (max * 1.0 / GAME_COUNT));
        logger.info("AVR AVR: {}", (avr * 1.0 / GAME_COUNT));
    }

    private int getEndGameType(final EndGameType egt) {
        if (egt == EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL
                || egt == EndGameType.DRAW_WITH_REPETITIONS
                || egt == EndGameType.DRAW_WITH_PEACE_MOVE_COUNT
                || egt == EndGameType.STALEMATE_TO_BLACK
                || egt == EndGameType.STALEMATE_TO_WHITE) {
            return 0;
        } else if (egt == EndGameType.CHECKMATE_TO_WHITE) {
            return 1;
        } else if (egt == EndGameType.CHECKMATE_TO_BLACK) {
            return 2;
        }
        return 3;
    }
}
