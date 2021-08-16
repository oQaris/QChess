package io.deeplay.qchess.game.model;

import com.google.gson.annotations.SerializedName;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Objects;

public class Move {
    @SerializedName("type")
    private final MoveType moveType;

    @SerializedName("from")
    private final Cell from;

    @SerializedName("to")
    private final Cell to;

    @SerializedName("turnInto")
    public FigureType turnInto;

    public Move(final MoveType moveType, final Cell from, final Cell to) {
        this.moveType = moveType;
        this.from = from;
        this.to = to;
    }

    public Move(final Move move, final FigureType turnInto) {
        moveType = move.moveType;
        from = move.from;
        to = move.to;
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

    /**
     * Не использует {@code move.turnInto}, т.к. пешки на доске не знают во что превратиться без
     * запроса игрока, смотреть {@link
     * io.deeplay.qchess.game.logics.MoveSystem#inAvailableMoves(Move move)}.
     *
     * <p>Проверка пешки на превращение вынесена в {@link
     * io.deeplay.qchess.game.logics.MoveSystem#checkCorrectnessIfSpecificMove(Move move)}.
     *
     * <p>Также превращение проверяется в проверке виртуального хода {@link
     * io.deeplay.qchess.game.logics.MoveSystem#isCorrectMoveWithoutCheckAvailableMoves(Move move)}
     */
    public boolean equalsWithoutTurnInto(final Move move) {
        return this == move
                || move != null
                        && moveType == move.moveType
                        && Objects.equals(from, move.from)
                        && Objects.equals(to, move.to);
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + Cell.hashCodes[from.column][from.row];
        result = 31 * result + Cell.hashCodes[to.column][to.row];
        result = 31 * result + moveType.ordinal();
        result = 31 * result + (turnInto == null ? 0 : turnInto.type);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Move move = (Move) o;
        return moveType == move.moveType
                && Objects.equals(from, move.from)
                && Objects.equals(to, move.to)
                && Objects.equals(turnInto, move.turnInto);
    }

    @Override
    public String toString() {
        final StringBuilder sb =
                new StringBuilder()
                        .append(from)
                        .append("-")
                        .append(to)
                        .append(" (")
                        .append(moveType)
                        .append(")");
        return switch (moveType) {
            case TURN_INTO, TURN_INTO_ATTACK -> sb.append(" turn into ")
                    .append(turnInto)
                    .toString();
            default -> sb.toString();
        };
    }
}
