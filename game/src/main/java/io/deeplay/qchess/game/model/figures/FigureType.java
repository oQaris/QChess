package io.deeplay.qchess.game.model.figures;

public enum FigureType {
    BISHOP((byte) 0),
    KING((byte) 1),
    KNIGHT((byte) 2),
    PAWN((byte) 3),
    QUEEN((byte) 4),
    ROOK((byte) 5);

    public static final String[] nameOfTypeNumber = {
        "BISHOP", "KING", "KNIGHT", "PAWN", "QUEEN", "ROOK"
    };

    /** Нужен для быстрого вычисления состояния доски */
    public final byte type;

    FigureType(byte type) {
        this.type = type;
    }
}
