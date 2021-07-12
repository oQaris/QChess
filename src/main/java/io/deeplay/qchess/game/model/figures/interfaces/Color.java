package io.deeplay.qchess.game.model.figures.interfaces;

public enum Color {
    BLACK, WHITE;

    public Color inverse() {
        return this == WHITE ? BLACK : WHITE;
    }
}
