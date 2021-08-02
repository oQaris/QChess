package io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Реализует эвристику как алгоритм минимакса, поэтому желательно использовать функцию оценки не
 * зависящую от цвета игрока (должна быть с нулевой суммой, т.е. для текущего игрока возвращать
 * максимум, а для противника минимум)
 */
public class MinimaxParallelSearch extends ParallelSearch {

    private static final Comparator<Move> movesPriority =
            (m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel;
    private static final int REPETITIONS_COUNT = 3;
    private static final double REPETITIONS_FACTOR = 0.75;

    private Move mainMove;

    public MinimaxParallelSearch(
            GameSettings gs, Color color, EvaluationFunc evaluationFunc, int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public double getHeuristics(Move mainMove, int depth) throws ChessError {
        this.mainMove = mainMove;
        return minimax(false, Double.MIN_VALUE, Double.MAX_VALUE, depth);
    }

    /**
     * @param alfa минимальная оценка из максимальных для максимизирующего игрока (лучшая из
     *     гарантированных для него)
     * @param beta максимальная оценка из минимальных для минимизирующего игрока (лучшая из
     *     гарантированных для него)
     */
    private double minimax(boolean isMaximizingPlayer, double alfa, double beta, int depth)
            throws ChessError {
        if (--depth < 0) return evaluationFunc.getEvaluation(gs, myColor);

        Color color;
        double theWorstValue;
        if (isMaximizingPlayer) {
            color = myColor;
            theWorstValue = Double.MIN_VALUE;
        } else {
            color = enemyColor;
            theWorstValue = Double.MAX_VALUE;
        }

        List<Move> allMoves = gs.moveSystem.getAllCorrectMovesSilence(color);
        if (allMoves.isEmpty()) {
            if (!gs.endGameDetector.isCheck(color)) theWorstValue /= 2;
            return theWorstValue;
        }

        double optEstimation = theWorstValue;
        double estimation;
        boolean isTurnIntoKnight = false; // для дополнительного хода (превращение пешки в коня)
        // т.к. сначала ходы упорядочены по превращению пешки, то после просмотра их всех, больше
        // нет необходимости проверять тип хода на превращение
        boolean skipTurnInto = false;
        allMoves.sort(movesPriority); // превращение пешки в приоритете

        Iterator<Move> it = allMoves.iterator();
        Move move = it.next();
        do {
            if (!skipTurnInto) {
                if (move.getMoveType() == MoveType.TURN_INTO
                        || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
                    if (move.getTurnInto() == null) {
                        move.setTurnInto(FigureType.QUEEN);
                        isTurnIntoKnight = true;
                    }
                } else skipTurnInto = true;
            }

            gs.moveSystem.move(move);
            estimation = minimax(!isMaximizingPlayer, alfa, beta, depth);
            if (gs.history.checkRepetitions(REPETITIONS_COUNT)) estimation *= REPETITIONS_FACTOR;
            gs.moveSystem.undoMove();

            if (isMaximizingPlayer) {
                if (estimation > optEstimation) optEstimation = estimation;
                if (estimation > alfa) alfa = estimation;
            } else {
                if (estimation < optEstimation) optEstimation = estimation;
                if (estimation < beta) beta = estimation;
            }

            if (isTurnIntoKnight) {
                isTurnIntoKnight = false;
                // дополнительный ход, как расширение текущего, поэтому не нужно переходить к
                // следующему элементу
                move.setTurnInto(FigureType.KNIGHT);
            } else if (it.hasNext()) move = it.next();
        } while (beta > alfa && it.hasNext());

        return optEstimation;
    }
}
