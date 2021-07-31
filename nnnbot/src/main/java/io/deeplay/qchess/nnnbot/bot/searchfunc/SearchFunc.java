package io.deeplay.qchess.nnnbot.bot.searchfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;

@FunctionalInterface
public interface SearchFunc {
    Move findBest(GameSettings gs, Color color, EvaluationFunc evaluationFunc) throws ChessError;
}
