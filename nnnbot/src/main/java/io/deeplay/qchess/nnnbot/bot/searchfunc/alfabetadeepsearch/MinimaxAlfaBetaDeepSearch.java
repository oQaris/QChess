package io.deeplay.qchess.nnnbot.bot.searchfunc.alfabetadeepsearch;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MinimaxAlfaBetaDeepSearch extends AlfaBetaDeepSearch {

    private static final Comparator<Move> movesPriority =
            (m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel;

    private GameSettings gs;
    private EvaluationFunc evaluationFunc;
    private Color myColor;
    private Color enemyColor;

    /**
     * @param maxDepth гарантируется больше нуля
     * @throws IllegalArgumentException если maxDepth <= 0
     */
    public MinimaxAlfaBetaDeepSearch(int maxDepth) {
        super(Double.MAX_VALUE, maxDepth);
    }

    @Override
    public double getEvaluation(
            GameSettings gs, Color color, int depth, EvaluationFunc evaluationFunc)
            throws ChessError {
        this.gs = gs;
        this.evaluationFunc = evaluationFunc;
        this.myColor = color;
        this.enemyColor = color.inverse();
        return minimax(false, Double.MIN_VALUE, Double.MAX_VALUE, depth);
    }

    private double minimax(boolean isMyMove, double alfa, double beta, int depth)
            throws ChessError {
        Color color = isMyMove ? myColor : enemyColor;
        // TODO: работает неправильно для четной глубины
        if (--depth < 0) return evaluationFunc.getEvaluation(gs, color);
        List<Move> allMoves = gs.moveSystem.getAllCorrectMovesSilence(color);
        if (allMoves.isEmpty()) return theBestEstimation;

        allMoves.sort(movesPriority);
        // Для дополнительных не очень важных ходов (превращение пешки в коня)
        AddMoves addMoves = new AddMoves();
        double optEstimation = isMyMove ? Double.MIN_VALUE : Double.MAX_VALUE;
        double estimation;

        for (Move move : allMoves) {
            estimation =
                    getTheBestEvaluationAfterVirtualMove(
                            addMoves, move, isMyMove, alfa, beta, depth);
            if (isMyMove) {
                if (estimation > optEstimation) optEstimation = estimation;
                if (alfa < optEstimation) alfa = optEstimation;
            } else {
                if (estimation < optEstimation) optEstimation = estimation;
                if (beta > optEstimation) beta = optEstimation;
            }
            if (beta <= alfa) return optEstimation;
        }

        // Превращения пешки в коня
        if (addMoves.additionalMoves != null) {
            for (Move move : addMoves.additionalMoves) {
                estimation =
                        getTheBestEvaluationAfterVirtualMove(move, isMyMove, alfa, beta, depth);
                if (isMyMove) {
                    if (estimation > optEstimation) optEstimation = estimation;
                    if (alfa < optEstimation) alfa = optEstimation;
                } else {
                    if (estimation < optEstimation) optEstimation = estimation;
                    if (beta > optEstimation) beta = optEstimation;
                }
                if (beta < alfa) return optEstimation;
            }
        }

        return optEstimation;
    }

    private double getTheBestEvaluationAfterVirtualMove(
            AddMoves addMoves, Move move, boolean isMyMove, double alfa, double beta, int depth)
            throws ChessError {
        double estimation;
        try {
            gs.moveSystem.move(move);
            estimation = minimax(!isMyMove, alfa, beta, depth);
            gs.moveSystem.undoMove();

            return estimation;
        } catch (ChessError e) {
            // move оказался превращением пешки
            move.setTurnInto(FigureType.QUEEN);
            estimation = getTheBestEvaluationAfterVirtualMove(move, isMyMove, alfa, beta, depth);

            move.setTurnInto(FigureType.KNIGHT);
            if (addMoves.additionalMoves == null) addMoves.additionalMoves = new ArrayList<>(5);
            addMoves.additionalMoves.add(move);

            return estimation;
        }
    }

    private double getTheBestEvaluationAfterVirtualMove(
            Move move, boolean isMyMove, double alfa, double beta, int depth) throws ChessError {
        gs.moveSystem.move(move);
        double estimation = minimax(!isMyMove, alfa, beta, depth);
        gs.moveSystem.undoMove();

        return estimation;
    }

    private static class AddMoves {
        public List<Move> additionalMoves;
    }
}
