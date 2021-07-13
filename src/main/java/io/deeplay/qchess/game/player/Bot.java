package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;
import java.util.EnumMap;
import java.util.Map;

public abstract class Bot extends Player {
    protected static final Map<TypeFigure, Integer> grades = Bot.preparedGrades();

    protected Bot(GameSettings roomSettings, Color color) {
        super(roomSettings, color);
    }

    private static Map<TypeFigure, Integer> preparedGrades() {
        Map<TypeFigure, Integer> res = new EnumMap<>(TypeFigure.class);
        res.put(TypeFigure.PAWN, 1);
        res.put(TypeFigure.KNIGHT, 3);
        res.put(TypeFigure.BISHOP, 3);
        res.put(TypeFigure.ROOK, 5);
        res.put(TypeFigure.QUEEN, 9);
        res.put(TypeFigure.KING, 100);
        return res;
    }

    protected void turnIntoInQueen(Move move) {
        if (move.getMoveType() == MoveType.TURN_INTO)
            move.setTurnInto(new Queen(color, move.getTo()));
    }
}
