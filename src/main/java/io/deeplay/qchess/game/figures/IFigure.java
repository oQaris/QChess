package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.model.Cell;
import java.util.Set;

public interface IFigure {

    /**
     * @return все варианты для перемещения фигуры, не выходящие за границы доски, учитывая уже занятые клетки.
     */
    Set<Cell> getAllMovePositions();
}
