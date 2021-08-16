package io.deeplay.qchess.core;

import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.CHECKMATE_TO_BLACK;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.CHECKMATE_TO_WHITE;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_PEACE_MOVE_COUNT;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_REPETITIONS;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.STALEMATE_TO_BLACK;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.STALEMATE_TO_WHITE;

import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.slf4j.Logger;

public class ArenaStats {
    private final Logger logger;
    private final Set<Entry<String, Function<RemotePlayer, String>>> optionalLogs;
    private final AtomicInteger countGame = new AtomicInteger(0);
    private final Map<EndGameType, Integer> gameResultsWhite;
    private final Map<EndGameType, Integer> gameResultsBlack;
    private final AtomicLong meanMedianFirst = new AtomicLong(0);
    private final AtomicLong meanMedianSecond = new AtomicLong(0);
    private final AtomicLong maxFirst = new AtomicLong(0);
    private final AtomicLong maxSecond = new AtomicLong(0);
    private long startTime = 0;

    public ArenaStats(
            final Logger logger, final Map<String, Function<RemotePlayer, String>> optionalLogs) {
        this.logger = logger;
        this.optionalLogs = optionalLogs.entrySet(); // Используется только для чтения
        gameResultsWhite =
                new ConcurrentHashMap<>(
                        Map.of(
                                CHECKMATE_TO_BLACK, 0,
                                CHECKMATE_TO_WHITE, 0,
                                STALEMATE_TO_BLACK, 0,
                                STALEMATE_TO_WHITE, 0,
                                DRAW_WITH_NOT_ENOUGH_MATERIAL, 0,
                                DRAW_WITH_PEACE_MOVE_COUNT, 0,
                                DRAW_WITH_REPETITIONS, 0));
        if (gameResultsWhite.size() != EndGameType.values().length - 1)
            throw new IllegalArgumentException("Рассмотрены не все случаи конца игры");
        gameResultsBlack = new ConcurrentHashMap<>(gameResultsWhite);
    }

    /** Вызывается один раз перед началом всех игр */
    void startTracking() {
        startTime = System.currentTimeMillis();
    }

    /** Вызывается после каждой проведённой игры (возможен вызов в многопотоке) */
    void addGameResult(
            final TimeWrapper firstPlayer,
            final TimeWrapper secondPlayer,
            final EndGameType gameResult) {
        if (startTime == 0) throw new IllegalArgumentException("startTracking() не был вызван");

        countGame.incrementAndGet();
        (firstPlayer.getColor() == Color.WHITE ? gameResultsWhite : gameResultsBlack)
                .computeIfPresent(gameResult, (k, v) -> v + 1);

        meanMedianFirst.addAndGet(firstPlayer.getMedian());
        meanMedianSecond.addAndGet(secondPlayer.getMedian());
        maxFirst.set(Math.max(maxFirst.get(), firstPlayer.getMax()));
        maxSecond.set(Math.max(maxSecond.get(), secondPlayer.getMax()));

        logger.info("-=+=+=+=+=- First -=+=+=+=+=+-");
        flushLogs(firstPlayer);
        logger.info("-=+=+=+=+=- Second -=+=+=+=+=-");
        flushLogs(secondPlayer);
    }

    private void flushLogs(final TimeWrapper wrappedPlayer) {
        logger.info("Mean:   {}", wrappedPlayer.getMean());
        logger.info("Median: {}", wrappedPlayer.getMedian());
        logger.info("Mode:   {}", wrappedPlayer.getMode());
        logger.info("Max:    {}", wrappedPlayer.getMax());
        logger.info("Min:    {}", wrappedPlayer.getMin());
        for (final Function<RemotePlayer, String> func : findLogsFunc(wrappedPlayer.getName()))
            logger.info(func.apply(wrappedPlayer.getPlayer()));
    }

    private List<Function<RemotePlayer, String>> findLogsFunc(final String botName) {
        final List<Function<RemotePlayer, String>> res = new ArrayList<>(optionalLogs.size());
        for (final Entry<String, Function<RemotePlayer, String>> pair : optionalLogs)
            if (Pattern.compile(pair.getKey()).matcher(botName).matches()) res.add(pair.getValue());
        return res;
    }

    /**
     * Вызывается обычно в конце всех игр, но можно вызвать и в процессе (запишет в логи результаты
     * на текущий момент)
     */
    synchronized void showResults() {
        final long timeGameInSec = (System.currentTimeMillis() - startTime) / 1000;
        final int drawWithPMC =
                gameResultsBlack.get(DRAW_WITH_PEACE_MOVE_COUNT)
                        + gameResultsWhite.get(DRAW_WITH_PEACE_MOVE_COUNT);
        final int drawWithRep =
                gameResultsBlack.get(DRAW_WITH_REPETITIONS)
                        + gameResultsWhite.get(DRAW_WITH_REPETITIONS);
        final int drawWithNEM =
                gameResultsBlack.get(DRAW_WITH_NOT_ENOUGH_MATERIAL)
                        + gameResultsWhite.get(DRAW_WITH_NOT_ENOUGH_MATERIAL);

        final int passedGame = countGame.get();
        logger.info("{}<---------------------------------------->", System.lineSeparator());
        logger.info(
                "Время на {} игр: {} min {} sec",
                passedGame,
                timeGameInSec / 60,
                timeGameInSec % 60);
        logger.info("Всего ничьих: {}", drawWithPMC + drawWithRep + drawWithNEM);
        logger.info("\tПравило 50-ти ходов:      {}", drawWithPMC);
        logger.info("\tС повторением позиций:    {}", drawWithRep);
        logger.info("\tПри недостатке материала: {}", drawWithNEM);
        logger.info(
                "\tПатов первому игроку:     {}",
                gameResultsBlack.get(STALEMATE_TO_BLACK)
                        + gameResultsWhite.get(STALEMATE_TO_WHITE));
        logger.info(
                "\tПатов второму игроку:     {}",
                gameResultsBlack.get(STALEMATE_TO_WHITE)
                        + gameResultsWhite.get(STALEMATE_TO_BLACK));
        logger.info(
                "Матов первому игроку: {}",
                gameResultsBlack.get(CHECKMATE_TO_BLACK)
                        + gameResultsWhite.get(CHECKMATE_TO_WHITE));
        logger.info(
                "Матов второму игроку: {}",
                gameResultsBlack.get(CHECKMATE_TO_WHITE)
                        + gameResultsWhite.get(CHECKMATE_TO_BLACK));
        logger.info("");
        logger.info(
                "Средне-медианное время хода первого игрока:\t{}",
                meanMedianFirst.get() / passedGame);
        logger.info("Максимальное время хода первого игрока:\t{}", maxFirst.get());
        logger.info(
                "Средне-медианное время хода второго игрока:\t{}",
                meanMedianSecond.get() / passedGame);
        logger.info("Максимальное время хода второго игрока:\t{}", maxSecond.get());
    }
}
