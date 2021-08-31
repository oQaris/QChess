package io.deeplay.qchess.logparser;

public enum ParseMode {
    START_MOVES,
    END_FIGURES;

    /**
     * Для START_MOVES обозначает сколько первых ходов просматривать
     * Для END_FIGURES обозначает сколь фигур должно быть на доске
     */
    private int count = 4;

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }
}
