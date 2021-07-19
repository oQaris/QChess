package io.deeplay.qchess.game.player;

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
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Rook;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(BotTest.class);
    final int COUNT = 1;

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
            Player firstPlayer = new AttackBot(roomSettings, Color.WHITE);
            Player secondPlayer = new MinimaxBot(roomSettings, Color.BLACK, 1);
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
        int grade = bot.minimaxRoot(2, true);
        Move bestMove = bot.getNextMove();

        Assertions.assertEquals(-102, grade);
        Assertions.assertEquals(
                new Move(MoveType.ATTACK, new Cell(1, 2), new Cell(2, 1)), bestMove);
    }

    public void testMinimaxBotMM() throws ChessError, ChessException {
        GameSettings roomSettings = new GameSettings(4, BoardFilling.EMPTY);
        roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
        roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
        roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));

        MinimaxBot bot = new MinimaxBot(roomSettings, Color.WHITE, 1);

        roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(2, 3)));
        int grade = bot.minimaxRoot(1, true);
        Assertions.assertEquals(-102, grade);

        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, new Cell(2, 3), new Cell(0, 3)));
        grade = bot.minimaxRoot(1, true);
        Assertions.assertEquals(-92, grade);

        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, new Cell(0, 3), new Cell(0, 1)));
        grade = bot.minimaxRoot(1, true);
        Assertions.assertEquals(-99, grade);

        // таким образом, это лучший ход
        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, new Cell(0, 1), new Cell(2, 1)));
        grade = bot.minimaxRoot(1, true);
        Assertions.assertEquals(-38, grade);
    }

    public void testMinimaxBotAttack() throws ChessError, ChessException {
        GameSettings roomSettings = new GameSettings(4, BoardFilling.EMPTY);
        roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
        roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
        roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));
        roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(1, 2)));

        MinimaxBot bot = new MinimaxBot(roomSettings, Color.WHITE, 1);
        int grade = bot.minimaxRoot(1, true);
        Move bestMove = bot.getNextMove();

        Assertions.assertEquals(-92, grade);
        Assertions.assertEquals(
                new Move(MoveType.ATTACK, new Cell(1, 2), new Cell(2, 1)), bestMove);
    }

    public void testEvaluateBoard() throws ChessException {
        GameSettings roomSettings = new GameSettings(BoardFilling.STANDARD);
        MinimaxBot bot = new MinimaxBot(roomSettings, Color.WHITE, 2);

        Assertions.assertEquals(0, bot.evaluateBoard());

        roomSettings.board.removeFigure(new Cell(1, 0));
        Assertions.assertEquals(52, bot.evaluateBoard());

        roomSettings.board.removeFigure(new Cell(3, 7));
        Assertions.assertEquals(-127, bot.evaluateBoard());
    }
}
