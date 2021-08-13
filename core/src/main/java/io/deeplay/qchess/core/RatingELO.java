package io.deeplay.qchess.core;

public class RatingELO {
    public static final String fileName = "elo_rating";

    public void updateELO(
            final String firstPlayerName, final String secondPlayerName, final int sA) {
        final long rA = 0;
        final long rB = 0;

        final double eA = 1. / (1. + Math.pow(10., (rB - rA) / 400.));

        final double k = 30;

        final long newRa = Math.round(rA + k * (sA - eA));
    }
}
