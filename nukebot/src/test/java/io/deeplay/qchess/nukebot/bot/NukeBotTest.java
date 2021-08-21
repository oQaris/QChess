package io.deeplay.qchess.nukebot.bot;

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
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.TimeWrapper;
import io.deeplay.qchess.nukebot.bot.NukeBotFactory.NukeBotSettings;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class NukeBotTest {

    @Ignore
    @Test
    public void testGame() throws ChessError {
        final GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);

        final var p1 = new TimeWrapper(NukeBotFactory.getNukeBot(gs, Color.WHITE));
        final var p2 = new RandomBot(gs, Color.BLACK);

        new Selfplay(gs, p1, p2).run();

        p1.printGraph();
        System.out.println("Среднее арифметическое время на ход: " + p1.getMean() / 1000.);
        System.out.println("Среднее медианное время на ход: " + p1.getMedian() / 1000.);
        System.out.println("Мода времени хода: " + p1.getMode() / 1000.);
        System.out.println("Максимум на ход: " + p1.getMax() / 1000.);
        System.out.println("Минимум на ход: " + p1.getMin() / 1000.);
    }

    @Ignore
    @Test
    public void testBotSettings() {
        System.out.println(NukeBotSettings.getStandardSettings());
    }

    @Ignore
    @Test
    public void testCreateBot() {
        NukeBotFactory.getNukeBot(new GameSettings(Board.BoardFilling.STANDARD), Color.WHITE);
    }

    /** Мат ладьей за 1 ход */
    @Test
    public void testCheckmate() throws ChessError, ChessException {
        final GameSettings gs = new GameSettings(BoardFilling.EMPTY);
        gs.board.setFigure(new King(Color.BLACK, Cell.parse("a8")));
        gs.board.setFigure(new King(Color.WHITE, Cell.parse("h8")));
        gs.board.setFigure(new Rook(Color.WHITE, Cell.parse("c7")));
        gs.board.setFigure(new Rook(Color.WHITE, Cell.parse("d5")));
        System.out.println(gs.board);

        final NukeBot bot = NukeBotFactory.getNukeBot(gs, Color.WHITE);
        final NukeBot bot2 = NukeBotFactory.getNukeBot(gs, Color.BLACK);
        final Selfplay game = new Selfplay(gs, bot, bot2);

        final Move move = bot.getNextMove();
        game.move(move);
        System.err.println(move);

        Assert.assertTrue(gs.endGameDetector.isCheckmate(Color.BLACK));
    }

    @Test
    public void testTurnInto() throws ChessError {
        final GameSettings gs = new GameSettings("r1b1k2r/pppn1ppp/2n5/8/3P4/P1Pq3P/4p1K1/5BNR");
        System.out.println(gs.board);

        final NukeBot bot = NukeBotFactory.getNukeBot(gs, Color.WHITE);
        final NukeBot bot2 = NukeBotFactory.getNukeBot(gs, Color.BLACK);
        final Selfplay game = new Selfplay(gs, bot, bot2);

        game.move(new Move(MoveType.QUIET_MOVE, Cell.parse("a3"), Cell.parse("a4")));

        final Move move = bot2.getNextMove();
        game.move(move);
        System.err.println(move);

        System.out.println(gs.board);

        final Move expected =
                new Move(MoveType.TURN_INTO_ATTACK, Cell.parse("e2"), Cell.parse("f1"));
        expected.turnInto = FigureType.QUEEN;
        Assert.assertEquals(expected, move);
    }

    @Test
    public void testPosition() throws ChessError {
        final GameSettings gs = new GameSettings("1rb3k1/p2p2pp/p1pP4/2P5/1B6/1KP5/5r1P/3R1B1R");
        System.out.println(gs.board);
        gs.board.findKing(Color.BLACK).wasMoved = true;
        gs.board.findKing(Color.WHITE).wasMoved = true;

        final NukeBot bot = NukeBotFactory.getNukeBot(gs, Color.WHITE);
        final NukeBot bot2 = NukeBotFactory.getNukeBot(gs, Color.BLACK);
        final Selfplay game = new Selfplay(gs, bot, bot2);

        final Move move = bot.getNextMove();
        game.move(move);
        System.err.println(move);

        System.out.println(gs.board);
    }
}
