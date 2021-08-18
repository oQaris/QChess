package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.BotFactory;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;

public class QBotFactory implements BotFactory {

    @Override
    public RemotePlayer newBot(final String name, final GameSettings gs, final Color myColor) {
        return new QNegamaxTTBot.Builder(gs, myColor)
                .setStrategy(new PestoStrategy())
                .setDepth(3)
                .withTT()
                .build();
    }
}
