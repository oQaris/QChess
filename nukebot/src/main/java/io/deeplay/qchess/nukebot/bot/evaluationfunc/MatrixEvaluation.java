package io.deeplay.qchess.nukebot.bot.evaluationfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.EnumMap;
import java.util.Map;

public class MatrixEvaluation {

    // ----------- Оценки позиции выбраны исходя из стоимости пешки == 100 ----------- //
    // -------------- Матрицы выбраны так, что белый игрок играет снизу -------------- //

    public static final int[][] pawnEval = {
        {2, 2, 2, 2, 2, 2, 2, 2},
        {122, 121, 120, 117, 117, 120, 121, 122},
        {13, 13, 23, 33, 33, 23, 13, 13},
        {8, 8, 13, 28, 28, 13, 8, 8},
        {3, 2, 3, 23, 23, 3, 2, 3},
        {7, -2, -7, 3, 3, -8, -2, 7},
        {7, 12, 12, -18, -18, 12, 12, 7},
        {2, 2, 2, 2, 2, 2, 2, 2}
    };
    public static final int[][] knightEval = {
        {-40, -28, -15, -13, -13, -15, -28, -40},
        {-28, -5, 18, 17, 17, 18, -5, -28},
        {-13, 18, 30, 33, 33, 30, 18, -13},
        {-12, 25, 36, 42, 42, 36, 25, -12},
        {-12, 20, 36, 42, 42, 36, 20, -12},
        {-13, 23, 30, 33, 33, 30, 23, -13},
        {-28, -5, 18, 22, 22, 18, -5, -28},
        {-40, -28, -15, -13, -13, -15, -28, -40}
    };
    public static final int[][] bishopEval = {
        {-2, 8, 11, 15, 15, 11, 8, -2},
        {10, 21, 22, 21, 21, 22, 21, 10},
        {10, 20, 26, 32, 32, 26, 20, 10},
        {11, 26, 27, 33, 33, 27, 26, 11},
        {11, 21, 32, 33, 33, 32, 21, 11},
        {10, 30, 30, 31, 31, 30, 30, 10},
        {10, 22, 15, 17, 17, 15, 22, 10},
        {-5, 2, 5, 2, 2, 5, 2, -5}
    };
    public static final int[][] rookEval = {
        {12, 15, 16, 17, 17, 16, 15, 12},
        {15, 22, 22, 22, 22, 22, 22, 15},
        {7, 15, 16, 15, 15, 16, 15, 7},
        {10, 16, 17, 17, 17, 17, 16, 10},
        {10, 16, 17, 17, 17, 17, 16, 10},
        {5, 15, 16, 16, 16, 16, 15, 5},
        {5, 13, 13, 15, 15, 13, 13, 5},
        {12, 13, 13, 20, 20, 13, 13, 12}
    };
    public static final int[][] queenEval = {
        {-10, 1, 2, 7, 7, 2, 1, -10},
        {1, 12, 13, 13, 13, 13, 12, 1},
        {2, 15, 21, 21, 21, 21, 15, 2},
        {10, 16, 22, 22, 22, 22, 16, 10},
        {15, 16, 22, 22, 22, 22, 16, 10},
        {2, 20, 21, 21, 21, 21, 15, 2},
        {1, 12, 18, 13, 13, 13, 12, 1},
        {-10, 1, 2, 7, 7, 2, 1, -10}
    };
    public static final int[][] kingEval = {
        {70, 70, 75, 75, 75, 75, 70, 70},
        {70, 70, 70, 70, 70, 70, 70, 70},
        {65, 60, 60, 55, 55, 60, 60, 65},
        {65, 60, 55, 50, 50, 55, 60, 65},
        {80, 70, 60, 50, 50, 60, 70, 80},
        {90, 85, 80, 80, 80, 80, 85, 90},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 100, 100, 99, 99, 99}
    };
    public static final int[][] middleGameKingEval = {
        {-13, -23, -22, -32, -32, -22, -23, -13},
        {-13, -23, -23, -33, -33, -23, -23, -13},
        {-14, -25, -25, -37, -37, -25, -25, -14},
        {-14, -25, -27, -38, -38, -27, -25, -14},
        {0, -13, -15, -28, -28, -15, -13, 0},
        {12, 1, 0, 0, 0, 0, 1, 12},
        {44, 44, 24, 24, 24, 24, 44, 44},
        {44, 54, 34, 25, 25, 34, 54, 44}
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
    public static int figurePositionHeuristics(GameSettings gs, Color myColor) {
        int enemyEstimation = 0;
        int myEstimation = 0;
        for (int row = 0; row < 8; ++row)
            for (int column = 0; column < 8; ++column) {
                final Figure figure = gs.board.getFigureUgly(row, column);
                if (figure != null) {
                    int r = figure.getColor() == Color.BLACK ? 7 - row : row;

                    int eval =
                            evaluations.get(figure.figureType)[r][column]
                                    + costInPawns.get(figure.figureType);
                    if (figure.getColor() == myColor) myEstimation += eval;
                    else enemyEstimation += eval;
                }
            }
        return 5 * myEstimation - 4 * enemyEstimation;
    }

    /**
     * Эвристика потенциальной атаки на фигуры
     *
     * @param myColor цвет игрока, который атакует соперника
     * @deprecated Не является потокобезопасной
     */
    @Deprecated
    public static int figureAttackHeuristics(GameSettings gs, Color myColor) throws ChessError {
        int myMoveCount = gs.board.getAllPreparedMoves(gs, myColor).size();
        int enemyMoveCount = gs.board.getAllPreparedMoves(gs, myColor.inverse()).size();
        return myMoveCount - enemyMoveCount;
    }

    /**
     * Эвристика подсчета стоимости фигур и возможность потенциальной атаки на них
     *
     * @param myColor цвет игрока, который атакует соперника
     * @deprecated Не является потокобезопасной
     */
    @Deprecated
    public static int ultimateHeuristics(GameSettings gs, Color myColor) throws ChessError {
        // TODO: use Lazy Evaluation
        return figurePositionHeuristics(gs, myColor) + 20 * figureAttackHeuristics(gs, myColor);
    }
}
