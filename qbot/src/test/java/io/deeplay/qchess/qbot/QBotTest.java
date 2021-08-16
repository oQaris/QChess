package io.deeplay.qchess.qbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.qchess.game.GameSettings;
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

    QBot getTestedBot(final GameSettings game, final Color myColor, final int depth) {
        return new QNegamaxTTBot(game, myColor, depth, new SimpleStrategy());
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
        setKings(Cell.parse("a8"), Cell.parse("h8"), roomSettings);
        roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("c7")));
        roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("d5")));
        System.out.println(roomSettings.board);

        final QBot bot = getTestedBot(roomSettings, Color.BLACK, 1);
        final List<Move> moves = bot.getTopMoves();

        assertEquals(1, moves.size());
        final Move bestMove = moves.get(0);

        assertEquals(new Move(MoveType.QUIET_MOVE, Cell.parse("d5"), Cell.parse("d8")), bestMove);
    }

    @Test
    void testQBotStalemate2Step() throws ChessError, ChessException {
        // тут можно поставить мат в 2 хода
        final GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        setKings(Cell.parse("c4"), Cell.parse("b8"), roomSettings);
        roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("h5")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("e7")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("c6")));

        final QBot bot = getTestedBot(roomSettings, Color.WHITE, 3);

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
    void testQBotCheckMate2StepAny() throws ChessError, ChessException {
        // тут можно поставить 2 разных мата в 2 хода
        final GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.history.setMinBoardStateToSave(QBot.MAX_DEPTH);
        setKings(Cell.parse("e1"), Cell.parse("b1"), roomSettings);
        roomSettings.board.setFigure(new Queen(Color.WHITE, Cell.parse("a3")));
        roomSettings.board.setFigure(new Knight(Color.WHITE, Cell.parse("a2")));
        roomSettings.board.setFigure(new Pawn(Color.WHITE, Cell.parse("f2")));
        roomSettings.board.setFigure(new Pawn(Color.WHITE, Cell.parse("g2")));
        roomSettings.board.setFigure(new Pawn(Color.WHITE, Cell.parse("h3")));
        roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("b2")));
        roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("b3")));

        final QBot bot = getTestedBot(roomSettings, Color.WHITE, 5);

        roomSettings.board.moveFigure(bot.getNextMove());
        final List<Move> blackMoves = roomSettings.moveSystem.getAllPreparedMoves(Color.BLACK);

        for (final Move move : blackMoves) {
            roomSettings.moveSystem.move(move);
            roomSettings.moveSystem.move(bot.getNextMove());
            assertEquals(
                    EndGameType.CHECKMATE_TO_BLACK,
                    roomSettings.endGameDetector.updateEndGameStatus());
            roomSettings.moveSystem.undoMove();
            roomSettings.moveSystem.undoMove();
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
