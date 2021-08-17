package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.BotFactory;
import io.deeplay.qchess.game.player.RemotePlayer;

public class LobotFactory implements BotFactory {

    @Override
    public RemotePlayer newBot(final String name, final GameSettings gs, final Color myColor) {
        // todo учитывать name
        return new LoBot(gs, myColor);
    }
}
