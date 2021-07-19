package io.deeplay.qchess.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Objects;

public class Move {
    @JsonProperty("type")
    private final MoveType moveType;

    @JsonProperty("from")
    private final Cell from;

    @JsonProperty("to")
    private final Cell to;
    // не должно влиять на equals и hashCode,
    // чтобы, проверяя корректность ходов, у пешек не возникали дополнительные условия,
    // т.к. пешки на доске не знают во что превратиться без запроса игрока.
    // проверка вынесена в MoveSystem
    @JsonProperty("turnInto")
    private FigureType turnInto;

    public Move(MoveType moveType, Cell from, Cell to) {
        this.moveType = moveType;
        this.from = from;
        this.to = to;
    }

    public FigureType getTurnInto() {
        return turnInto;
    }

    /**
     * @param turnInto указывать from = this.to и to = this.to, как у этого мува, но необязательно
     */
    public void setTurnInto(FigureType turnInto) {
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
    public int hashCode() {
        return Objects.hash(moveType, from, to);
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
    public String toString() {
        StringBuilder sb =
                new StringBuilder()
                        .append(from)
                        .append("-")
                        .append(to)
                        .append(" (")
                        .append(moveType)
                        .append(")");
        if (moveType == MoveType.TURN_INTO) sb.append(" turn into ").append(turnInto);
        return sb.toString();
    }
}
