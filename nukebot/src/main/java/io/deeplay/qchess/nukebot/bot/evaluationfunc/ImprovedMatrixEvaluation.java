package io.deeplay.qchess.nukebot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.EnumMap;
import java.util.Map;

public class ImprovedMatrixEvaluation {

    // ----------- Оценки позиции выбраны исходя из стоимости пешки == 100 ----------- //
    // -------------- Матрицы выбраны так, что белый игрок играет снизу -------------- //

    public static final int[][] pawnEval = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {5, 5, 10, 25, 25, 10, 5, 5},
        {0, 0, 0, 20, 20, 0, 0, 0},
        {5, -5, -10, 0, 0, -10, -5, 5},
        {5, 10, 10, -20, -20, 10, 10, 5},
        {0, 0, 0, 0, 0, 0, 0, 0}
    };
    public static final int[][] knightEval = {
        {-50, -40, -30, -30, -30, -30, -40, -50},
        {-40, -20, 0, 0, 0, 0, -20, -40},
        {-30, 0, 10, 15, 15, 10, 0, -30},
        {-30, 5, 15, 20, 20, 15, 5, -30},
        {-30, 0, 15, 20, 20, 15, 0, -30},
        {-30, 5, 10, 15, 15, 10, 5, -30},
        {-40, -20, 0, 5, 5, 0, -20, -40},
        {-50, -40, -30, -30, -30, -30, -40, -50}
    };
    public static final int[][] bishopEval = {
        {-20, -10, -10, -10, -10, -10, -10, -20},
        {-10, 0, 0, 0, 0, 0, 0, -10},
        {-10, 0, 5, 10, 10, 5, 0, -10},
        {-10, 5, 5, 10, 10, 5, 5, -10},
        {-10, 0, 10, 10, 10, 10, 0, -10},
        {-10, 10, 10, 10, 10, 10, 10, -10},
        {-10, 5, 0, 0, 0, 0, 5, -10},
        {-20, -10, -10, -10, -10, -10, -10, -20}
    };
    public static final int[][] rookEval = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {5, 10, 10, 10, 10, 10, 10, 5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {0, 0, 0, 5, 5, 0, 0, 0}
    };
    public static final int[][] queenEval = {
        {-20, -10, -10, -5, -5, -10, -10, -20},
        {-10, 0, 0, 0, 0, 0, 0, -10},
        {-10, 0, 5, 5, 5, 5, 0, -10},
        {-5, 0, 5, 5, 5, 5, 0, -5},
        {0, 0, 5, 5, 5, 5, 0, -5},
        {-10, 5, 5, 5, 5, 5, 0, -10},
        {-10, 0, 5, 0, 0, 0, 0, -10},
        {-20, -10, -10, -5, -5, -10, -10, -20}
    };
    public static final int[][] middleGameKingEval = {
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-20, -30, -30, -40, -40, -30, -30, -20},
        {-10, -20, -20, -20, -20, -20, -20, -10},
        {20, 20, 0, 0, 0, 0, 20, 20},
        {20, 30, 10, 0, 0, 10, 30, 20}
    };
    public static final int[][] endGameKingEval = {
        {-50, -40, -30, -20, -20, -30, -40, -50},
        {-30, -20, -10, 0, 0, -10, -20, -30},
        {-30, -10, 20, 30, 30, 20, -10, -30},
        {-30, -10, 30, 40, 40, 30, -10, -30},
        {-30, -10, 30, 40, 40, 30, -10, -30},
        {-30, -10, 20, 30, 30, 20, -10, -30},
        {-30, -30, 0, 0, 0, 0, -30, -30},
        {-50, -30, -30, -30, -30, -30, -30, -50}
    };

    public static final Map<FigureType, int[][]> evaluations;
    public static final Map<FigureType, Integer> costInPawns;

    static {
        costInPawns = new EnumMap<>(FigureType.class);
        costInPawns.put(FigureType.PAWN, 100);
        costInPawns.put(FigureType.KNIGHT, 320);
        costInPawns.put(FigureType.BISHOP, 330);
        costInPawns.put(FigureType.ROOK, 500);
        costInPawns.put(FigureType.QUEEN, 900);
        costInPawns.put(FigureType.KING, 20000);

        evaluations = new EnumMap<>(FigureType.class);
        evaluations.put(FigureType.PAWN, pawnEval);
        evaluations.put(FigureType.KNIGHT, knightEval);
        evaluations.put(FigureType.BISHOP, bishopEval);
        evaluations.put(FigureType.ROOK, rookEval);
        evaluations.put(FigureType.QUEEN, queenEval);
        evaluations.put(FigureType.KING, middleGameKingEval);
    }

    /**
     * Эвристика подсчета стоимости позиции фигур
     *
     * @param myColor цвет игрока, который укрепляет свою позицию
     */
    public static int figurePositionHeuristics(final GameSettings gs, final Color myColor) {
        int enemyEstimation = 0;
        int myEstimation = 0;
        for (int row = 0; row < 8; ++row)
            for (int column = 0; column < 8; ++column) {
                final Figure figure = gs.board.getFigureUgly(row, column);
                if (figure != null) {
                    final int r = figure.getColor() == Color.BLACK ? 7 - row : row;

                    final int eval =
                            evaluations.get(figure.figureType)[r][column]
                                    + costInPawns.get(figure.figureType);
                    if (figure.getColor() == myColor) myEstimation += eval;
                    else enemyEstimation += eval;
                }
            }
        return 5 * myEstimation - 4 * enemyEstimation;
    }
}
