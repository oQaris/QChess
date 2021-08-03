package io.deeplay.qchess.lobot.strategy;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.lobot.FigureService;
import java.util.HashMap;
import java.util.Map;

public class StaticPositionMatrixEvaluateStrategy implements EvaluateStrategy {
    private final int[][] pawnEvaluate = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {10, 10, 10, 10, 10, 10, 10, 10},
        {2, 2, 4, 6, 6, 4, 2, 2},
        {1, 1, 2, 5, 5, 2, 1, 1},
        {0, 0, 0, 4, 4, 0, 0, 0},
        {1, -1, -2, 0, 0, -2, -1, 1},
        {1, 2, 2, -4, -4, 2, 2, 1},
        {0, 0, 0, 0, 0, 0, 0, 0}};

    private final int[][] rookEvaluate = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {-1, 0, 0, 0, 0, 0, 0, -1},
        {-1, 0, 0, 0, 0, 0, 0, -1},
        {-1, 0, 0, 0, 0, 0, 0, -1},
        {-1, 0, 0, 0, 0, 0, 0, -1},
        {-1, 0, 0, 0, 0, 0, 0, -1},
        {-1, 0, 0, 0, 0, 0, 0, -1},
        {0, 0, 0, 1, 1, 0, 0, 0}};

    private final int[][] knightEvaluate = {
        {-10, -8, -6, -6, -6, -6, -8, -10},
        {-8, -4, 0, 0, 0, 0, -4, -8},
        {-6, 0, 2, 3, 3, 2, 0, -6},
        {-6, 1, 3, 4, 4, 3, 1, -6},
        {-6, 0, 3, 4, 4, 3, 0, -6},
        {-6, 1, 2, 3, 3, 2, 1, -6},
        {-8, -4, 0, 1, 1, 0, -4, -8},
        {-10, -8, -6, -6, -6, -6, -8, -10}};

    private final int[][] bishopEvaluate = {
        {-4, -2, -2, -2, -2, -2, -2, -4},
        {-2, 0, 0, 0, 0, 0, 0, -2},
        {-2, 0, 1, 2, 2, 1, 0, -2},
        {-2, 1, 1, 2, 2, 1, 1, -2},
        {-2, 0, 2, 2, 2, 2, 0, -2},
        {-2, 2, 2, 2, 2, 2, 2, -2},
        {-2, 1, 0, 0, 0, 0, 1, -2},
        {-4, -2, -2, -2, -2, -2, -2, -4}};

    private final int[][] queenEvaluate = {
        {-4, -2, -2, -1, -1, -2, -2, -4},
        {-2, 0, 0, 0, 0, 0, 0, -2},
        {-2, 0, 1, 1, 1, 1, 0, -2},
        {-1, 0, 1, 1, 1, 1, 0, -1},
        {0, 0, 1, 1, 1, 1, 0, -1},
        {-2, 1, 1, 1, 1, 1, 0, -2},
        {-2, 0, 1, 0, 0, 0, 0, -2},
        {-4, -2, -2, -1, -1, -2, -2, -4}};

    private final int[][] kingEvaluate = {
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-6, -8, -8, -10, -10, -8, -8, -6},
        {-4, -6, -6, -8, -8, -6, -6, -4},
        {-2, -4, -4, -4, -4, -4, -4, -2},
        {4, 4, 0, 0, 0, 0, 4, 4},
        {4, 6, 2, 0, 0, 2, 6, 4}};

    private final Map<FigureType, int[][]> figureFieldMap = new HashMap<>();

    public StaticPositionMatrixEvaluateStrategy() {
        figureFieldMap.put(FigureType.PAWN, pawnEvaluate);
        figureFieldMap.put(FigureType.ROOK, rookEvaluate);
        figureFieldMap.put(FigureType.KNIGHT, knightEvaluate);
        figureFieldMap.put(FigureType.BISHOP, bishopEvaluate);
        figureFieldMap.put(FigureType.QUEEN, queenEvaluate);
        figureFieldMap.put(FigureType.KING, kingEvaluate);
    }

    @Override
    public int evaluateBoard(Board board, Color color) {
        int result = 0;
        for (Figure figure : board.getAllFigures()) {
            int coef = (figure.getColor() == color) ? 1 : -1;
            int inverse = figure.getColor() == Color.WHITE? 1 : -1;
            int val = FigureService.convertFigureToVal(figure);
            int rowCoord = (figure.getCurrentPosition().row - ((1 - inverse) / 2) * (Board.STD_BOARD_SIZE - 1)) * inverse;
            int columnCoord = (figure.getCurrentPosition().column - ((1 - inverse) / 2) * (Board.STD_BOARD_SIZE - 1)) * inverse;
            int cur = (coef * (val + figureFieldMap.get(figure.figureType)[rowCoord][columnCoord]));
            result += cur;
        }
        return result;
    }
}
