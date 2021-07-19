package io.deeplay.qchess.client.view.gui;

public class ViewFigure {
    private final String color;
    private final String type;

    public ViewFigure(String color, String type) {
        this.color = color;
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public String getType() {
        return type;
    }
}
