package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(BotTest.class);
    final int COUNT = 1;

    public void testBotsRandom() throws ChessError {
        log.error("//------------ Два Рандомных Бота ------------//");
        final long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer = new RandomBot(roomSettings, Color.WHITE);
            final Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
            System.out.println("1 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}", System.currentTimeMillis() - m);
    }

    public void testBotsRndAtk() throws ChessError {
        log.error("//------------ Рандомный и Атакующий Боты ------------//");
        final long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer = new RandomBot(roomSettings, Color.WHITE);
            final Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
            final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
            System.out.println("2 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}", System.currentTimeMillis() - m);
    }

    public void testBotsAttack() throws ChessError {
        log.error("//------------ Два Атакующих Бота ------------//");
        final long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            final Player firstPlayer = new AttackBot(roomSettings, Color.WHITE);
            final Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
            final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
            System.out.println("3 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}", System.currentTimeMillis() - m);
    }
}
