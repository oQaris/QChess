package io.deeplay.qchess.nukebot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.UUID;

public class NukeBot extends RemotePlayer {

    private final SearchFunc searchFunc;

    @Deprecated private int id;

    @Deprecated private int moveCount;
    @Deprecated private double timeToThink;
    @Deprecated private double maxTimeToThink = Double.MIN_VALUE;
    @Deprecated private double minTimeToThink = Double.MAX_VALUE;

    public NukeBot(
            final GameSettings roomSettings, final Color color, final SearchFunc searchFunc) {
        super(roomSettings, color, "nuke-bot-" + UUID.randomUUID(), "nuke-bot");
        this.searchFunc = searchFunc;
    }

    @Deprecated
    public int getId() {
        return id;
    }

    @Deprecated
    public void setId(final int id) {
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
        final long startTime = System.currentTimeMillis();

        final Move move = searchFunc.findBest();

        final double time = (System.currentTimeMillis() - startTime) / 1000.;
        timeToThink += time;
        if (time < minTimeToThink) minTimeToThink = time;
        if (time > maxTimeToThink) maxTimeToThink = time;
        return move;
    }
}