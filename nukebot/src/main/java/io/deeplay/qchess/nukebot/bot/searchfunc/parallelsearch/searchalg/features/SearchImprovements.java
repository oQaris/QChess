package io.deeplay.qchess.nukebot.bot.searchfunc.parallelsearch.searchalg.features;

import io.deeplay.qchess.game.model.Move;
import java.util.Comparator;
import java.util.List;

public abstract class SearchImprovements {

    public static final Comparator<Move> movesPriority =
            (m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel;

    public static void prioritySort(List<Move> allMoves) {
        allMoves.sort(movesPriority);
    }
}
