package io.deeplay.qchess.core;

import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.ClientGUI;
import io.deeplay.qchess.core.config.ArenaSettings;
import io.deeplay.qchess.core.config.ConfigException;
import io.deeplay.qchess.game.player.AttackBot.AttackBotFactory;
import io.deeplay.qchess.game.player.BotFactory.SpecificFactory;
import io.deeplay.qchess.lobot.LobotFactory;
import io.deeplay.qchess.nukebot.bot.NukeBotFactory;
import io.deeplay.qchess.qbot.QBotFactory;
import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(final String[] args) throws IOException, ConfigException {
        System.out.println(
                "Введите \"s\", чтобы запустить сервер или \"c\", чтобы запустить клиент");
        System.out.println("Ещё можно ввести \"a\", чтобы лицезреть бесконечную мощь Qbot'a");

        final String input;
        if (args.length > 0) input = args[0];
        else input = new Scanner(System.in).nextLine().strip();

        switch (input) {
                // Сервер
            case "s", "-s", "server" -> {
                final IServerView view = new ServerConsole();
                view.startView();
                view.close();
            }
                // Клиент
            case "c", "-c", "client" -> {
                final IClientView view = new ClientGUI();
                view.startView();
                view.close();
            }
                // Aрена
            case "a", "-a", "arena" -> {
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
                        new Tournament(
                                conf.getNumberGame(), qbotFactory, lobotFactory, nukebotFactory);
                try {
                    tournament.runMegaBattle();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            default -> System.out.println("Некорректная команда");
        }
    }
}
