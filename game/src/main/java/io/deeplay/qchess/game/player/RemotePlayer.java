package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;

public class RemotePlayer extends Player {

    private final String sessionToken;

    public RemotePlayer(GameSettings roomSettings, Color color, String sessionToken) {
        super(roomSettings, color);
        this.sessionToken = sessionToken;
    }

    @Override
    public Move getNextMove() {
        throw new UnsupportedOperationException("Удаленный игрок не может ходить");
    }

    public String getSessionToken() {
        return sessionToken;
    }
}