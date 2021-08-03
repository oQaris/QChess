package io.deeplay.qchess.nnnbot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;

@FunctionalInterface
public interface EvaluationFunc {
    double getEvaluation(GameSettings gs, Color color) throws ChessError;
}
