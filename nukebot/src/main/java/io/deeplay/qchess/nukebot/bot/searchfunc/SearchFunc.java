package io.deeplay.qchess.nukebot.bot.searchfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;

public abstract class SearchFunc {

    public static final long TIME_TO_MOVE = 5000;

    public final EvaluationFunc evaluationFunc;
    public final Color myColor;
    public final int maxDepth;

    protected final GameSettings gs;

    protected SearchFunc(
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        this.evaluationFunc = evaluationFunc;
        myColor = color;
        this.maxDepth = maxDepth;
        this.gs = gs;
    }

    public abstract Move findBest() throws ChessError;
}
