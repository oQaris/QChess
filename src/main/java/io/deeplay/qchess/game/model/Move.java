package io.deeplay.qchess.game.model;

import java.util.Objects;

public class Move {
    private MoveType moveType;
    private Cell from;
    private Cell to;

    public Move(MoveType moveType, Cell from, Cell to) {
        this.moveType = moveType;
        this.from = from;
        this.to = to;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Cell getFrom() {
        return from;
    }

    public Cell getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return moveType == move.moveType
                && Objects.equals(from, move.from)
                && Objects.equals(to, move.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveType, from, to);
    }
}