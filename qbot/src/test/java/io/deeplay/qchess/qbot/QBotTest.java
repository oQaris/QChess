package io.deeplay.qchess.qbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QBotTest {
    private static final Logger log = LoggerFactory.getLogger(QBotTest.class);
    final int COUNT = 10000;

    @Test
    void evaluateBoard() {
        ExecutorService executor = Executors.newCachedThreadPool();
        log.error("//------------ Минимаксный и Рандомный боты ------------//");
        long m = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            executor.execute(
                    () -> {
                        GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
                        Player firstPlayer = new QBot(roomSettings, Color.WHITE, 4);
                        Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);
                        try {
                            Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
                            game.run();
                        } catch (ChessError error) {
                            error.printStackTrace();
                        }
                    });
            System.out.println("1 - " + (i + 1) + "/" + COUNT);
        }
        log.error("Time: {}\n", System.currentTimeMillis() - m);
        executor.shutdown();
    }

    /*public void testQBot() throws ChessError, ChessException {
      // Cell.BOARD_SIZE = 3;
      GameSettings roomSettings = new GameSettings(3, BoardFilling.EMPTY);
      roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
      roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
      roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));
      roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(1, 2)));

      QBot bot = new QBot(roomSettings, Color.WHITE, 2);
      int grade = bot.minimaxRoot(2, true);
      Move bestMove = bot.getNextMove();

      assertEquals(-102, grade);
      assertEquals(
          new Move(MoveType.ATTACK, new Cell(1, 2), new Cell(2, 1)), bestMove);
    }

    public void testQBotMM() throws ChessError, ChessException {
      GameSettings roomSettings = new GameSettings(4, BoardFilling.EMPTY);
      roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
      roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
      roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));

      QBot bot = new QBot(roomSettings, Color.WHITE, 1);

      roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(2, 3)));
      int grade = bot.minimaxRoot(1, true);
      assertEquals(-102, grade);

      roomSettings.board.moveFigure(
          new Move(MoveType.QUIET_MOVE, new Cell(2, 3), new Cell(0, 3)));
      grade = bot.minimaxRoot(1, true);
      assertEquals(-92, grade);

      roomSettings.board.moveFigure(
          new Move(MoveType.QUIET_MOVE, new Cell(0, 3), new Cell(0, 1)));
      grade = bot.minimaxRoot(1, true);
      assertEquals(-99, grade);

      // таким образом, это лучший ход
      roomSettings.board.moveFigure(
          new Move(MoveType.QUIET_MOVE, new Cell(0, 1), new Cell(2, 1)));
      grade = bot.minimaxRoot(1, true);
      assertEquals(-38, grade);
    }

    public void testQBotAttack() throws ChessError, ChessException {
      GameSettings roomSettings = new GameSettings(4, BoardFilling.EMPTY);
      roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
      roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
      roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));
      roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(1, 2)));

      QBot bot = new QBot(roomSettings, Color.WHITE, 1);
      int grade = bot.minimaxRoot(1, true);
      Move bestMove = bot.getNextMove();

      assertEquals(-92, grade);
      assertEquals(
          new Move(MoveType.ATTACK, new Cell(1, 2), new Cell(2, 1)), bestMove);
    }

    public void testQBotStalemate1Step() throws ChessError, ChessException {
      // мат ладьёй за один ход
      GameSettings roomSettings = new GameSettings(5, BoardFilling.EMPTY);
      roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("a8")));
      roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("c7")));
      roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("d5")));
      System.out.println(roomSettings.board);

      QBot bot = new QBot(roomSettings, Color.BLACK, 2);
      List<Move> moves = bot.getNextMoves();

      assertEquals(1, moves.size());
      Move bestMove = moves.get(0);

      assertEquals(
          new Move(MoveType.QUIET_MOVE, Cell.parse("d5"), Cell.parse("d8")), bestMove);
    }

    public void testQBotStalemate2Step() throws ChessError, ChessException {
      // тут можно поставить мат в 2 хода
      GameSettings roomSettings = new GameSettings(BoardFilling.EMPTY);
      roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("c4")));
      roomSettings.board.setFigure(new King(Color.BLACK, Cell.parse("b8")));
      roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("h5")));
      roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("e7")));
      roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("c6")));

      QBot bot = new QBot(roomSettings, Color.WHITE, 3);

      List<Move> moves1 = bot.getNextMoves();
      assertEquals(3, moves1.size());

      Move expected = new Move(MoveType.QUIET_MOVE, Cell.parse("c6"), Cell.parse("f6"));
      Assertions.assertTrue(moves1.contains(expected));

      roomSettings.board.moveFigure(expected);
      roomSettings.board.moveFigure(
          new Move(MoveType.QUIET_MOVE, Cell.parse("b8"), Cell.parse("a8")));

      List<Move> moves2 = bot.getNextMoves();
      assertEquals(1, moves2.size());

      assertEquals(
          new Move(MoveType.QUIET_MOVE, Cell.parse("f6"), Cell.parse("f8")), moves2.get(0));
    }*/

    public void testQBotCheckMate2Step() throws ChessError, ChessException {
        // тут можно поставить пат в 1 ход, или мат в 2 хода
        /*GameSettings roomSettings = new GameSettings(BoardFilling.EMPTY);
        roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("c4")));
        roomSettings.board.setFigure(new King(Color.BLACK, Cell.parse("b8")));
        roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("h5")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("e7")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("c6")));*/
    }

    public void testEvaluateBoard() throws ChessException, ChessError {
        GameSettings roomSettings = new GameSettings(BoardFilling.STANDARD);
        QBot bot = new QBot(roomSettings, Color.WHITE, 2);

        assertEquals(0, bot.evaluateBoard());

        roomSettings.board.removeFigure(new Cell(1, 0));
        assertEquals(52, bot.evaluateBoard());

        roomSettings.board.removeFigure(new Cell(3, 7));
        assertEquals(-127, bot.evaluateBoard());
    }
}
