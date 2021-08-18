package io.deeplay.qchess.game.model.figures;

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

    /** @return id тип пустой клетки на доске (совместим с PeSTO) */
    public static int getEmptyValue() {
        return 12;
    }
}
