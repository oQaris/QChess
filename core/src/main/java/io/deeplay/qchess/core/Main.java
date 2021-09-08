package io.deeplay.qchess.core;

import ch.qos.logback.classic.util.ContextInitializer;
import io.deeplay.qchess.core.config.ArenaSettings;
import io.deeplay.qchess.core.config.ConfigException;
import io.deeplay.qchess.game.player.AttackBot.AttackBotFactory;
import io.deeplay.qchess.game.player.BotFactory.SpecificFactory;
import io.deeplay.qchess.game.player.RandomBot.RandomBotFactory;
import io.deeplay.qchess.lobot.LobotFactory;
import io.deeplay.qchess.nukebot.bot.NukeBotFactory;
import io.deeplay.qchess.qbot.QBotFactory;
import java.io.IOException;

public class Main {
    public static void main(final String[] args) throws IOException, ConfigException {
        final ArenaSettings conf = new ArenaSettings();
        final String logback = conf.getLogback() + ArenaSettings.DEFAULT_LOGBACK_NAME;
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logback);

        final SpecificFactory qbotFactory =
                new SpecificFactory(new QBotFactory(), conf.getQbotName());
        final SpecificFactory lobotFactory =
                new SpecificFactory(new LobotFactory(), conf.getLobotName());
        final SpecificFactory nukebotFactory =
                new SpecificFactory(new NukeBotFactory(), conf.getNukebotName());
        final SpecificFactory randombotFactory =
                new SpecificFactory(new RandomBotFactory(), "Рандомный_Бот");
        final SpecificFactory attackbotFactory =
                new SpecificFactory(new AttackBotFactory(), "Атакующий_Бот");

        final Tournament tournament =
                new Tournament(
                        conf.getNumberGame(),
                        qbotFactory,
                        lobotFactory,
                        nukebotFactory,
                        randombotFactory,
                        attackbotFactory);
        try {
            tournament.runMegaBattle();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
