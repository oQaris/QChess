package io.deeplay.qchess.nukebot.bot.searchalg.features;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class SearchImprovements {

    public static final Comparator<Move> movesPriority =
            Comparator.<Move>comparingInt(m -> m.getMoveType().importantLevel).reversed();

    public static void prioritySort(final List<Move> allMoves) {
        allMoves.sort(movesPriority);
    }

    /** @return отсортированный поток по убыванию (макс. жертва - мин. агрессор) */
    public static Iterator<Move> MVV_LVA_attack_sort(final Board board, final List<Move> allMoves) {
        // TODO: добавить оценку позиции (?)
        // TODO: переделать на ArrayList
        return allMoves.stream()
                .filter(
                        move ->
                                switch (move.getMoveType()) {
                                    case ATTACK, TURN_INTO_ATTACK -> true;
                                    default -> false;
                                })
                .sorted(
                        Comparator.comparingInt(
                                m ->
                                        board.getFigureUgly(m.getFrom()).figureType.type
                                                - board.getFigureUgly(m.getTo()).figureType.type))
                .iterator();
    }
}
