package io.deeplay.qchess.nnnbot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;

@FunctionalInterface
public interface EvaluationFunc {

    double MAX_ESTIMATION = Double.MAX_VALUE;
    double MIN_ESTIMATION = -Double.MAX_VALUE;

    /** @return значение, которое необходимо увеличивать игроку с цветом color */
    double getHeuristics(GameSettings gs, Color myColor) throws ChessError;
}
