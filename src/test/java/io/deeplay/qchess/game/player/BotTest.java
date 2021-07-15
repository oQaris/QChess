package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import junit.framework.TestCase;

public class BotTest extends TestCase {

    public void testBots() throws ChessError {
        // ExecutorService executor = Executors.newCachedThreadPool();
        final int COUNT = 1;
        for (int i = 0; i < COUNT; i++) {
            /*executor.execute(
            () -> {*/
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new RandomBot(roomSettings, Color.BLACK);
            Player secondPlayer = new RandomBot(roomSettings, Color.WHITE);
            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            // try {
            game.run();
            /*} catch (ChessError error) {
                error.printStackTrace();
            }*/
            // });
        }
        for (int i = 0; i < COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new AttackBot(roomSettings, Color.BLACK);
            Player secondPlayer = new RandomBot(roomSettings, Color.WHITE);
            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
        }
        for (int i = 0; i < COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new AttackBot(roomSettings, Color.BLACK);
            Player secondPlayer = new AttackBot(roomSettings, Color.WHITE);
            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
        }
        // executor.shutdown();
    }
}
