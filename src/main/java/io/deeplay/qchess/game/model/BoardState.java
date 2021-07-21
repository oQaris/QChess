package io.deeplay.qchess.game.model;

import java.util.Objects;

/** Описывает состояние доски */
public class BoardState {
    public final String forsythEdwards;
    public final Move lastMove;
    public final int peaceMoveCount;

    public BoardState(String forsythEdwards, Move lastMove, int peaceMoveCount) {
        this.forsythEdwards = forsythEdwards;
        this.lastMove = lastMove;
        this.peaceMoveCount = peaceMoveCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || BoardState.class != o.getClass()) return false;
        BoardState that = (BoardState) o;
        return peaceMoveCount == that.peaceMoveCount
                && forsythEdwards.equals(that.forsythEdwards)
                && Objects.equals(lastMove, that.lastMove);
    }

    @Override
    public int hashCode() {
        final int h1 = lastMove == null ? 1 : lastMove.fullHashCode();
        return 17 * (31 * h1 + forsythEdwards.hashCode()) + peaceMoveCount;
    }
}
