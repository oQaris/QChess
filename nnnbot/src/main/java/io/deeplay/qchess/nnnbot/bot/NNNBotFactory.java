package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class NNNBotFactory {

    private static final Logger logger = LoggerFactory.getLogger(NNNBotFactory.class);

    private static int lastId;

    public static synchronized NNNBot getNNNBot(String time, GameSettings gs, Color color) {
        MDC.put("time", time);

        NNNBot nnnBot = new NNNBot(gs, color);

        nnnBot.setId(++lastId);

        int testCacheSize = NNNBot.MAX_DEPTH * 500; // * new Random().nextInt(50);
        if (lastId % 2 == 0) {
            nnnBot.includeCache();
            nnnBot.setCacheSize(testCacheSize);
            logger.info("Создан бот #{} с размером кеша: {}", lastId, testCacheSize);
        } else {
            logger.info("Создан бот #{} без кеша", lastId);
        }

        return nnnBot;
    }
}
