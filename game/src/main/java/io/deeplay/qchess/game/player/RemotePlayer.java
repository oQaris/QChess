package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;

public class RemotePlayer extends Player {

    private final String sessionToken;

    public RemotePlayer(
            final GameSettings roomSettings, final Color color, final String sessionToken) {
        super(roomSettings, color);
        this.sessionToken = sessionToken;
    }

    @Override
    public Move getNextMove() throws ChessError {
        throw new UnsupportedOperationException("Удаленный игрок не может ходить");
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.REMOTE_PLAYER;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}
