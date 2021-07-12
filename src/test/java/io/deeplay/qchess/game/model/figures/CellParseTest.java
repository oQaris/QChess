package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.model.Cell;
import org.junit.Assert;
import org.junit.Test;

public class CellParseTest {
    @Test(expected = IllegalArgumentException.class)
    public void testParseFail1() {
        Cell.parse("a0");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFail2() {
        Cell.parse("j1");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFail3() {
        Cell.parse("а1");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFail4() {
        Cell.parse("а11");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFail5() {
        Cell.parse("");
        Assert.fail();
    }
}
