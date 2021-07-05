package io.deeplay.qchess.game;

import io.deeplay.qchess.game.figures.IFigure;

public class Cell {

    private IFigure figure;

    /**
     * @return фигура или null, если нет
     */
    public IFigure getFigure() {
        return figure;
    }

    /**
     * @return true если клетку бьет черная фигура, false иначе
     */
    public boolean blackHits() {
        // TODO: реализовать рейкастом (или перебором будет быстрее?)
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @return true если клетку бьет белая фигура, false иначе
     */
    public boolean whiteHits() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
