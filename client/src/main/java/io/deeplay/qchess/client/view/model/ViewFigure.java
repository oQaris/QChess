package io.deeplay.qchess.client.view.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViewFigure that = (ViewFigure) o;
        return Objects.equals(color, that.color) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
