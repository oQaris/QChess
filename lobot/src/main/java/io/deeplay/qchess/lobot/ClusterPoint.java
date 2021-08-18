package io.deeplay.qchess.lobot;

import java.util.Objects;

public class ClusterPoint {

    private int value;
    private int mark;

    public ClusterPoint(final int value, final int mark) {
        setValue(value);
        setMark(mark);
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(final int mark) {
        this.mark = mark;
        if (mark < -1) {
            this.mark = 0;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ClusterPoint that = (ClusterPoint) o;
        return value == that.value && mark == that.mark;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, mark);
    }

    @Override
    public String toString() {
        return "{" + "value=" + value + ", mark=" + mark + '}';
    }
}
