package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.lobot.evaluation.MonteCarloEvaluation;
import io.deeplay.qchess.lobot.evaluation.PestoEvaluation;
import io.deeplay.qchess.lobot.evaluation.StaticPositionMatrixEvaluation;
import io.deeplay.qchess.lobot.evaluation.FiguresCostSumEvaluation;
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
        ExecutorService executor = Executors.newCachedThreadPool();
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            // executor.execute(
            //        () -> {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.NEGASCOUT, 2));
            Player secondPlayer = new LoBot(roomSettings, Color.BLACK, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.MINIMAX, 2));
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
                e.printStackTrace();
            }
            //        });
            logger.info("Game {} complete", i);
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        logger.info("Draw: {}; Blackwin: {}; Whitewin: {}", results[0], results[1], results[2]);
        executor.shutdown();
    }

    @Test
    public void testMonteCarloEvaluation() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new MonteCarloEvaluation(5, false), TraversalAlgorithm.MINIMAX, 2));
            Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
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

    @Test
    public void testGameNegamaxWithAlphaBeta() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.NEGAMAXALPHABETA, 4));
            Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
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

    @Test
    public void testGamePesto() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new PestoEvaluation(), TraversalAlgorithm.MINIMAX, 2));
            Player secondPlayer = new LoBot(roomSettings, Color.BLACK, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.MINIMAX, 2));
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
            //max += LoBot.MAX_TIME;
            //avr += ((LoBot.FULL_TIME * 1.0) / LoBot.STEP_COUNT);
            //LoBot.MAX_TIME = 0;
            //LoBot.FULL_TIME = 0;
            //LoBot.STEP_COUNT = 0;
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        logger.info("Draw: {}; Blackwin: {}; Whitewin: {}", results[0], results[1], results[2]);
        //logger.info("AVR MAX: {}", (max * 1.0 / GAME_COUNT));
        //logger.info("AVR AVR: {}", (avr * 1.0 / GAME_COUNT));
    }

    @Test
    public void testGameNegamax() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.NEGAMAX, 2));
            Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
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

    @Test
    public void testGameNegascout() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE,new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.NEGASCOUT, 4));
            Player secondPlayer = new LoBot(roomSettings, Color.BLACK, new Strategy(new FiguresCostSumEvaluation(), TraversalAlgorithm.MINIMAX, 2));
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
                e.printStackTrace();
            }
            logger.info("Game {} complete", i);
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

    @Test
    public void testGameMinimax() {
        final int[] results = new int[3];
        Arrays.fill(results, 0);
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.MINIMAX, 4));
            Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
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
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.EXPECTIMAX, 2));
            Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
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
        long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= GAME_COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy(new StaticPositionMatrixEvaluation(), TraversalAlgorithm.MINIMAX, 4));
            Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();

                int index = getEndGameType(roomSettings.endGameDetector.getGameResult());
                if (index < 3) {
                    results[index]++;
                } else {
                    logger.info("{} WTF?!", i);
                }
            } catch (ChessError e) {
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

    private int getEndGameType(EndGameType egt) {
        if(egt == EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL
            || egt == EndGameType.DRAW_WITH_REPETITIONS
            || egt == EndGameType.DRAW_WITH_PEACE_MOVE_COUNT
            || egt == EndGameType.STALEMATE_TO_BLACK
            || egt == EndGameType.STALEMATE_TO_WHITE) {
            return 0;
        } else if(egt == EndGameType.CHECKMATE_TO_WHITE) {
            return 1;
        } else if(egt == EndGameType.CHECKMATE_TO_BLACK) {
            return 2;
        }
        return 3;
    }
}