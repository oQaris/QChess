package io.deeplay.qchess.nnnbot.bot.searchfunc.features;

import io.deeplay.qchess.game.model.Move;
import java.util.Comparator;
import java.util.List;

public abstract class SearchImprovements {

    private static final Comparator<Move> movesPriority =
            (m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel;

    public static void prioritySort(List<Move> allMoves) {
        allMoves.sort(movesPriority);
    }
}