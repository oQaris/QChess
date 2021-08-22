package io.deeplay.qchess.qbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.player.ConsolePlayer;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.qbot.strategy.MatrixStrategy;
import io.deeplay.qchess.qbot.strategy.SimpleStrategy;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Ignore
class QBotTest {
    private void setKings(final Cell whitePos, final Cell blackPos, final GameSettings gs)
            throws ChessException {
        final King kingB = new King(Color.BLACK, blackPos);
        kingB.wasMoved = true;
        gs.board.setFigure(kingB);
        final King kingW = new King(Color.WHITE, whitePos);
        kingW.wasMoved = true;
        gs.board.setFigure(kingW);
    }

    QBot getTestedBot(final GameSettings game, final Color myColor, final int depth) {
        return new QExpectimaxBot(game, myColor, depth, new SimpleStrategy());
    }

    @Test
    void testWithRand() throws ChessError {
        final GameSettings gameSettings = new GameSettings(BoardFilling.STANDARD);
        final RandomBot bot1 = new RandomBot(gameSettings, Color.WHITE);
        final QBot bot2 = new QExpectimaxBot(gameSettings, Color.BLACK, 3);

        final Selfplay game = new Selfplay(gameSettings, bot1, bot2);
        game.run();

        assertEquals(EndGameType.CHECKMATE_TO_WHITE, gameSettings.endGameDetector.getGameResult());
    }
    void testExpectimax() throws ChessError {
        final GameSettings gameSettings = new GameSettings(BoardFilling.STANDARD);
        final AttackBot bot1 = new AttackBot(gameSettings, Color.WHITE);
        final QExpectimaxBot bot2 = new QExpectimaxBot(gameSettings, Color.BLACK, 3);

        final Selfplay game = new Selfplay(gameSettings, bot1, bot2);
        game.run();

        assertEquals(EndGameType.CHECKMATE_TO_WHITE, gameSettings.endGameDetector.getGameResult());
    }

    @Test
    @Disabled
    void testFirstStep() throws ChessError {
        final GameSettings game = new GameSettings(BoardFilling.STANDARD);
        final QBot bot1 = new QExpectimaxBot(game, Color.WHITE, 3);
        final QBot bot2 = new QMinimaxBot(game, Color.WHITE, 3);
        assertEquals(bot1.getTopMoves(), bot2.getTopMoves());

        game.moveSystem.move(new Move(MoveType.QUIET_MOVE, Cell.parse("e2"), Cell.parse("e4")));
        game.moveSystem.move(new Move(MoveType.QUIET_MOVE, Cell.parse("b8"), Cell.parse("c6")));
        assertEquals(bot1.getTopMoves(), bot2.getTopMoves());
    }

    @Test
    void testQBotStalemate1Step() throws ChessError, ChessException {
        // мат ладьёй за один ход
        final GameSettings game = new GameSettings(Board.BoardFilling.EMPTY);
        setKings(Cell.parse("a8"), Cell.parse("h8"), game);
        game.board.setFigure(new Rook(Color.BLACK, Cell.parse("c7")));
        game.board.setFigure(new Rook(Color.BLACK, Cell.parse("d5")));
        System.out.println(game.board);

        final QBot bot = getTestedBot(game, Color.BLACK, 1);
        final List<Move> moves = bot.getTopMoves();

        assertEquals(1, moves.size());
        final Move bestMove = moves.get(0);

        assertEquals(new Move(MoveType.QUIET_MOVE, Cell.parse("d5"), Cell.parse("d8")), bestMove);
    }

    @Test
    void testQBotStalemate2Step() throws ChessError, ChessException {
        // тут можно поставить мат в 2 хода
        final GameSettings game = new GameSettings(Board.BoardFilling.EMPTY);
        setKings(Cell.parse("c4"), Cell.parse("b8"), game);
        game.board.setFigure(new Pawn(Color.BLACK, Cell.parse("h5")));
        game.board.setFigure(new Rook(Color.WHITE, Cell.parse("e7")));
        game.board.setFigure(new Rook(Color.WHITE, Cell.parse("c6")));

        final QBot bot = getTestedBot(game, Color.WHITE, 3);

        final List<Move> moves1 = bot.getTopMoves();

        final Move expected = new Move(MoveType.QUIET_MOVE, Cell.parse("c6"), Cell.parse("h6"));
        // assertTrue(moves1.contains(expected));

        game.moveSystem.move(expected);
        game.moveSystem.move(new Move(MoveType.QUIET_MOVE, Cell.parse("b8"), Cell.parse("a8")));

        final List<Move> moves2 = bot.getTopMoves();
        assertEquals(1, moves2.size());

        assertEquals(
                new Move(MoveType.QUIET_MOVE, Cell.parse("h6"), Cell.parse("h8")), moves2.get(0));
    }

    @Test
    void testQBotCheckMate2Step() throws ChessError, ChessException {
        // тут можно поставить пат в 1 ход, или мат в 2 хода
        // todo
    }

    @Test
    void testQBotCheckMate2StepAny() throws ChessError, ChessException {
        // тут можно поставить 2 разных мата в 2 хода
        final GameSettings game = new GameSettings(Board.BoardFilling.EMPTY);
        game.history.setMinBoardStateToSave(QBot.MAX_DEPTH);
        setKings(Cell.parse("e1"), Cell.parse("b1"), game);
        game.board.setFigure(new Queen(Color.WHITE, Cell.parse("a3")));
        game.board.setFigure(new Knight(Color.WHITE, Cell.parse("a2")));
        game.board.setFigure(new Pawn(Color.WHITE, Cell.parse("f2")));
        game.board.setFigure(new Pawn(Color.WHITE, Cell.parse("g2")));
        game.board.setFigure(new Pawn(Color.WHITE, Cell.parse("h3")));
        game.board.setFigure(new Pawn(Color.BLACK, Cell.parse("b2")));
        game.board.setFigure(new Pawn(Color.BLACK, Cell.parse("b3")));

        final QBot bot = getTestedBot(game, Color.WHITE, 5);

        for (final Move firstMove : bot.getTopMoves()) {
            game.moveSystem.move(firstMove);
            System.err.println("Белые походили: " + firstMove);

            final List<Move> blackMoves = game.moveSystem.getAllPreparedMoves(Color.BLACK);

            for (final Move move : blackMoves) {
                game.moveSystem.move(move);
                System.out.println("Чёрные походили: " + move);

                for (final Move secondMove : bot.getTopMoves()) {
                    game.moveSystem.move(secondMove);
                    System.err.println("Белые ответили: " + secondMove);
                    System.out.print(game.board);
                    assertEquals(
                            EndGameType.CHECKMATE_TO_BLACK,
                            game.endGameDetector.updateEndGameStatus());
                    game.moveSystem.undoMove();
                }
                game.moveSystem.undoMove();
            }
        }
    }

    @Test
    void testEvaluateBoard() throws ChessException {
        final Board board = new GameSettings(Board.BoardFilling.STANDARD).board;
        final Strategy strategy = new MatrixStrategy();

        assertEquals(0, strategy.evaluateBoard(board));

        board.removeFigure(new Cell(1, 0));
        assertEquals(52, strategy.evaluateBoard(board));

        board.removeFigure(new Cell(3, 7));
        assertEquals(-127, strategy.evaluateBoard(board));
    }
}
