package io.deeplay.qchess.game.logics;

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

    @Test
    public void testIsCheck_zeroFigures() {
        Assert.assertFalse(ms.isCheck(true));
        Assert.assertFalse(ms.isCheck(false));
    }
}
