package io.deeplay.qchess.nnnbot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;

public class FigurePositionsEvaluation implements EvaluationFunc {

    @Override
    public double getEvaluation(GameSettings gs, Color color) throws ChessError {
        return gs.board.getFigureCount(color);
        // return gs.moveSystem.getAllCorrectMoves(color).size() / 432.;
    }
}
