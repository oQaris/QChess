package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;

public class RemotePlayer extends Player {

    private final int playerID;

    public RemotePlayer(GameSettings roomSettings, Color color, int playerID) {
        super(roomSettings, color);
        this.playerID = playerID;
    }

    @Override
    public Move getNextMove() {
        throw new UnsupportedOperationException("Удаленный игрок не может ходить");
    }

    public int getPlayerID() {
        return playerID;
    }
}
