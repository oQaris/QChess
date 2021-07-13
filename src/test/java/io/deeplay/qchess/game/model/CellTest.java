package io.deeplay.qchess.game.model;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CellTest {

    private final String cellStr;

    public CellTest(String cellStr) {
        this.cellStr = cellStr;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> dataForTest() {
        return Arrays.asList(new Object[][] {{"a0"}, {"j1"}, {"а1"}, {"а11"}, {""}});
    }

    @Test(expected = IllegalArgumentException.class)
    public void paramTest() {
        Cell.parse(cellStr);
        Assert.fail();
    }
}
