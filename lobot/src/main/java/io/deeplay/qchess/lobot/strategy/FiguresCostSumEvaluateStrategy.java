package io.deeplay.qchess.lobot.strategy;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.lobot.FigureService;

public class FiguresCostSumEvaluateStrategy implements EvaluateStrategy {

    @Override
    public int evaluateBoard(final Board board, final Color color) {
        int result = 0;
        for (final Figure figure : board.getAllFigures()) {
            final int coef = (figure.getColor() == color) ? 1 : -1;
            final int val = FigureService.convertFigureToVal(figure);
            result += (coef * val);
        }
        return result;
    }
}
