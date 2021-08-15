package io.deeplay.qchess.client.view.model;

public class ViewBoard {
    public final int SIZE = 8;
    private final ViewFigure[][] cells = new ViewFigure[SIZE][SIZE];

    public void setFigure(final int column, final int row, final ViewFigure figure) {
        cells[row][column] = figure;
    }

    public ViewFigure getFigure(final int column, final int row) {
        return cells[row][column];
    }
}
