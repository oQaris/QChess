package io.deeplay.qchess;

import io.deeplay.qchess.game.model.Move;

public class MoveWeight {
    private final Move move;
    private final double weight;

    public MoveWeight(final Move move, final double weight) {
        this.move = move;
        this.weight = weight;
    }


    public Move getMove() {
        return move;
    }

    public double getWeight() {
        return weight;
    }
}
