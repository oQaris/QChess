package io.deeplay.qchess.game.model;

public enum Color {
    BLACK,
    WHITE;

    public Color inverse() {
        return this == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}
