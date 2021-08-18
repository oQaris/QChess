package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.model.Move;

public class MovePoint extends ClusterPoint {
    private final Move move;

    public MovePoint(final int value, final int mark, final Move move) {
        super(value, mark);
        this.move = move;
    }

    public Move getMove() {
        return move;
    }
}
