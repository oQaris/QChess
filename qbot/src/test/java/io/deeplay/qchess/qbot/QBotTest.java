package io.deeplay.qchess.qbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.qbot.strategy.IStrategy;
import io.deeplay.qchess.qbot.strategy.MatrixStrategy;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
import io.deeplay.qchess.qbot.strategy.SimpleStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class QBotTest {
    private void setKings(Cell whitePos, Cell blackPos, GameSettings gs) throws ChessException {
        final King kingB = new King(Color.BLACK, blackPos);
        kingB.setWasMoved(true);
        gs.board.setFigure(kingB);
        final King kingW = new King(Color.WHITE, whitePos);
        kingW.setWasMoved(true);
        gs.board.setFigure(kingW);
    }

    @Test
    void testQBotGame() throws ChessError, ChessException {
        Color myColor = Color.WHITE;
        for (int i = 0; i < 1; i++) {
            GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            QBot firstPlayer = new QBot(gs, myColor, 1, new PestoStrategy());
            Player secondPlayer = new RandomBot(gs, myColor.inverse());
            try {
                System.out.println();
                System.out.println(firstPlayer.getTopMoves());
                System.out.println();
                /*Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();*/
            } catch (ChessError e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testQBotGradeNextMove() throws ChessError, ChessException {
        GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
        roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
        roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));
        roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(1, 2)));
        setKings(new Cell(7, 7), new Cell(5, 5), roomSettings);

        QBot bot = new QBot(roomSettings, Color.WHITE, 2, new MatrixStrategy());
        int grade = bot.minimaxRoot(2, true);
        Move bestMove = bot.getNextMove();

        // -102
        assertEquals(-89, grade);
        assertEquals(new Move(MoveType.ATTACK, new Cell(1, 2), new Cell(2, 1)), bestMove);
    }

    @Test
    void testQBotMM() throws ChessError, ChessException {
        GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
        roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
        roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));
        setKings(new Cell(7, 7), new Cell(5, 5), roomSettings);

        QBot bot = new QBot(roomSettings, Color.WHITE, 1);

        roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(2, 3)));
        int grade = bot.minimaxRoot(1, true);
        assertEquals(-89, grade);

        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, new Cell(2, 3), new Cell(0, 3)));
        grade = bot.minimaxRoot(1, true);
        assertEquals(-79, grade);

        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, new Cell(0, 3), new Cell(0, 1)));
        grade = bot.minimaxRoot(1, true);
        assertEquals(-85, grade);

        // таким образом, это лучший ход
        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, new Cell(0, 1), new Cell(2, 1)));
        grade = bot.minimaxRoot(1, true);
        assertEquals(-25, grade);
    }

    @Test
    void testQBotAttack() throws ChessError, ChessException {
        GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.board.setFigure(new Rook(Color.BLACK, new Cell(0, 0)));
        roomSettings.board.setFigure(new Knight(Color.BLACK, new Cell(0, 1)));
        roomSettings.board.setFigure(new Bishop(Color.BLACK, new Cell(2, 1)));
        roomSettings.board.setFigure(new Bishop(Color.WHITE, new Cell(1, 2)));
        setKings(new Cell(7, 7), new Cell(5, 5), roomSettings);

        QBot bot = new QBot(roomSettings, Color.WHITE, 1);
        int grade = bot.minimaxRoot(1, true);
        Move bestMove = bot.getNextMove();

        assertEquals(-79, grade);
        assertEquals(new Move(MoveType.ATTACK, new Cell(1, 2), new Cell(2, 1)), bestMove);
    }

    @Test
    void testQBotStalemate1Step() throws ChessError, ChessException {
        // мат ладьёй за один ход
        GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("a8")));
        roomSettings.board.setFigure(new King(Color.BLACK, Cell.parse("h8")));
        roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("c7")));
        roomSettings.board.setFigure(new Rook(Color.BLACK, Cell.parse("d5")));
        System.out.println(roomSettings.board);

        QBot bot = new QBot(roomSettings, Color.BLACK, 3);
        List<Move> moves = bot.getTopMoves();

        assertEquals(1, moves.size());
        Move bestMove = moves.get(0);

        assertEquals(new Move(MoveType.QUIET_MOVE, Cell.parse("d5"), Cell.parse("d8")), bestMove);
    }

    @Test
    void testQBotStalemate2Step() throws ChessError, ChessException {
        // тут можно поставить мат в 2 хода
        GameSettings roomSettings = new GameSettings(Board.BoardFilling.EMPTY);
        roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("c4")));
        roomSettings.board.setFigure(new King(Color.BLACK, Cell.parse("b8")));
        roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("h5")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("e7")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("c6")));

        QBot bot = new QBot(roomSettings, Color.WHITE, 4, new SimpleStrategy());

        List<Move> moves1 = bot.getTopMoves();

        Move expected = new Move(MoveType.QUIET_MOVE, Cell.parse("c6"), Cell.parse("h6"));
        assertTrue(moves1.contains(expected));

        roomSettings.board.moveFigure(expected);
        roomSettings.board.moveFigure(
                new Move(MoveType.QUIET_MOVE, Cell.parse("b8"), Cell.parse("a8")));

        List<Move> moves2 = bot.getTopMoves();
        assertEquals(1, moves2.size());

        assertEquals(
                new Move(MoveType.QUIET_MOVE, Cell.parse("h6"), Cell.parse("h8")), moves2.get(0));
    }

    @Test
    void testQBotCheckMate2Step() throws ChessError, ChessException {
        // тут можно поставить пат в 1 ход, или мат в 2 хода
        // todo
        /*GameSettings roomSettings = new GameSettings(BoardFilling.EMPTY);
        roomSettings.board.setFigure(new King(Color.WHITE, Cell.parse("c4")));
        roomSettings.board.setFigure(new King(Color.BLACK, Cell.parse("b8")));
        roomSettings.board.setFigure(new Pawn(Color.BLACK, Cell.parse("h5")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("e7")));
        roomSettings.board.setFigure(new Rook(Color.WHITE, Cell.parse("c6")));*/
    }

    @Test
    void testEvaluateBoard() throws ChessException, ChessError {
        Board board = new GameSettings(Board.BoardFilling.STANDARD).board;
        IStrategy strategy = new MatrixStrategy();

        assertEquals(0, strategy.evaluateBoard(board));

        board.removeFigure(new Cell(1, 0));
        assertEquals(52, strategy.evaluateBoard(board));

        board.removeFigure(new Cell(3, 7));
        assertEquals(-127, strategy.evaluateBoard(board));
    }
}
