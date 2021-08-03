package io.deeplay.qchess.lobot.strategy;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.lobot.FigureService;

public class FiguresCostSumEvaluateStrategy implements EvaluateStrategy {

    @Override
    public int evaluateBoard(Board board, Color color) {
        int result = 0;
        for (Figure figure : board.getAllFigures()) {
            int coef = (figure.getColor() == color) ? 1 : -1;
            int val = FigureService.convertFigureToVal(figure);
            result += (coef * val);
        }
        return result;
    }
}
