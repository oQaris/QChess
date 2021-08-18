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
    public static final String SEPARATOR = "-";

    private <T> Class<? extends T> getSubClassWithMinLD(
            final Class<T> superClass, final String name) {
        final LevenshteinDistance distance = new LevenshteinDistance();
        return new Reflections()
                .getSubTypesOf(superClass).stream()
                        .min(
                                Comparator.comparingInt(
                                        cls ->
                                                distance.apply(
                                                        normalize(cls.getSimpleName()), name)))
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "У класса "
                                                        + superClass.getName()
                                                        + " отсутствуют наследники"));
    }

    @Override
    public RemotePlayer newBot(final String name, final GameSettings gs, final Color myColor) {
        final String[] tokens = name.split(SEPARATOR);
        if (tokens.length == 0)
            throw new IllegalArgumentException("Должен быть указан хотя бы тип бота");

        final String botType = normalize(tokens[0]);
        final QBot.Builder builder = parseAndCreateBuilder(botType, gs, myColor);

        for (final String str : tokens) {
            final String token = normalize(str);

            // Число устанавливает глубину
            if (isNumber(token)) builder.setDepth(Integer.parseInt(token));
            // "tt" включает таблицы транспонирования
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
                    throw new IllegalArgumentException("Ошибка при установке стратегии");
                }
            }
        }
        return builder.build();
    }

    private QBot.Builder parseAndCreateBuilder(
            final String botType, final GameSettings gs, final Color myColor) {
        try {
            return (QBot.Builder)
                    Arrays.stream(getSubClassWithMinLD(QBot.class, botType).getClasses())
                            .filter(bld -> bld.getSuperclass() == QBot.Builder.class)
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "В классе-наследнике должен быть реализован QBot.Builder"))
                            .getConstructor(GameSettings.class, Color.class)
                            .newInstance(gs, myColor);
        } catch (final InstantiationException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            throw new IllegalArgumentException("Ошибка при создании бота");
        }
    }

    private String normalize(final String origin) {
        return origin.strip().toLowerCase(Locale.ROOT);
    }

    private boolean isNumber(final String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }
}
