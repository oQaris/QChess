package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;

public interface BotFactory {
    /**
     * Создаёт бота по переданной строке
     *
     * @param name Строка, на основе которой создаётся бот
     * @param gs Настройки игры
     * @param myColor Цвет создаваемого бота
     * @return Новый бот
     */
    RemotePlayer newBot(final String name, final GameSettings gs, final Color myColor);

    class SpecificFactory {
        private final BotFactory factory;
        private final String botName;

        public SpecificFactory(final BotFactory factory, final String botName) {
            this.factory = factory;
            this.botName = botName;
        }

        public BotFactory getFactory() {
            return factory;
        }

        public String getBotName() {
            return botName;
        }

        public RemotePlayer create(final GameSettings gs, final Color myColor) {
            return factory.newBot(botName, gs, myColor);
        }
    }
}
