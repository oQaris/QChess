package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class QBot extends RemotePlayer {
    public static final int MAX_DEPTH = 100;
    protected final Strategy strategy;
    protected final int depth;

    public QBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy,
            final String name) {
        super(roomSettings, color, "q-bot-" + UUID.randomUUID(), name);
        this.strategy = strategy;
        depth = searchDepth;
        if (depth < 0 || depth > MAX_DEPTH) {
            throw new IllegalArgumentException("Некорректная глубина поиска!");
        }
        history.setMinBoardStateToSave(MAX_DEPTH);
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = getTopMoves();
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    abstract List<Move> getTopMoves() throws ChessError;
}
