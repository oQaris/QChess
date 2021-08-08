package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.model.Color;

public enum FigureType {
    BISHOP(1),
    KING(2),
    KNIGHT(3),
    PAWN(4),
    QUEEN(5),
    ROOK(6);

    public static final String[] nameOfTypeNumber = {
        "NULL", "BISHOP", "KING", "KNIGHT", "PAWN", "QUEEN", "ROOK"
    };

    /** Нужен для быстрого вычисления состояния доски */
    public final int type;

    FigureType(int type) {
        this.type = type;
    }

    /** Фигуры будут записываться в доску как их тип + этот коэффициент */
    public static int getColorCoeff(Color color) {
        return color == Color.WHITE ? 0 : 100;
    }
}
