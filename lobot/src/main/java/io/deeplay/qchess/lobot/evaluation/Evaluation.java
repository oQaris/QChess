package io.deeplay.qchess.lobot.evaluation;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;

public interface Evaluation {

    int evaluateBoard(Board board, Color color);
}
