package io.deeplay.qchess.game.model.figures;

import java.io.Serializable;

public enum FigureType implements Serializable {
    PAWN(0),
    KNIGHT(1),
    BISHOP(2),
    ROOK(3),
    QUEEN(4),
    KING(5);

    public static final String[] nameOfTypeNumber = {
        "PAWN", "KNIGHT", "BISHOP", "ROOK", "QUEEN", "KING"
    };

    /** id тип пустой клетки на доске (совместим с PeSTO) */
    public static final int EMPTY_TYPE = 12;

    /** id тип фигуры. Нужен для быстрого вычисления состояния доски */
    public final int type;

    FigureType(final int type) {
        this.type = type;
    }
}
