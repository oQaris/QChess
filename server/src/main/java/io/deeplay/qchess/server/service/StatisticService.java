package io.deeplay.qchess.server.service;

import io.deeplay.qchess.game.model.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class StatisticService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticService.class);

    private StatisticService() {}

    /** Записывает результат игры в файл */
    public static synchronized void writeEndGameStats(
            final int roomId, final int gameId, final String result) {
        MDC.put("path", roomId + "/" + "games_result");
        // logger.info("{} - {}", gameId, result);
        logger.info(result);
    }

    /** Записывает очередной ход игры в файл */
    public static synchronized void writeMoveStats(
            final int roomId, final int gameId, final Move move) {
        MDC.put("path", roomId + "/" + "moves_" + gameId);
        logger.info("{}", move);
    }
}
