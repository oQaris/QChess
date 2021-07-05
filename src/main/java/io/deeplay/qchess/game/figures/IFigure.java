package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.Cell;
import java.util.Set;

public interface IFigure {

    Set<Cell> getAllMovePositions();
}
