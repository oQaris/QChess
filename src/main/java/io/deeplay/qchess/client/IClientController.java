package io.deeplay.qchess.client;

import io.deeplay.qchess.gui.ViewCell;
import io.deeplay.qchess.gui.ViewFigure;
import java.util.Set;

/** Преобразует и отправляет данные */
public interface IClientController {

    Set<ViewCell> getAllMoves(int row, int column);

    boolean checkFigure(int row, int column);

    boolean checkFigure(int row, int column, boolean isWhite);

    ViewFigure getFigure(int row, int column);
}
