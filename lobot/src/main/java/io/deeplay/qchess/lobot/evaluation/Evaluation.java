package io.deeplay.qchess.lobot.evaluation;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;

public interface Evaluation {

    int evaluateBoard(final GameSettings gameSettings, final Color color);
}
