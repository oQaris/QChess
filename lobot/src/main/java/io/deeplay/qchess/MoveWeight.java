package io.deeplay.qchess;

import io.deeplay.qchess.game.model.Move;

public class MoveWeight {
    private Move move;
    private double weight;

    public MoveWeight() {
        weight = 0;
    }

    public MoveWeight(final Move move, final double weight) {
        this.move = move;
        this.weight = weight;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(final Move move) {
        this.move = move;
    }

    public double getWeight() {
        return weight;
    }

    public void incWeight() {
        weight += 1;
    }
}
