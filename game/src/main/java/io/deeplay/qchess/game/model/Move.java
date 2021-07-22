package io.deeplay.qchess.game.model;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Objects;

public class Move {
    @SerializedName("type")
    private MoveType moveType;

    @SerializedName("from")
    private Cell from;

    @SerializedName("to")
    private Cell to;

    /**
     * Не должно влиять на equals и hashCode, чтобы, проверяя корректность ходов, у пешек не
     * возникали дополнительные условия, т.к. пешки на доске не знают во что превратиться без
     * запроса игрока. Проверка вынесена в MoveSystem
     */
    @SerializedName("turnInto")
    private FigureType turnInto;

    public Move(final MoveType moveType, final Cell from, final Cell to) {
        this.moveType = moveType;
        this.from = from;
        this.to = to;
    }

    public FigureType getTurnInto() {
        return turnInto;
    }

    public void setTurnInto(final FigureType turnInto) {
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

    /** @return полный хеш мува */
    public int fullHashCode() {
        return Objects.hash(moveType, from, to, turnInto);
    }

    /**
     * Не хеширует фигуру для превращения, читать подробнее: {@link #turnInto}
     *
     * <p>Для получения полного хеша используйте {@link #fullHashCode}
     */
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