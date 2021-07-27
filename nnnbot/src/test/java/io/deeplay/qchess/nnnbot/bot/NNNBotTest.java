package io.deeplay.qchess.nnnbot.bot;

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

public class NNNBotTest {

    private static final Logger logger = LoggerFactory.getLogger(NNNBotTest.class);

    private static final int COUNT = 50;
    private static final Object mutexDoneTasks = new Object();
    private static volatile int doneTasks;

    @Test
    public void testGame() {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= COUNT; i++) {
            executor.execute(
                    () -> {
                        GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
                        Player firstPlayer = new RandomBot(roomSettings, Color.WHITE);
                        Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
                        try {
                            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                            game.run();
                        } catch (ChessError e) {
                            e.printStackTrace();
                        }
                        synchronized (mutexDoneTasks) {
                            ++doneTasks;
                            logger.info("Games completed: {}/{}", doneTasks, COUNT);
                        }
                    });
        }
        while (doneTasks != COUNT) Thread.onSpinWait();
        long timeInSec = (System.currentTimeMillis() - startTime) / 1000;
        logger.info("Time: {} min {} sec", timeInSec / 60, timeInSec % 60);
        executor.shutdown();
    }
}
