package io.deeplay.qchess.lobot.evaluation;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.lobot.FigureService;

public class FiguresCostSumEvaluation implements Evaluation {

    @Override
    public int evaluateBoard(final GameSettings gameSettings, final Color color) {
        int result = 0;
        for (Figure figure : gameSettings.board.getAllFigures()) {
            int coef = (figure.getColor() == color) ? 1 : -1;
            int val = FigureService.convertFigureToVal(figure);
            result += (coef * val);
        }
        return result;
    }
}
