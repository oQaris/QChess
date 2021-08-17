package io.deeplay.qchess.core;

import io.deeplay.qchess.game.player.BotFactory.SpecificFactory;
import java.io.IOException;

public class Tournament {
    final int numGamesWithEach;
    final SpecificFactory[] factories;

    public Tournament(final int numGamesWithEach, final SpecificFactory... factories) {
        this.numGamesWithEach = numGamesWithEach;
        this.factories = factories;
    }

    public void runMegaBattle() throws IOException, InterruptedException {
        for (int i = 0; i < factories.length - 1; ++i)
            for (int j = i + 1; j < factories.length; ++j)
                new Arena(factories[i], factories[j], numGamesWithEach).battle();
    }
}
