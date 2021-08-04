package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.RemotePlayer;

public class QExpectimaxBot extends RemotePlayer {

    public QExpectimaxBot(GameSettings roomSettings, Color color, String sessionToken) {
        super(roomSettings, color, sessionToken);
    }
}
