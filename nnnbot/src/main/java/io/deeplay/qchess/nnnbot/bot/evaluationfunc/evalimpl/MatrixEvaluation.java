package io.deeplay.qchess.nnnbot.bot.evaluationfunc.evalimpl;

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

    private static final Integer[][] pawnEval = {
        {800, 800, 800, 800, 800, 800, 800, 800},
        {290, 285, 280, 270, 270, 280, 285, 290},
        {12, 13, 15, 14, 14, 15, 13, 12},
        {12, 13, 14, 15, 15, 14, 13, 12},
        {12, 11, 15, 14, 15, 12, 11, 12},
        {11, 12, 14, 13, 14, 11, 12, 11},
        {10, 10, 10, 10, 10, 10, 10, 10},
        {9, 9, 9, 9, 9, 9, 9, 9}
    };
    private static final Integer[][] knightEval = {
        {40, 50, 60, 70, 70, 60, 50, 40},
        {50, 60, 75, 70, 70, 75, 60, 50},
        {70, 75, 80, 75, 75, 80, 75, 70},
        {75, 80, 85, 90, 90, 85, 80, 75},
        {75, 80, 85, 90, 90, 85, 80, 75},
        {70, 75, 80, 75, 75, 80, 75, 70},
        {50, 60, 75, 70, 70, 75, 60, 50},
        {40, 50, 60, 70, 70, 60, 50, 40}
    };
    private static final Integer[][] bishopEval = {
        {75, 75, 85, 100, 100, 85, 75, 75},
        {80, 85, 90, 85, 85, 90, 85, 80},
        {80, 80, 85, 90, 90, 85, 80, 80},
        {85, 85, 90, 95, 95, 90, 85, 85},
        {85, 85, 90, 95, 95, 90, 85, 85},
        {80, 80, 80, 85, 85, 80, 80, 80},
        {80, 70, 60, 70, 70, 60, 70, 80},
        {60, 50, 60, 50, 50, 60, 50, 60}
    };
    private static final Integer[][] rookEval = {
        {50, 60, 65, 70, 70, 65, 60, 50},
        {40, 50, 50, 50, 50, 50, 50, 40},
        {50, 60, 65, 60, 60, 65, 60, 50},
        {60, 65, 70, 70, 70, 70, 65, 60},
        {60, 65, 70, 70, 70, 70, 65, 60},
        {40, 60, 65, 65, 65, 65, 60, 40},
        {40, 55, 55, 60, 60, 55, 55, 40},
        {50, 55, 55, 60, 60, 55, 55, 50}
    };
    private static final Integer[][] queenEval = {
        {40, 45, 50, 50, 50, 50, 45, 40},
        {45, 50, 55, 55, 55, 55, 50, 45},
        {50, 60, 65, 65, 65, 65, 60, 50},
        {60, 65, 70, 70, 70, 70, 65, 60},
        {60, 65, 70, 70, 70, 70, 65, 60},
        {50, 60, 65, 65, 65, 65, 60, 50},
        {45, 50, 55, 55, 55, 55, 50, 45},
        {40, 45, 50, 50, 50, 50, 45, 40}
    };
    private static final Integer[][] kingEval = {
        {70, 70, 75, 75, 75, 75, 70, 70},
        {70, 70, 70, 70, 70, 70, 70, 70},
        {65, 60, 60, 55, 55, 60, 60, 65},
        {65, 60, 55, 50, 50, 55, 60, 65},
        {80, 70, 60, 50, 50, 60, 70, 80},
        {90, 85, 80, 80, 80, 80, 85, 90},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 100, 100, 99, 99, 99}
    };

    private static final Map<FigureType, Integer[][]> evaluations;
    private static final Map<FigureType, Integer> costInPawns;

    static {
        costInPawns = new EnumMap<>(FigureType.class);
        costInPawns.put(FigureType.PAWN, 100);
        costInPawns.put(FigureType.KNIGHT, 320);
        costInPawns.put(FigureType.BISHOP, 330);
        costInPawns.put(FigureType.ROOK, 500);
        costInPawns.put(FigureType.QUEEN, 900);
        costInPawns.put(FigureType.KING, 20000);
    }

    static {
        evaluations = new EnumMap<>(FigureType.class);
        evaluations.put(FigureType.PAWN, pawnEval);
        evaluations.put(FigureType.KNIGHT, knightEval);
        evaluations.put(FigureType.BISHOP, bishopEval);
        evaluations.put(FigureType.ROOK, rookEval);
        evaluations.put(FigureType.QUEEN, queenEval);
        evaluations.put(FigureType.KING, kingEval);

        // TODO: добавить проверку, что стоимости фигур пропорциональны стоимости позиции
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
                Figure figure = gs.board.getFigureUgly(row, column);
                if (figure == null) continue;
                int r = figure.getColor() == Color.BLACK ? 7 - row : row;

                int eval =
                        evaluations.get(figure.figureType)[r][column]
                                + costInPawns.get(figure.figureType);
                if (figure.getColor() == myColor) myEstimation += eval;
                else enemyEstimation += eval;
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
