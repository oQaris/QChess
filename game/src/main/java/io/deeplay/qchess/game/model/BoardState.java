package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.model.figures.Figure;
import java.util.Objects;

/** Описывает состояние доски */
public class BoardState {
    public final String forsythEdwards;
    public final Move lastMove;
    public final boolean hasMovedBeforeLastMove;
    public final Figure removedFigure;
    public final int peaceMoveCount;

    public BoardState(
            String forsythEdwards,
            Move lastMove,
            int peaceMoveCount,
            boolean hasMovedBeforeLastMove,
            Figure removedFigure) {
        this.forsythEdwards = forsythEdwards;
        this.lastMove = lastMove;
        this.peaceMoveCount = peaceMoveCount;
        this.hasMovedBeforeLastMove = hasMovedBeforeLastMove;
        this.removedFigure = removedFigure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || BoardState.class != o.getClass()) return false;
        BoardState that = (BoardState) o;
        return peaceMoveCount == that.peaceMoveCount
                && hasMovedBeforeLastMove == that.hasMovedBeforeLastMove
                && forsythEdwards.equals(that.forsythEdwards)
                && Objects.equals(lastMove, that.lastMove)
                && Objects.equals(removedFigure, that.removedFigure);
    }

    @Override
    public int hashCode() {
        final int h1 = lastMove == null ? 1 : lastMove.fullHashCode();
        return Objects.hash(
                h1, forsythEdwards, hasMovedBeforeLastMove, removedFigure, peaceMoveCount);
    }
}
