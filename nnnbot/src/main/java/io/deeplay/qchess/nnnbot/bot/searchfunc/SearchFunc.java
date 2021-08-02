package io.deeplay.qchess.nnnbot.bot.searchfunc;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;

@FunctionalInterface
public interface SearchFunc {
    Move findBest() throws ChessError;
}
