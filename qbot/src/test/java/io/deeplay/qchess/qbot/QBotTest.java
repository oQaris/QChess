package io.deeplay.qchess.qbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.qbot.strategy.MatrixStrategy;
import io.deeplay.qchess.qbot.strategy.SimpleStrategy;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.List;
import org.junit.jupiter.api.Test;

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

    @Test
    void testFirstStep() throws ChessError {
        final GameSettings game = new GameSettings(BoardFilling.STANDARD);
        final QNegamaxTTBot bot1 = new QNegamaxTTBot(game, Color.WHITE, 3);
        final QMinimaxBot bot2 = new QMinimaxBot(game, Color.WHITE, 3);
        assertEquals(bot1.getTopMoves(), bot2.getTopMoves());

        game.moveSystem.move(new Move(MoveType.QUIET_MOVE, Cell.parse("e2"), Cell.parse("e4")));
        game.moveSystem.move(new Move(MoveType.QUIET_MOVE, Cell.parse("b8"), Cell.parse("c6")));
        assertEquals(bot1.getTopMoves(), bot2.getTopMoves());
    }

    @Test
    void testQBotStalemate1Step() throws ChessError, ChessException {
        // мат ладьёй за один ход
        final GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("a8")));
        roomSettings.board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("c7")));
        roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("d5")));
        System.out.println(roomSettings.board);

        final QNegamaxTTBot bot = new QNegamaxTTBot(roomSettings, Color.BLACK, 1);
        final List<Move> moves = bot.getTopMoves();

        assertEquals(1, moves.size());
        final Move bestMove = moves.get(0);

        assertEquals(new Move(MoveType.QUIET_MOVE, Cell.parse("d5"), Cell.parse("d8")), bestMove);
    }

    @Test
    void testQBotStalemate2Step() throws ChessError, ChessException {
        // тут можно поставить мат в 2 хода
        final GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("c4")));
        roomSettings.board.setFigure(new King(Color.BLACK, Cell.parse("b8")));
        roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("h5")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("e7")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("c6")));

        final QNegamaxTTBot bot =
                new QNegamaxTTBot(roomSettings, Color.WHITE, 3, new SimpleStrategy());

        final List<Move> moves1 = bot.getTopMoves();

        final Move expected = new Move(MoveType.QUIET_MOVE, Cell.parse("c6"), Cell.parse("h6"));
        assertTrue(moves1.contains(expected));

        roomSettings.board.moveFigure(expected);
        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, Cell.parse("b8"), Cell.parse("a8")));

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
    void testEvaluateBoard() throws ChessException, ChessError {
        final Board board = new GameSettings(Board.BoardFilling.STANDARD).board;
        final Strategy strategy = new MatrixStrategy();

        assertEquals(0, strategy.evaluateBoard(board));

        board.removeFigure(new Cell(1, 0));
        assertEquals(52, strategy.evaluateBoard(board));

        board.removeFigure(new Cell(3, 7));
        assertEquals(-127, strategy.evaluateBoard(board));
    }
}
