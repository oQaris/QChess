package io.deeplay.qchess.nukebot.bot.features;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Comparator;
import java.util.List;

public class MoveSorter {

    public static final Comparator<Move> movesPriority =
            Comparator.<Move>comparingInt(m -> m.getMoveType().importantLevel).reversed();
    private final Comparator<Move> MVVmLVA;

    public MoveSorter(final Board board) {
        MVVmLVA =
                Comparator.<Move>comparingInt(
                                m -> {
                                    final Figure from = board.getFigureUgly(m.getFrom());
                                    final Figure to = board.getFigureUgly(m.getTo());
                                    return to == null
                                            ? 0
                                            : FigureType.EMPTY_TYPE
                                                    + to.figureType.type
                                                    - from.figureType.type;
                                })
                        .reversed();
    }

    /** Сортирует ходы по типу */
    public static void moveTypeSort(final List<Move> allMoves) {
        allMoves.sort(movesPriority);
    }

    /** Сортировка MVV-LVA: максимальная жертва - минимальный агрессор */
    public void MVVmLVASort(final List<Move> allMoves) {
        allMoves.sort(MVVmLVA);
    }

    /** Сортирует ходы на основе эвристики истории, бабочки и MVV-LVA */
    public void allSorts(final List<Move> allMoves) {
        allMoves.sort(
                (m1, m2) -> {
                    final int s1 = MVVmLVA.compare(m1, m2);
                    if (s1 != 0) return s1;
                    return movesPriority.compare(m1, m2);
                });
    }
}
