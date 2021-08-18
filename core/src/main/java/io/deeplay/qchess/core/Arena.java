package io.deeplay.qchess.core;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.BotFactory.SpecificFactory;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.QNegamaxTTBot;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Arena {
    private static final Logger logger = LoggerFactory.getLogger(Arena.class);
    private static final Map<String, Function<RemotePlayer, String>> optionalLogs =
            Map.of(
                    "NegaMaxBot",
                    bot -> "Обращений к ТТ: " + ((QNegamaxTTBot) bot).getCountFindingTT());
    private final int countGame;
    private final SpecificFactory firstFactory;
    private final SpecificFactory secondFactory;
    private final ArenaStats stats = new ArenaStats(logger, optionalLogs);
    private final static RatingELO rating = new RatingELO();

    static {
        try {
            rating.pullELO();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public Arena(
            final SpecificFactory firstFactory,
            final SpecificFactory secondFactory,
            final int countGame) {
        this.firstFactory = firstFactory;
        this.secondFactory = secondFactory;
        this.countGame = countGame;
    }

    public void battle() throws InterruptedException, IOException {
        logger.info("Запущена битва ботов:");
        logger.info(firstFactory.getBotName());
        logger.info(secondFactory.getBotName());

        /*final int countProc = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor =
                Executors.newFixedThreadPool(Math.min(countProc, countGame));*/

        stats.startTracking();
        for (int i = 1; i <= countGame; i++) {
            // executor.execute(
            (i % 2 == 0
                            ? new Game(
                                    firstFactory,
                                    secondFactory,
                                    countGame,
                                    rating,
                                    Color.WHITE,
                                    stats)
                            : new Game(
                                    firstFactory,
                                    secondFactory,
                                    countGame,
                                    rating,
                                    Color.BLACK,
                                    stats))
                    .run();
        }
        // executor.shutdown();
        // executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        stats.showResults();
        logger.info("{}", rating);
        //rating.saveELO();
    }

    private static class Game implements Runnable {
        private static final AtomicInteger curTask = new AtomicInteger(0);
        private static final AtomicInteger doneTasks = new AtomicInteger(0);
        private final Color myColor;
        private final ArenaStats stats;
        private final SpecificFactory firstFactory;
        private final SpecificFactory secondFactory;
        private final int countGame;
        private final RatingELO rating;

        public Game(
                final SpecificFactory firstFactory,
                final SpecificFactory secondFactory,
                final int countGame,
                final RatingELO rating,
                final Color myColor,
                final ArenaStats stats) {
            this.firstFactory = firstFactory;
            this.secondFactory = secondFactory;
            this.countGame = countGame;
            this.rating = rating;
            this.myColor = myColor;
            this.stats = stats;
        }

        @Override
        public void run() {
            final GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            final TimeWrapper firstPlayer = new TimeWrapper(firstFactory.create(gs, myColor));
            final TimeWrapper secondPlayer =
                    new TimeWrapper(secondFactory.create(gs, myColor.inverse()));
            try {
                MDC.put(
                        "tournament",
                        firstFactory.getBotName()
                                + "_VS_"
                                + secondFactory.getBotName()
                                + "/"
                                + curTask.incrementAndGet());
                final Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (final ChessError e) {
                logger.error("Ошибка в игре: {}", e.getMessage());
            }
            logger.info("");
            logger.info("Games completed: {}/{}", doneTasks.incrementAndGet(), countGame);
            final EndGameType gameResult = gs.endGameDetector.getGameResult();
            logger.info("fp: {}, {}", myColor, gameResult);

            stats.addGameResult(firstPlayer, secondPlayer, gameResult);

            final double firstPlayerFactor = getFactor(firstPlayer.getColor(), gameResult);
            rating.updateELO(firstPlayer.getName(), secondPlayer.getName(), firstPlayerFactor);
            stats.showResults();
            try {
                rating.saveELO();
            } catch (final IOException e) {
                e.printStackTrace();
            }
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
