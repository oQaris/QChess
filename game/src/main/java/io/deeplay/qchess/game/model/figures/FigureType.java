package io.deeplay.qchess.game.model.figures;

public enum FigureType {
    BISHOP((byte) 1),
    KING((byte) 2),
    KNIGHT((byte) 3),
    PAWN((byte) 4),
    QUEEN((byte) 5),
    ROOK((byte) 6);

    public static final String[] nameOfTypeNumber = {
        "NULL", "BISHOP", "KING", "KNIGHT", "PAWN", "QUEEN", "ROOK"
    };

    /** Нужен для быстрого вычисления состояния доски */
    public final byte type;

    FigureType(byte type) {
        this.type = type;
    }
}
