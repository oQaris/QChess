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
import io.deeplay.qchess.qbot.QMinimaxBot;
import io.deeplay.qchess.qbot.TimeWrapper;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
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
    private static final Map<EndGameType, Integer> gameResultsWhite = initGameResults();
    private static final Map<EndGameType, Integer> gameResultsBlack = initGameResults();
    private static final AtomicLong meanMedianFirst = new AtomicLong(0);
    private static final AtomicLong meanMedianSecond = new AtomicLong(0);
    private static final AtomicLong maxFirst = new AtomicLong(0);
    private static final AtomicLong maxSecond = new AtomicLong(0);

    // Тут задаётся Первый игрок
    public static RemotePlayer newFirstPlayer(GameSettings gs, Color myColor) {
        return new QMinimaxBot(gs, myColor, 3, new PestoStrategy());
    }

    // Тут задаётся Второй игрок
    public static RemotePlayer newSecondPlayer(GameSettings gs, Color myColor) {
        return NNNBotFactory.getNNNBot(gs, myColor);
    }

    private static Map<EndGameType, Integer> initGameResults() {
        return new ConcurrentHashMap<>(
                Map.of(
                        CHECKMATE_TO_BLACK, 0,
                        CHECKMATE_TO_WHITE, 0,
                        STALEMATE_TO_BLACK, 0,
                        STALEMATE_TO_WHITE, 0,
                        DRAW_WITH_NOT_ENOUGH_MATERIAL, 0,
                        DRAW_WITH_PEACE_MOVE_COUNT, 0,
                        DRAW_WITH_REPETITIONS, 0));
    }

    public void battle() {
        logger.warn(
                "Запущена битва ботов:\n{}\n{}",
                // todo исправить костыль
                newFirstPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE).getSessionToken(),
                newSecondPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE)
                        .getSessionToken());

        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= COUNT; i++) {
            executor.execute(
                    i % 2 == 0
                            ? new Game(Color.WHITE, gameResultsWhite)
                            : new Game(Color.BLACK, gameResultsBlack));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            logger.error("Что то не то с потоками");
            throw new RuntimeException(e);
        }
        showResults((System.currentTimeMillis() - startTime) / 1000);
    }

    void showResults(long timeGame) {
        int d1 =
                gameResultsBlack.get(DRAW_WITH_PEACE_MOVE_COUNT)
                        + gameResultsWhite.get(DRAW_WITH_PEACE_MOVE_COUNT);
        int d2 =
                gameResultsBlack.get(DRAW_WITH_REPETITIONS)
                        + gameResultsWhite.get(DRAW_WITH_REPETITIONS);
        int d3 =
                gameResultsBlack.get(DRAW_WITH_NOT_ENOUGH_MATERIAL)
                        + gameResultsWhite.get(DRAW_WITH_NOT_ENOUGH_MATERIAL);
        int d = d1 + d2 + d3;

        logger.warn("\n<------------------------------------------------------>");
        logger.warn("\nTime: {} min {} sec", timeGame / 60, timeGame % 60);
        logger.warn(
                "\nВсего ничьих: {}\n"
                        + "\tПравило 50-ти ходов:      {}\n"
                        + "\tС повторением позиций:    {}\n"
                        + "\tПри недостатке материала: {}\n"
                        + "Мат первому игроку: {}\n"
                        + "Мат второму игроку: {}\n"
                        + "Пат первому игроку: {}\n"
                        + "Пат второму игроку: {}",
                d,
                d1,
                d2,
                d3,
                gameResultsBlack.get(CHECKMATE_TO_BLACK) + gameResultsWhite.get(CHECKMATE_TO_WHITE),
                gameResultsBlack.get(CHECKMATE_TO_WHITE) + gameResultsWhite.get(CHECKMATE_TO_BLACK),
                gameResultsBlack.get(STALEMATE_TO_BLACK) + gameResultsWhite.get(STALEMATE_TO_WHITE),
                gameResultsBlack.get(STALEMATE_TO_WHITE)
                        + gameResultsWhite.get(STALEMATE_TO_BLACK));
        logger.warn("Средне-медианное время хода первого игрока: {}", meanMedianFirst.get());
        logger.warn("Максимальное время хода первого игрока:     {}", maxFirst.get());
        logger.warn("Средне-медианное время хода второго игрока: {}", meanMedianSecond.get());
        logger.warn("Максимальное время хода второго игрока:     {}", maxSecond.get());
    }

    private static class Game implements Runnable {
        private static final AtomicInteger curTask = new AtomicInteger(0);
        private static final AtomicInteger doneTasks = new AtomicInteger(0);
        private final Color myColor;
        private final Map<EndGameType, Integer> resultsOutput;

        public Game(Color myColor, Map<EndGameType, Integer> resultsOutput) {
            this.myColor = myColor;
            this.resultsOutput = resultsOutput;
        }

        @Override
        public void run() {
            GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            TimeWrapper firstPlayer = new TimeWrapper(newFirstPlayer(gs, myColor));
            TimeWrapper secondPlayer = new TimeWrapper(newSecondPlayer(gs, myColor.inverse()));
            try {
                MDC.put("game", Integer.toString(curTask.incrementAndGet()));
                Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
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
            // firstPlayer.printGraph();
            logger.info("== Second ==");
            logger.info("Mean:   {}", secondPlayer.getMean());
            logger.info("Median: {}", secondPlayer.getMedian());
            logger.info("Mode:   {}", secondPlayer.getMode());
            logger.info("Max:    {}", secondPlayer.getMax());
            logger.info("Min:    {}", secondPlayer.getMin());
            // secondPlayer.printGraph();
        }
    }
}
