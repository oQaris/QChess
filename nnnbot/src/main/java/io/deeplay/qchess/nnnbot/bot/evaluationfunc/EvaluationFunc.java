package io.deeplay.qchess.nnnbot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;

@FunctionalInterface
public interface EvaluationFunc {

    int MAX_ESTIMATION = Integer.MAX_VALUE - 100;
    int MIN_ESTIMATION = 100 - Integer.MAX_VALUE;

    /** @return значение, которое необходимо увеличивать игроку с цветом color */
    int getHeuristics(GameSettings gs, Color myColor) throws ChessError;
}
