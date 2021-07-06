package io.deeplay.qchess.game.model;

public class Move {

    private Cell from;
    private Cell to;

    public Move(Cell from, Cell to) {
        this.from = from;
        this.to = to;
    }

    public Cell getFrom() {
        return from;
    }

    public Cell getTo() {
        return to;
    }
}
