package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.figures.interfaces.Figure;
import java.util.Objects;

public class Move {

    private Figure turnInto;
    private MoveType moveType;
    private Cell from;
    private Cell to;

    public Move(MoveType moveType, Cell from, Cell to) {
        this.moveType = moveType;
        this.from = from;
        this.to = to;
    }

    public void setTurnInto(Figure turnInto) {
        this.turnInto = turnInto;
    }

    public Figure getTurnInto() {
        return turnInto;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Move move = (Move) o;
        return moveType == move.moveType
                && Objects.equals(from, move.from)
                && Objects.equals(to, move.to)
                && Objects.equals(turnInto, move.turnInto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveType, from, to, turnInto);
    }

    @Override
    public String toString() {
        return "Move{"
                + "moveType=" + moveType
                + ", from=" + from
                + ", to=" + to
                + '}';
    }
}
