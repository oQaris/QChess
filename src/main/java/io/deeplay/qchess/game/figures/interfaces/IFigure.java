package io.deeplay.qchess.game.figures.interfaces;

import io.deeplay.qchess.game.model.Cell;
import java.util.Set;

public interface IFigure {

    /**
     * @return все варианты для перемещения фигуры, не выходящие за границы доски, учитывая уже занятые клетки.
     */
    Set<Cell> getAllMovePositions();

    /**
     * @return true - если цвет фигуры белый, false - если чёрный
     */
    boolean isWhite();

    /**
     * @return текущее положение фигуры на доске
     */
    Cell getCurrentPosition();
}
