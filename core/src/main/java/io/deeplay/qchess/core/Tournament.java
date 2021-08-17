package io.deeplay.qchess.core;

import io.deeplay.qchess.game.player.BotFactory.SpecificFactory;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.MDC;

public class Tournament {
    final int numGamesWithEach;
    final SpecificFactory[] factories;

    public Tournament(final int numGamesWithEach, final SpecificFactory... factories) {
        this.numGamesWithEach = numGamesWithEach;
        this.factories = factories;
    }

    public void runMegaBattle() throws IOException, InterruptedException {
        final int countProc = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(countProc);

        for (int i = 0; i < factories.length - 1; ++i)
            for (int j = i + 1; j < factories.length; ++j) {
                final int finalI = i;
                final int finalJ = j;
                executor.execute(
                        () -> {
                            MDC.put(
                                    "tournament",
                                    factories[finalI].getBotName()
                                            + "_VS_"
                                            + factories[finalJ].getBotName());
                            try {
                                new Arena(factories[finalI], factories[finalJ], numGamesWithEach)
                                        .battle();
                            } catch (final InterruptedException | IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
}
