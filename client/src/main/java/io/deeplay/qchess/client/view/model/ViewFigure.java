package io.deeplay.qchess.client.view.model;

public class ViewFigure {
    private final String color;
    private final ViewFigureType type;

    public ViewFigure(final String color, final ViewFigureType type) {
        this.color = color;
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public ViewFigureType getType() {
        return type;
    }
}
