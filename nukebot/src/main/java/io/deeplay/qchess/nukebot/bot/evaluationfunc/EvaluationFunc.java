package io.deeplay.qchess.nukebot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;

@FunctionalInterface
public interface EvaluationFunc {

    int MAX_ESTIMATION = Integer.MAX_VALUE;
    int MIN_ESTIMATION = -Integer.MAX_VALUE;

    int PAWN_COST = 100;
    int QUARTER_PAWN_COST = PAWN_COST / 4;

    int QUEEN_COST = 900;
    int DOUBLE_QUEEN_MINUS_PAWN_COST = 2 * (QUEEN_COST - PAWN_COST);

    /** @return значение, которое необходимо увеличивать игроку с цветом color */
    int getHeuristics(GameSettings gs, Color myColor) throws ChessError;
}
