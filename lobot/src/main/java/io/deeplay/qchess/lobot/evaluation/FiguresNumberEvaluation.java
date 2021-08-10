package io.deeplay.qchess.lobot.evaluation;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;

public class FiguresNumberEvaluation implements Evaluation {

    @Override
    public int evaluateBoard(Board board, Color color) {
        int result = 0;
        for (Figure figure : board.getAllFigures()) {
            int coef = (figure.getColor() == color) ? 1 : -1;
            result += coef;
        }
        return result;
    }
}
