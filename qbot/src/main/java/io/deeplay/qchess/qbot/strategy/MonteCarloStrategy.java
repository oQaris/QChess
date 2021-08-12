package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Стратегия оценки доски методом Монте-Карло. */
public class MonteCarloStrategy extends Strategy {
    public static final int COUNT_GAMES = 100;
    private static final Logger logger = LoggerFactory.getLogger(MonteCarloStrategy.class);

    @Override
    public int evaluateBoard(final Board board) {
        final AtomicInteger wins = new AtomicInteger(0);
        final ExecutorService gamePool = Executors.newCachedThreadPool();
        for (int i = 0; i < COUNT_GAMES; i++) {
            gamePool.execute(
                    () -> {
                        final GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
                        final Player firstPlayer = new RandomBot(gs, Color.WHITE);
                        final Player secondPlayer = new RandomBot(gs, Color.BLACK);
                        try {
                            final Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                            game.run();
                        } catch (final ChessError e) {
                            logger.error(
                                    "Ошибка при выполнении игры в стратегии Монте-Карло: {}",
                                    e.getLocalizedMessage());
                        }
                        wins.addAndGet(
                                switch (gs.endGameDetector.getGameResult()) {
                                    case EndGameType.CHECKMATE_TO_BLACK -> 1;
                                    case EndGameType.CHECKMATE_TO_WHITE -> -1;
                                    default -> 0; // ничьи и паты
                                });
                    });
        }
        gamePool.shutdown();
        try {
            gamePool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            logger.error(
                    "Оценка Монте-Карло считалась больше секунды: {}", e.getLocalizedMessage());
        }
        return wins.get();
    }
}
