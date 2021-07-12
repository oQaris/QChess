package io.deeplay.qchess.game.model;

import io.deeplay.qchess.game.model.figures.interfaces.Figure;

import java.util.Objects;

public class Move {

    private final MoveType moveType;
    private final Cell from;
    private final Cell to;
    // не должно влиять на equals и hashCode,
    // чтобы, проверяя корректность ходов, у пешек не возникали дополнительные условия,
    // т.к. пешки на доске не знают во что превратиться без запроса игрока.
    // проверка вынесена в MoveSystem
    private Figure turnInto;

    public Move(MoveType moveType, Cell from, Cell to) {
        this.moveType = moveType;
        this.from = from;
        this.to = to;
    }

    public Figure getTurnInto() {
        return turnInto;
    }

    /**
     * @param turnInto указывать from = this.to и to = this.to, как у этого мува, но необязательно
     */
    public void setTurnInto(Figure turnInto) {
        this.turnInto = turnInto;
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
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return getMoveType() == move.getMoveType()
                && Objects.equals(getFrom(), move.getFrom())
                && Objects.equals(getTo(), move.getTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMoveType(), getFrom(), getTo());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append(from).append("-").append(to)
                .append(" (").append(moveType).append(")");
        if (moveType == MoveType.TURN_INTO)
            sb.append(" turn into ").append(turnInto);
        return sb.toString();
    }
}