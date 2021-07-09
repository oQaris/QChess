package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HistoryTest {

    @Ignore
    @Test
    public void testBoardNotation() throws ChessError, ChessException {
        Board board = new Board();
        MoveSystem ms = new MoveSystem(board);
        board.initBoard(ms, Board.BoardFilling.STANDARD);
        History history = new History(board);
        Assert.assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", history.addRecord());
    }
}
