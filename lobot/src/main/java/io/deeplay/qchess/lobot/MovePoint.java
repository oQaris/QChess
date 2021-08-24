package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.model.Move;
import java.util.Objects;

public class MovePoint extends ClusterPoint {
    private final Move move;

    public MovePoint(final int value, final int mark, final Move move) {
        super(value, mark);
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final MovePoint movePoint = (MovePoint) o;
        return Objects.equals(move, movePoint.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), move);
    }
}
