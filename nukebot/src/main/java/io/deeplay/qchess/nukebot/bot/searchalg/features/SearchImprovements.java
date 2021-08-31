package io.deeplay.qchess.nukebot.bot.searchalg.features;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Comparator;
import java.util.List;

public abstract class SearchImprovements {

    public static final Comparator<Move> movesPriority =
            Comparator.<Move>comparingInt(m -> m.getMoveType().importantLevel).reversed();

    public static void prioritySort(final List<Move> allMoves) {
        allMoves.sort(movesPriority);
    }

    /** Сортирует ходы на основе эвристики истории, бабочки и MVV-LVA */
    public static void allSorts(final Board board, final List<Move> allMoves) {
        final Comparator<Move> MVV_LVA =
                Comparator.comparingInt(
                        m -> {
                            final Figure from = board.getFigureUgly(m.getFrom());
                            final Figure to = board.getFigureUgly(m.getTo());
                            return to == null
                                    ? FigureType.EMPTY_TYPE
                                    : from.figureType.type - to.figureType.type;
                        });
        allMoves.sort(
                (m1, m2) -> {
                    final int s1 = MVV_LVA.compare(m1, m2);
                    if (s1 != 0) return s1;
                    return movesPriority.compare(m1, m2);
                });
    }
}
