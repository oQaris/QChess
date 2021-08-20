package io.deeplay.qchess.nukebot.bot.searchalg.features;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class SearchImprovements {

    public static final Comparator<Move> movesPriority =
            Comparator.<Move>comparingInt(m -> m.getMoveType().importantLevel).reversed();

    private static final int HISTORY_SCALE = 1;

    public static void prioritySort(final List<Move> allMoves) {
        allMoves.sort(movesPriority);
    }

    /** @return отсортированный итератор по убыванию (макс. жертва - мин. агрессор) */
    public static Stream<Move> MVV_LVA_sort(final Board board, final Stream<Move> allMoves) {
        return allMoves.sorted(
                Comparator.comparingInt(
                        m -> {
                            final Figure from = board.getFigureUgly(m.getFrom());
                            final Figure to = board.getFigureUgly(m.getTo());
                            return to == null
                                    ? FigureType.EMPTY_TYPE
                                    : from.figureType.type - to.figureType.type;
                        }));
    }

    /** Сортирует ходы на основе эвристики истории и бабочки */
    public static Stream<Move> relativeHistorySort(
            final Stream<Move> allMoves,
            final int[][][] moveHistory,
            final int[][][] butterfly,
            final int color) {
        return allMoves.sorted(
                Comparator.<Move>comparingInt(
                                m ->
                                        HISTORY_SCALE
                                                * moveHistory[color][m.getFrom().toSquare()][
                                                        m.getTo().toSquare()]
                                                / butterfly[color][m.getFrom().toSquare()][
                                                        m.getTo().toSquare()])
                        .reversed());
    }

    public static void allSort(
            final Board board,
            final List<Move> allMoves,
            final int[][][] moveHistory,
            final int[][][] butterfly,
            final int color) {
        final Comparator<Move> relativeHistory =
                Comparator.<Move>comparingInt(
                                m ->
                                        HISTORY_SCALE
                                                * moveHistory[color][m.getFrom().toSquare()][
                                                        m.getTo().toSquare()]
                                                / butterfly[color][m.getFrom().toSquare()][
                                                        m.getTo().toSquare()])
                        .reversed();
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
                    final int s1 = relativeHistory.compare(m1, m2);
                    if (s1 != 0) return s1;
                    final int s2 = MVV_LVA.compare(m1, m2);
                    if (s2 != 0) return s2;
                    return movesPriority.compare(m1, m2);
                });
    }

    public static Stream<Move> allSort(
            final Board board,
            final Stream<Move> allMoves,
            final int[][][] moveHistory,
            final int[][][] butterfly,
            final int color) {
        final Comparator<Move> relativeHistory =
                Comparator.<Move>comparingInt(
                                m ->
                                        HISTORY_SCALE
                                                * moveHistory[color][m.getFrom().toSquare()][
                                                        m.getTo().toSquare()]
                                                / butterfly[color][m.getFrom().toSquare()][
                                                        m.getTo().toSquare()])
                        .reversed();
        final Comparator<Move> MVV_LVA =
                Comparator.comparingInt(
                        m -> {
                            final Figure from = board.getFigureUgly(m.getFrom());
                            final Figure to = board.getFigureUgly(m.getTo());
                            return to == null
                                    ? FigureType.EMPTY_TYPE
                                    : from.figureType.type - to.figureType.type;
                        });
        return allMoves.sorted(
                (m1, m2) -> {
                    final int s1 = relativeHistory.compare(m1, m2);
                    if (s1 != 0) return s1;
                    final int s2 = MVV_LVA.compare(m1, m2);
                    if (s2 != 0) return s2;
                    return movesPriority.compare(m1, m2);
                });
    }
}
