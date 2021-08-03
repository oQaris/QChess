package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;

public interface EvaluateStrategy {
    int evaluateBoard(Board board, Color color);
}
