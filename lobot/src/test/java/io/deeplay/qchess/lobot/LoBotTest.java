package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.lobot.evaluation.FiguresCostSumEvaluation;
import io.deeplay.qchess.lobot.evaluation.PestoEvaluation;
import io.deeplay.qchess.lobot.evaluation.StaticPositionMatrixEvaluation;
import java.util.Arrays;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoBotTest {

    private static final Logger logger = LoggerFactory.getLogger(LoBotTest.class);
    private static final int GAME_COUNT = 10;

    @Test
    public void testGame() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();
        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.NEGASCOUT,
                                    2,
                                    false));
            final Player secondPlayer =
                    new LoBot(
                            roomSettings,
                            Color.BLACK,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.MINIMAX,
                                    2,
                                    false));
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
    public void testMonteCarloEvaluation() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.MINIMAX,
                                    4,
                                    true));
            final Player secondPlayer =
                    new LoBot(
                            roomSettings,
                            Color.BLACK,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.MINIMAX,
                                    4,
                                    false));

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
    public void testGameNegamaxWithAlphaBeta() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.NEGAMAXALPHABETA,
                                    4,
                                    false));
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
    public void testGamePesto() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new Strategy(
                                    new PestoEvaluation(), TraversalAlgorithm.MINIMAX, 5, false));
            final Player secondPlayer =
                    new LoBot(
                            roomSettings,
                            Color.BLACK,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.MINIMAX,
                                    5,
                                    false));
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
    public void testGameNegamax() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.NEGAMAX,
                                    2,
                                    false));
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
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.NEGASCOUT,
                                    4,
                                    false));
            final Player secondPlayer =
                    new LoBot(
                            roomSettings,
                            Color.BLACK,
                            new Strategy(
                                    new FiguresCostSumEvaluation(),
                                    TraversalAlgorithm.MINIMAX,
                                    2,
                                    false));
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
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.MINIMAX,
                                    4,
                                    false));
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
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.EXPECTIMAX,
                                    2,
                                    false));
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

        for (int i = 1; i <= GAME_COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer =
                    new LoBot(
                            roomSettings,
                            Color.WHITE,
                            new Strategy(
                                    new StaticPositionMatrixEvaluation(),
                                    TraversalAlgorithm.MINIMAX,
                                    4,
                                    false));
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
