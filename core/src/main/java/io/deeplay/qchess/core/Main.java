package io.deeplay.qchess.core;

import io.deeplay.qchess.core.config.ArenaSettings;
import io.deeplay.qchess.core.config.ConfigException;
import io.deeplay.qchess.game.player.BotFactory.SpecificFactory;
import io.deeplay.qchess.lobot.LobotFactory;
import io.deeplay.qchess.nukebot.bot.NukeBotFactory;
import io.deeplay.qchess.qbot.QBotFactory;
import java.io.IOException;

public class Main {
    public static void main(final String[] args) throws IOException, ConfigException {
        final ArenaSettings conf = new ArenaSettings();
        // System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, conf.getLogback());

        // todo Надо реализовать свои
        final SpecificFactory qbotFactory =
                new SpecificFactory(new QBotFactory(), conf.getQbotName());
        final SpecificFactory lobotFactory =
                new SpecificFactory(new LobotFactory(), conf.getLobotName());
        final SpecificFactory nukebotFactory =
                new SpecificFactory(new NukeBotFactory(), conf.getNukebotName());

        final Tournament tournament =
                new Tournament(conf.getNumberGame(), qbotFactory, lobotFactory, nukebotFactory);
        try {
            tournament.runMegaBattle();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
