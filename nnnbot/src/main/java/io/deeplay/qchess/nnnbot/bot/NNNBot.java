package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.SearchFunc;
import java.util.UUID;

public class NNNBot extends RemotePlayer {

    private final SearchFunc searchFunc;
    private final EvaluationFunc evaluationFunc;

    @Deprecated private int id;

    @Deprecated private int moveCount;
    @Deprecated private double timeToThink;
    @Deprecated private double maxTimeToThink = Double.MIN_VALUE;
    @Deprecated private double minTimeToThink = Double.MAX_VALUE;

    public NNNBot(
            GameSettings roomSettings,
            Color color,
            SearchFunc searchFunc,
            EvaluationFunc evaluationFunc) {
        super(roomSettings, color, "n-nn-bot-" + UUID.randomUUID());
        this.searchFunc = searchFunc;
        this.evaluationFunc = evaluationFunc;
    }

    @Deprecated
    public int getId() {
        return id;
    }

    @Deprecated
    public void setId(int id) {
        this.id = id;
    }

    @Deprecated
    public double getAverageTimeToThink() {
        return timeToThink / moveCount;
    }

    @Deprecated
    public int getMoveCount() {
        return moveCount;
    }

    @Deprecated
    public double getMaxTimeToThink() {
        return maxTimeToThink;
    }

    @Deprecated
    public double getMinTimeToThink() {
        return minTimeToThink;
    }

    @Override
    public Move getNextMove() throws ChessError {
        ++moveCount;
        long startTime = System.currentTimeMillis();

        Move move = searchFunc.findBest(roomSettings, color, evaluationFunc);

        double time = (System.currentTimeMillis() - startTime) / 1000.;
        timeToThink += time;
        if (time < minTimeToThink) minTimeToThink = time;
        if (time > maxTimeToThink) maxTimeToThink = time;
        return move;
    }
}
