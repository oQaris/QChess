package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.BotFactory;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.reflections8.Reflections;

public class QBotFactory implements BotFactory {

    private <T> Class<? extends T> getSubClassWithMinLD(
            final Class<T> superClass, final String name) {
        final LevenshteinDistance distance = new LevenshteinDistance();
        return new Reflections()
                .getSubTypesOf(superClass).stream()
                        .min(
                                Comparator.comparingInt(
                                        cls ->
                                                distance.apply(
                                                        cls.getSimpleName()
                                                                .toLowerCase(Locale.ROOT),
                                                        name)))
                        .get();
    }

    @Override
    public RemotePlayer newBot(final String name, final GameSettings gs, final Color myColor) {
        final String[] tokens = name.split("-");
        if (tokens.length == 0)
            throw new IllegalArgumentException("Должен быть указан хотя бы тип бота");
        final String type = tokens[0].toLowerCase(Locale.ROOT);

        final QBot.Builder builder;
        try {
            builder =
                    (QBot.Builder)
                            Arrays.stream(getSubClassWithMinLD(QBot.class, type).getClasses())
                                    .filter(bld -> bld.getSuperclass() == QBot.Builder.class)
                                    .findFirst()
                                    .get()
                                    .getConstructor(GameSettings.class, Color.class)
                                    .newInstance(gs, myColor);
        } catch (final InstantiationException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "В классе-наследнике должен быть реализован QBot.Builder");
        }

        for (final String str : tokens) {
            final String token = str.strip().toLowerCase(Locale.ROOT);
            if (isNumber(token)) builder.setDepth(Integer.parseInt(token));
            else if (token.equals("tt")) builder.withTT();
            else {
                try {
                    builder.setStrategy(
                            getSubClassWithMinLD(Strategy.class, token)
                                    .getDeclaredConstructor()
                                    .newInstance());

                } catch (final InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException e) {
                    throw new IllegalArgumentException("Ошибка при создании бота");
                }
            }
        }
        final QBot bot = builder.build();
        /*System.out.println(bot.getClass());
        System.out.println(bot.depth);
        System.out.println(bot.strategy);
        System.out.println(
                bot instanceof QNegamaxTTBot ? ((QNegamaxTTBot) bot).ttEnable : "not_supported");*/
        return bot;
    }

    private boolean isNumber(final String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }
}
