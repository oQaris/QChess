package io.deeplay.qchess.client;

import io.deeplay.qchess.gui.ViewCell;
import io.deeplay.qchess.gui.ViewFigure;
import java.util.Set;

/** Получает данные из игры и InputTrafficHandler и преобразует в данные для IClientView */
public class LocalPlayerController implements IClientController {

    @Override
    public Set<ViewCell> getAllMoves(int row, int column) {
        return null;
    }

    @Override
    public boolean checkFigure(int row, int column) {
        return false;
    }

    @Override
    public boolean checkFigure(int row, int column, boolean isWhite) {
        return false;
    }

    @Override
    public ViewFigure getFigure(int row, int column) {
        return null;
    }
}
