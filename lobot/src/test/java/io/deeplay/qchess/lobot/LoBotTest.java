package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoBotTest {
    private static final Logger logger = LoggerFactory.getLogger(LoBotTest.class);

    private static final int COUNT = 100;

    private final int[] results = new int[3];

    @Test
    public void testGame() {
        Arrays.fill(results, 0);
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();
        int avr = 0;
        int max = 0;

        for (int i = 1; i <= COUNT; i++) {
            // executor.execute(
            //        () -> {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.BLACK, new SimpleEvaluateStrategy(), 2);
            Player secondPlayer = new LoBot(roomSettings, Color.WHITE, new FullFieldEvaluateStrategy(), 2);
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();

                roomSettings.endGameDetector.isCheckmate(Color.BLACK);
                roomSettings.endGameDetector.isCheckmate(Color.WHITE);
                roomSettings.endGameDetector.isDraw();
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

            logger.info("MAX TIME: {}", LoBot.MAX);
            logger.info("AVR TIME: {}", ((LoBot.TIME * 1.0) / LoBot.COUNT));
            logger.info("COUNT: {}\n", LoBot.COUNT);

            max += LoBot.MAX;
            avr += ((LoBot.TIME * 1.0) / LoBot.COUNT);
            LoBot.MAX = 0;
            LoBot.TIME = 0;
            LoBot.COUNT = 0;
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        logger.info("Draw: {}; Blackwin: {}; Whitewin: {}", results[0], results[1], results[2]);
        logger.info("AVR MAX: {}", (max * 1.0 / COUNT));
        logger.info("AVR AVR: {}", (avr * 1.0 / COUNT));
        executor.shutdown();
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