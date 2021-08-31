package io.deeplay.qchess.nukebot.bot.searchfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;

public abstract class SearchFunc<T extends SearchAlgorithm<? super T>> extends SearchAlgorithm<T>
        implements ResultUpdater {

    public static final long TIME_TO_MOVE = 5000;

    protected SearchFunc(
            final Move mainMove,
            final GameSettings gs,
            final int maxDepth,
            final int moveVersion,
            final SearchFunc<?> searchFunc) {
        super(searchFunc, mainMove, moveVersion, gs, maxDepth);
    }

    protected SearchFunc(final T alg) {
        super(alg);
    }

    /** @return true, если время на обдумывание хода вышло */
    public static boolean timesUp(final long startTimeMillis) {
        return System.currentTimeMillis() - startTimeMillis > TIME_TO_MOVE;
    }

    public abstract Move findBest() throws ChessError;
}
