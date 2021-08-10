package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.model.Move;

@FunctionalInterface
public interface Updater {
    void updateResult(Move move, int estimation);
}
