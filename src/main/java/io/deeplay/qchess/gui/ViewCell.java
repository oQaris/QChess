package io.deeplay.qchess.gui;

import java.util.Objects;

public class ViewCell {
    private final int column;
    private final int row;
    private final boolean attack;

    public ViewCell(int row, int column, boolean attack) {
        this.column = column;
        this.row = row;
        this.attack = attack;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public boolean isAttack() {
        return attack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViewCell viewCell = (ViewCell) o;
        return column == viewCell.column && row == viewCell.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }
}
