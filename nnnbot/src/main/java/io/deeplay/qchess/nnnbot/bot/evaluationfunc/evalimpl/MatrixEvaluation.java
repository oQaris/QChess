package io.deeplay.qchess.nnnbot.bot.evaluationfunc.evalimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.EnumMap;
import java.util.Map;

public class MatrixEvaluation {

    private static final Integer[][] pawnEval = {
        {800, 800, 800, 800, 800, 800, 800, 800},
        {290, 290, 290, 290, 290, 290, 290, 290},
        {100, 100, 100, 100, 100, 100, 100, 100},
        {20, 20, 20, 20, 20, 20, 20, 20},
        {15, 15, 15, 15, 15, 15, 15, 15},
        {11, 11, 11, 11, 11, 11, 11, 11},
        {10, 10, 10, 10, 10, 10, 10, 10},
        {9, 9, 9, 9, 9, 9, 9, 9},
    };
    private static final Integer[][] knightEval = {
        {65, 75, 85, 95, 95, 85, 75, 65},
        {90, 100, 120, 100, 100, 120, 100, 90},
        {80, 90, 100, 100, 100, 100, 90, 80},
        {80, 85, 90, 90, 90, 90, 85, 80},
        {75, 80, 85, 90, 90, 85, 80, 75},
        {70, 75, 70, 75, 75, 70, 75, 70},
        {50, 60, 75, 70, 70, 75, 60, 50},
        {40, 50, 60, 70, 70, 60, 50, 40},
    };
    private static final Integer[][] bishopEval = {
        {75, 75, 85, 95, 95, 85, 75, 75},
        {80, 90, 100, 120, 120, 100, 90, 80},
        {90, 100, 100, 95, 95, 100, 100, 90},
        {100, 100, 90, 95, 95, 90, 100, 100},
        {100, 90, 90, 85, 85, 90, 90, 100},
        {85, 80, 80, 90, 90, 80, 80, 85},
        {80, 70, 60, 70, 70, 60, 70, 80},
        {60, 50, 60, 50, 50, 60, 50, 60},
    };
    private static final Integer[][] rookEval = {
        {120, 120, 110, 130, 130, 110, 120, 120},
        {90, 100, 100, 100, 100, 100, 100, 90},
        {100, 110, 115, 110, 110, 115, 110, 100},
        {110, 115, 120, 120, 120, 120, 115, 110},
        {110, 115, 120, 120, 120, 120, 115, 110},
        {90, 110, 115, 110, 110, 115, 110, 90},
        {90, 105, 105, 110, 110, 105, 105, 90},
        {100, 100, 100, 120, 120, 100, 100, 100},
    };
    private static final Integer[][] queenEval = {
        {176, 178, 178, 179, 179, 178, 178, 176},
        {178, 180, 180, 180, 180, 180, 180, 178},
        {178, 180, 181, 181, 181, 181, 180, 178},
        {179, 180, 181, 181, 181, 181, 180, 179},
        {179, 180, 181, 181, 181, 181, 180, 179},
        {178, 181, 181, 181, 181, 181, 180, 178},
        {178, 180, 181, 180, 180, 180, 180, 178},
        {176, 178, 178, 179, 179, 178, 178, 176},
    };
    private static final Integer[][] kingEval = {
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1796, 1794, 1794, 1792, 1792, 1794, 1794, 1796},
        {1798, 1796, 1796, 1796, 1796, 1796, 1796, 1798},
        {1804, 1804, 1800, 1800, 1800, 1800, 1804, 1804},
        {1804, 1806, 1802, 1800, 1800, 1802, 1806, 1804},
    };

    private static final Map<FigureType, Integer[][]> evaluations;
    private static final Map<FigureType, Integer> constInPawns;

    static {
        constInPawns = new EnumMap<>(FigureType.class);
        constInPawns.put(FigureType.PAWN, 100);
        constInPawns.put(FigureType.KNIGHT, 320);
        constInPawns.put(FigureType.BISHOP, 330);
        constInPawns.put(FigureType.ROOK, 500);
        constInPawns.put(FigureType.QUEEN, 900);
        constInPawns.put(FigureType.KING, 20000);
    }

    static {
        evaluations = new EnumMap<>(FigureType.class);
        evaluations.put(FigureType.PAWN, pawnEval);
        evaluations.put(FigureType.KNIGHT, knightEval);
        evaluations.put(FigureType.BISHOP, bishopEval);
        evaluations.put(FigureType.ROOK, rookEval);
        evaluations.put(FigureType.QUEEN, queenEval);
        evaluations.put(FigureType.KING, kingEval);
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

                int eval = evaluations.get(figure.figureType)[r][column];
                if (figure.getColor() == myColor) myEstimation += eval;
                else enemyEstimation += eval;
            }
        return 5 * myEstimation - 4 * enemyEstimation;
    }

    /**
     * Эвристика потенциальной атаки на фигуры
     *
     * @param myColor цвет игрока, который атакует соперника
     */
    public static int figureAttackHeuristics(GameSettings gs, Color myColor) {
        int myMoveCount = gs.moveSystem.getMoveCounts(myColor);
        int enemyMoveCount = gs.moveSystem.getMoveCounts(myColor.inverse());
        return myMoveCount - enemyMoveCount;
    }

    /**
     * Эвристика подсчета стоимости фигур и возможность потенциальной атаки на них
     *
     * @param myColor цвет игрока, который атакует соперника
     */
    public static int ultimateHeuristics(GameSettings gs, Color myColor) {
        return figurePositionHeuristics(gs, myColor) + 20 * figureAttackHeuristics(gs, myColor);
    }
}
