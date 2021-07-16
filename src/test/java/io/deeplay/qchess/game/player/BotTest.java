package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Rook;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(BotTest.class);
    final int COUNT = 100;

    public void testBotsRandom() throws ChessError {
        // ExecutorService executor = Executors.newCachedThreadPool();
        log.error("//------------ Два Рандомных Бота ------------//");
        long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            /*executor.execute(
            () -> {*/
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new RandomBot(roomSettings, Color.WHITE);
            Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            // try {
            game.run();
            /*} catch (ChessError error) {
                error.printStackTrace();
            }*/
            // });
            System.out.println("1 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}\n", System.currentTimeMillis() - m);
        // executor.shutdown();
    }

    public void testBotsRndAtk() throws ChessError {
        log.error("//------------ Райндомный и Атакующий Боты ------------//");
        long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new RandomBot(roomSettings, Color.WHITE);
            Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
            System.out.println("2 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}\n", System.currentTimeMillis() - m);
    }

    public void testBotsAttack() throws ChessError {
        log.error("//------------ Два Атакующих Бота ------------//");
        long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new AttackBot(roomSettings, Color.WHITE);
            Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
            System.out.println("3 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}", System.currentTimeMillis() - m);
    }

    public void testBotsRndMM() throws ChessError {
        log.error("//------------ Райндомный и Минимаксный Боты ------------//");
        long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new MinimaxBot(roomSettings, Color.WHITE, 2);
            Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
            game.run();
            System.out.println("4 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}\n", System.currentTimeMillis() - m);
    }

    public void testMinimaxBot() throws ChessError, ChessException {
        // Cell.BOARD_SIZE = 3;
        GameSettings roomSettings = new GameSettings(3, BoardFilling.EMPTY);
        roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
        roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
        roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));
        roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(1, 2)));

        MinimaxBot bot = new MinimaxBot(roomSettings, Color.WHITE, 2);
        int grade = bot.minimax(2, true);
        Assertions.assertEquals(-5, grade);
    }
}
