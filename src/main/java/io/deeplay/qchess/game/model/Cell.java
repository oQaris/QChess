package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.figures.IFigure;

public class Cell {

    private IFigure figure;
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Cell o = (Cell) obj;
        return x == o.x && y == o.y;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }

    /**
     * @return фигура или null, если нет
     */
    public IFigure getFigure() {
        return figure;
    }

    public void setFigure(IFigure figure) {
        this.figure = figure;
    }

    /**
     * @return true если клетка свободная
     */
    public boolean isEmpty() {
        return figure == null;
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
