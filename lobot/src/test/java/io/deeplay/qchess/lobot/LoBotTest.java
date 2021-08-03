package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoBotTest {
    private static final Logger logger = LoggerFactory.getLogger(LoBotTest.class);

    private static final int COUNT = 1;

    @Test
    public void testGame() {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();
        long fullIn = 0;
        for (int i = 1; i <= COUNT; i++) {
            // executor.execute(
            //        () -> {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new FullFieldEvaluateStrategy());
            Player secondPlayer = new LoBot(roomSettings, Color.BLACK, new SimpleEvaluateStrategy());
            try {
                Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }
            //        });
            logger.info("Game {} complete", i);
            System.out.println(roomSettings.board);
            //System.out.println(LoBot.COUNT);
            //fullIn += LoBot.COUNT;
            LoBot.COUNT = 0;
        }
        logger.info("Time: {}\n", System.currentTimeMillis() - startTime);
        System.out.println((fullIn * 1.0) / COUNT);
        executor.shutdown();
    }
}