package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.RemotePlayer;

public class QForkJoinBot extends RemotePlayer {

    // todo  В разработке
    public QForkJoinBot(GameSettings roomSettings, Color color, String sessionToken) {
        super(roomSettings, color, sessionToken);
    }
}
