package io.deeplay.qchess.core;

import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.CHECKMATE_TO_BLACK;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.CHECKMATE_TO_WHITE;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_PEACE_MOVE_COUNT;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.DRAW_WITH_REPETITIONS;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.STALEMATE_TO_BLACK;
import static io.deeplay.qchess.game.logics.EndGameDetector.EndGameType.STALEMATE_TO_WHITE;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nnnbot.bot.NNNBotFactory;
import io.deeplay.qchess.qbot.QBot;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Arena {
    private static final Logger logger = LoggerFactory.getLogger(Arena.class);
    private static final int COUNT = 10;
    private static final Map<EndGameType, Integer> gameResultsWhite;
    private static final Map<EndGameType, Integer> gameResultsBlack;
    private static final AtomicLong meanMedianFirst = new AtomicLong(0);
    private static final AtomicLong meanMedianSecond = new AtomicLong(0);
    private static final AtomicLong maxFirst = new AtomicLong(0);
    private static final AtomicLong maxSecond = new AtomicLong(0);

    /** Тут задаётся Первый игрок */
    public static RemotePlayer newFirstPlayer(final GameSettings gs, final Color myColor) {
        return new QBot(gs, myColor, 3);
    }

    /** Тут задаётся Второй игрок */
    public static RemotePlayer newSecondPlayer(final GameSettings gs, final Color myColor) {
        return NNNBotFactory.getNNNBot(gs, myColor);
    }

    static {
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
        gameResultsBlack = new ConcurrentHashMap<>(gameResultsWhite);
    }

    public void battle() throws InterruptedException {
        logger.info(
                "Запущена битва ботов:\n{}\n{}",
                // todo исправить костыль
                newFirstPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE).getSessionToken(),
                newSecondPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE)
                        .getSessionToken());

        final int countProc = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(Math.min(countProc, COUNT));
        final long startTime = System.currentTimeMillis();

        for (int i = 1; i <= COUNT; i++) {
            executor.execute(
                    i % 2 == 0
                            ? new Game(Color.WHITE, gameResultsWhite)
                            : new Game(Color.BLACK, gameResultsBlack));
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        showResults((System.currentTimeMillis() - startTime) / 1000);
    }

    void showResults(final long timeGameInSec) {
        final int drawWithPMC =
                gameResultsBlack.get(DRAW_WITH_PEACE_MOVE_COUNT)
                        + gameResultsWhite.get(DRAW_WITH_PEACE_MOVE_COUNT);
        final int drawWithRep =
                gameResultsBlack.get(DRAW_WITH_REPETITIONS)
                        + gameResultsWhite.get(DRAW_WITH_REPETITIONS);
        final int drawWithNEM =
                gameResultsBlack.get(DRAW_WITH_NOT_ENOUGH_MATERIAL)
                        + gameResultsWhite.get(DRAW_WITH_NOT_ENOUGH_MATERIAL);

        logger.info("\n<------------------------------------------------------>");
        logger.info("\nTime: {} min {} sec", timeGameInSec / 60, timeGameInSec % 60);
        logger.info(
                "\nВсего ничьих: {}\n"
                        + "\tПравило 50-ти ходов:      {}\n"
                        + "\tС повторением позиций:    {}\n"
                        + "\tПри недостатке материала: {}\n"
                        + "Мат первому игроку: {}\n"
                        + "Мат второму игроку: {}\n"
                        + "Пат первому игроку: {}\n"
                        + "Пат второму игроку: {}",
                drawWithPMC + drawWithRep + drawWithNEM,
                drawWithPMC,
                drawWithRep,
                drawWithNEM,
                gameResultsBlack.get(CHECKMATE_TO_BLACK) + gameResultsWhite.get(CHECKMATE_TO_WHITE),
                gameResultsBlack.get(CHECKMATE_TO_WHITE) + gameResultsWhite.get(CHECKMATE_TO_BLACK),
                gameResultsBlack.get(STALEMATE_TO_BLACK) + gameResultsWhite.get(STALEMATE_TO_WHITE),
                gameResultsBlack.get(STALEMATE_TO_WHITE)
                        + gameResultsWhite.get(STALEMATE_TO_BLACK));
        logger.info("Средне-медианное время хода первого игрока: {}", meanMedianFirst.get());
        logger.info("Максимальное время хода первого игрока:     {}", maxFirst.get());
        logger.info("Средне-медианное время хода второго игрока: {}", meanMedianSecond.get());
        logger.info("Максимальное время хода второго игрока:     {}", maxSecond.get());
    }

    private static class Game implements Runnable {
        private static final AtomicInteger curTask = new AtomicInteger(0);
        private static final AtomicInteger doneTasks = new AtomicInteger(0);
        private final Color myColor;
        private final Map<EndGameType, Integer> resultsOutput;

        public Game(final Color myColor, final Map<EndGameType, Integer> resultsOutput) {
            this.myColor = myColor;
            this.resultsOutput = resultsOutput;
        }

        @Override
        public void run() {
            final GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            final TimeWrapper firstPlayer = new TimeWrapper(newFirstPlayer(gs, myColor));
            final TimeWrapper secondPlayer = new TimeWrapper(newSecondPlayer(gs, myColor.inverse()));
            try {
                MDC.put("game", Integer.toString(curTask.incrementAndGet()));
                Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                logger.error("Ошибка в игре: {}", e.getLocalizedMessage());
            }
            resultsOutput.computeIfPresent(gs.endGameDetector.getGameResult(), (k, v) -> v + 1);
            System.out.println("Games completed: " + (doneTasks.incrementAndGet()) + "/" + COUNT);

            meanMedianFirst.addAndGet(firstPlayer.getMedian() / COUNT);
            meanMedianSecond.addAndGet(secondPlayer.getMedian() / COUNT);
            maxFirst.set(Math.max(maxFirst.get(), firstPlayer.getMax()));
            maxSecond.set(Math.max(maxSecond.get(), secondPlayer.getMax()));

            logger.info("== First ==");
            logger.info("Mean:   {}", firstPlayer.getMean());
            logger.info("Median: {}", firstPlayer.getMedian());
            logger.info("Mode:   {}", firstPlayer.getMode());
            logger.info("Max:    {}", firstPlayer.getMax());
            logger.info("Min:    {}", firstPlayer.getMin());
            logger.info("== Second ==");
            logger.info("Mean:   {}", secondPlayer.getMean());
            logger.info("Median: {}", secondPlayer.getMedian());
            logger.info("Mode:   {}", secondPlayer.getMode());
            logger.info("Max:    {}", secondPlayer.getMax());
            logger.info("Min:    {}", secondPlayer.getMin());
        }
    }
}
