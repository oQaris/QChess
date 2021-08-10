package io.deeplay.qchess.lobot;

import io.deeplay.qchess.lobot.evaluation.Evaluation;
import io.deeplay.qchess.lobot.evaluation.PestoEvaluation;

public class Strategy {
    private final Evaluation evaluation;
    private final TraversalAlgorithm algorithm;
    private final int depth;

    public Strategy() {
        this(new PestoEvaluation(), TraversalAlgorithm.MINIMAX, 2);
    }

    public Strategy(Evaluation evaluation, TraversalAlgorithm algorithm, int depth) {
        this.evaluation = evaluation;
        this.algorithm = algorithm;
        this.depth = depth;
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
}
