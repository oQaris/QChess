package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MoveSystemIsCheckTest {

    private Board board;
    private MoveSystem ms;

    @Before
    public void setUp() {
        board = new Board();
        ms = new MoveSystem(board);
    }

    @Test(expected = ChessError.class)
    public void testIsCheck_zeroFigures() throws ChessError {
        Assert.assertFalse(ms.isCheck(true));
        Assert.assertFalse(ms.isCheck(false));
    }
}
