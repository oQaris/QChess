package io.deeplay.qchess.core;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.game.player.RemotePlayer;
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
    private static final int COUNT = 10;
    private static final Map<String, Function<RemotePlayer, String>> optionalLogs =
            Map.of(
                    "Minimax*", // Тут пишется регулярка для имени игроков
                    // А тут задаётся функция, которая вызывается после каждой партии.
                    // Должна возвращать строку, выводимую в логах
                    bot -> "Optional Log"
                    // Пример:
                    /*"Обращений к ТТ: " + ((QNegamaxTTBot) bot).countFindingTT*/ );
    private static final ArenaStats stats = new ArenaStats(logger, optionalLogs);

    /** Тут задаётся Первый игрок */
    public static RemotePlayer newFirstPlayer(final GameSettings gs, final Color myColor) {
        return new RandomBot(gs, myColor);
    }

    /** Тут задаётся Второй игрок */
    public static RemotePlayer newSecondPlayer(final GameSettings gs, final Color myColor) {
        return new RandomBot(gs, myColor);
    }

    public void battle() throws InterruptedException {
        logger.info(
                "Запущена битва ботов:\n{}\n{}",
                newFirstPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE).getName(),
                newSecondPlayer(new GameSettings(BoardFilling.EMPTY), Color.WHITE).getName());

        final int countProc = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(Math.min(countProc, COUNT));

        stats.startTracking();
        for (int i = 1; i <= COUNT; i++) {
            executor.execute(
                    i % 2 == 0 ? new Game(Color.WHITE, stats) : new Game(Color.BLACK, stats));
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        stats.showResults();
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
            logger.info("\nGames completed: " + (doneTasks.incrementAndGet()) + "/" + COUNT);
            final EndGameType gameResult = gs.endGameDetector.getGameResult();
            logger.info("fp: {}, {}", myColor, gameResult);

            stats.addGameResult(firstPlayer, secondPlayer, gameResult);
        }
    }
}
