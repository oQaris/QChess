package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;

public class RemotePlayer extends Player {

    private final String sessionToken;
    private final String name;

    public RemotePlayer(
            final GameSettings roomSettings,
            final Color color,
            final String sessionToken,
            String name) {
        super(roomSettings, color);
        this.sessionToken = sessionToken;
        this.name = name;
    }

    @Override
    public Move getNextMove() throws ChessError {
        throw new UnsupportedOperationException("Удаленный игрок не может ходить");
    }

    public String getName() {
        return name;
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.GUI_PLAYER;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}
