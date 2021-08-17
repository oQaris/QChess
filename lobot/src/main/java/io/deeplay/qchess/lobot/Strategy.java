package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.lobot.evaluation.Evaluation;
import io.deeplay.qchess.lobot.evaluation.PestoEvaluation;

public class Strategy {

    private final Evaluation evaluation;
    private final TraversalAlgorithm algorithm;
    private final int depth;
    private final boolean onMonteCarlo;

    public Strategy() {
        this(new PestoEvaluation(), TraversalAlgorithm.MINIMAX, 2, false);
    }

    public Strategy(Evaluation evaluation, TraversalAlgorithm algorithm, int depth,
        boolean onMonteCarlo) {
        this.evaluation = evaluation;
        this.algorithm = algorithm;
        this.depth = depth;
        this.onMonteCarlo = onMonteCarlo;
    }

    public static int getTerminalEvaluation(final Color color, final EndGameType endGameType) {
        if (color == Color.WHITE) {
            return switch (endGameType) {
                case CHECKMATE_TO_BLACK -> Integer.MIN_VALUE + 100;
                case CHECKMATE_TO_WHITE -> Integer.MAX_VALUE - 100;
                default -> 0;
            };
        } else {
            return switch (endGameType) {
                case CHECKMATE_TO_WHITE -> Integer.MIN_VALUE + 100;
                case CHECKMATE_TO_BLACK -> Integer.MAX_VALUE - 100;
                default -> 0;
            };
        }
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public TraversalAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getDepth() {
        return depth;
    }

    public boolean getOnMonteCarlo() {
        return onMonteCarlo;
    }
}
