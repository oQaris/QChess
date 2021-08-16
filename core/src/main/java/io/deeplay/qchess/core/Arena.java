package io.deeplay.qchess.core;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.QMinimaxBot;
import io.deeplay.qchess.qbot.QNegamaxTTBot;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Arena {
    private static final Logger logger = LoggerFactory.getLogger(Arena.class);
    private static final int COUNT = 100;
    private static final Map<String, Function<RemotePlayer, String>> optionalLogs =
            Map.of("Nega*", bot -> "Обращений к ТТ: " + ((QNegamaxTTBot) bot).getCountFindingTT());
    private static final ArenaStats stats = new ArenaStats(logger, optionalLogs);
    private static final RatingELO rating = new RatingELO();

    /** Тут задаётся Первый игрок */
    public static RemotePlayer newFirstPlayer(final GameSettings gs, final Color myColor) {
        return new QNegamaxTTBot(gs, myColor);
    }

    /** Тут задаётся Второй игрок */
    public static RemotePlayer newSecondPlayer(final GameSettings gs, final Color myColor) {
        return new QMinimaxBot(gs, myColor);
    }

    public void battle() throws InterruptedException, IOException {
        logger.info("Запущена битва ботов:");
        logger.info(newFirstPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE).getName());
        logger.info(newSecondPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE).getName());

        final int countProc = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(Math.min(countProc, COUNT));
        rating.pullELO();

        stats.startTracking();
        for (int i = 1; i <= COUNT; i++) {
            executor.execute(
                    i % 2 == 0 ? new Game(Color.WHITE, stats) : new Game(Color.BLACK, stats));
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        stats.showResults();
        logger.info("{}", rating);
        rating.saveELO();
    }

    private static class Game implements Runnable {
        private static final AtomicInteger curTask = new AtomicInteger(0);
        private static final AtomicInteger doneTasks = new AtomicInteger(0);
        private final Color myColor;
        private final ArenaStats stats;

        public Game(final Color myColor, final ArenaStats stats) {
            this.myColor = myColor;
            this.stats = stats;
        }

        @Override
        public void run() {
            final GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            final TimeWrapper firstPlayer = new TimeWrapper(newFirstPlayer(gs, myColor));
            final TimeWrapper secondPlayer =
                    new TimeWrapper(newSecondPlayer(gs, myColor.inverse()));
            try {
                MDC.put("game", Integer.toString(curTask.incrementAndGet()));
                final Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (final ChessError e) {
                logger.error("Ошибка в игре: {}", e.getMessage());
            }
            logger.info("");
            logger.info("Games completed: {}/{}", doneTasks.incrementAndGet(), COUNT);
            final EndGameType gameResult = gs.endGameDetector.getGameResult();
            logger.info("fp: {}, {}", myColor, gameResult);

            stats.addGameResult(firstPlayer, secondPlayer, gameResult);

            final double firstPlayerFactor = getFactor(firstPlayer.getColor(), gameResult);
            rating.updateELO(firstPlayer.getName(), secondPlayer.getName(), firstPlayerFactor);
        }

        private double getFactor(final Color firstPlayerColor, final EndGameType result) {
            return result
                            == (firstPlayerColor == Color.WHITE
                                    ? EndGameType.CHECKMATE_TO_BLACK
                                    : EndGameType.CHECKMATE_TO_WHITE)
                    ? 1
                    : (result
                                    == (firstPlayerColor == Color.WHITE
                                            ? EndGameType.CHECKMATE_TO_WHITE
                                            : EndGameType.CHECKMATE_TO_BLACK)
                            ? 0
                            : 0.5);
        }
    }
}
