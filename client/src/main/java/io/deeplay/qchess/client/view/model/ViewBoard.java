package io.deeplay.qchess.client.view.model;

public class ViewBoard {
    public final int SIZE = 8;
    private final ViewFigure[][] cells = new ViewFigure[SIZE][SIZE];

    public void setFigure(int column, int row, ViewFigure figure) {
        cells[row][column] = figure;
    }

    public ViewFigure getFigure(int column, int row) {
        return cells[row][column];
    }
}
