package io.deeplay.qchess.nnnbot.bot.searchfunc.alfabetadeepsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.searchfunc.SearchFunc;
import java.util.List;

/** Поиск с альфа-бета отсечением на заданную глубину */
public abstract class AlfaBetaDeepSearch implements SearchFunc {

    /** Используется в качестве лучшей оценки для терминального узла (у противника нет ходов) */
    public final double theBestEstimation;
    /** Используется в качестве худшей оценки для терминального узла */
    public final double theWorstEstimation;

    public final int maxDepth;

    /**
     * @param maxDepth гарантируется больше нуля
     * @throws IllegalArgumentException если maxDepth <= 0
     */
    protected AlfaBetaDeepSearch(
            double theBestEstimation, double theWorstEstimation, int maxDepth) {
        if (maxDepth <= 0)
            throw new IllegalArgumentException("Максимальная глубина должна быть больше нуля");

        this.theBestEstimation = theBestEstimation;
        this.theWorstEstimation = theWorstEstimation;
        this.maxDepth = maxDepth;
    }

    @Override
    public Move findBest(GameSettings gs, Color color, EvaluationFunc evaluationFunc)
            throws ChessError {
        double optEstimation = Double.MIN_VALUE;
        double estimation;

        List<Move> allMoves = gs.moveSystem.getAllCorrectMovesSilence(color);
        Move theBestMove = allMoves.get(0);

        // TODO: запуск нескольких потоков для начальной глубины (?)
        for (Move move : allMoves) {
            if (move.getMoveType() == MoveType.TURN_INTO
                    || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
                move.setTurnInto(FigureType.QUEEN);
                estimation =
                        getMyBestEvaluationAfterVirtualMove(
                                move, gs, color, maxDepth, evaluationFunc);
                if (estimation > optEstimation) {
                    theBestMove = move;
                    optEstimation = estimation;
                }

                move.setTurnInto(FigureType.KNIGHT);
            }
            estimation =
                    getMyBestEvaluationAfterVirtualMove(move, gs, color, maxDepth, evaluationFunc);
            if (estimation > optEstimation) {
                theBestMove = move;
                optEstimation = estimation;
            }
        }

        return theBestMove;
    }

    /**
     * Делает мой виртуальный ход и лучший ход противника
     *
     * @return лучшая оценка для цвета color
     */
    private double getMyBestEvaluationAfterVirtualMove(
            Move move, GameSettings gs, Color color, int maxDepth, EvaluationFunc evaluationFunc)
            throws ChessError {
        gs.moveSystem.move(move);
        double estimation = getEvaluation(gs, color, maxDepth, evaluationFunc);
        gs.moveSystem.undoMove();
        return estimation;
    }

    /**
     * Делает лучший виртуальный ход противника
     *
     * @param depth гарантируется больше нуля
     * @return лучшая оценка для цвета color
     */
    public abstract double getEvaluation(
            GameSettings gs, Color color, int depth, EvaluationFunc evaluationFunc)
            throws ChessError;
}
