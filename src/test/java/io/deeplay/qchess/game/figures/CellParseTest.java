package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.model.Cell;
import org.junit.Assert;
import org.junit.Test;

public class CellParseTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSetParse1() {
        Cell.parse("a0");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetParse2() {
        Cell.parse("j1");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetParse3() {
        Cell.parse("а1");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetParse4() {
        Cell.parse("а11");
        Assert.fail();
    }
}