package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.exceptions.ChessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CellTest {

    @ParameterizedTest
    @ValueSource(strings = {"a0", "j1", /* это русская буква: */ "а1", "a11", "", "-"})
    public void testIncorrectParse(String cellStr) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Cell.parse(cellStr));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a1", "g3", "f8", "h8", "a8", "h1"})
    public void testToString(String cellStr) throws ChessException {
        Assertions.assertEquals(cellStr, Cell.parse(cellStr).toString());
    }
}
