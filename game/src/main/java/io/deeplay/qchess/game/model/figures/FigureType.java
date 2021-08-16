package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.model.Color;

public enum FigureType {
    PAWN(0),
    KNIGHT(1),
    BISHOP(2),
    ROOK(3),
    QUEEN(4),
    KING(5);

    public static final String[] nameOfTypeNumber = {
        "PAWN", "KNIGHT", "BISHOP", "ROOK", "QUEEN", "KING"
    };

    /** Нужен для быстрого вычисления состояния доски */
    public final int type;

    FigureType(final int type) {
        this.type = type;
    }

    /** Фигуры будут записываться в доску этим значением */
    public int getPestoValue(final Color color) {
        return 2 * type + (color == Color.WHITE ? 0 : 1);
    }

    public static int getEmptyPestoValue() {
        return 12;
    }
}
