package io.deeplay.qchess.qbot.strategy;

import static io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Матричная стратегия оценки доски, учитывает положение фигур на доске. */
public class MatrixStrategy extends Strategy {
    private static final Logger logger = LoggerFactory.getLogger(MatrixStrategy.class);
    private static final Map<FigureType, Integer[][]> grades;
    private static final Integer[][] pawnEval = {
        {20, 20, 20, 20, 20, 20, 20, 20},
        {30, 30, 30, 30, 30, 30, 30, 30},
        {22, 22, 24, 26, 26, 24, 22, 22},
        {21, 21, 22, 25, 25, 22, 21, 21},
        {20, 20, 20, 24, 24, 20, 20, 20},
        {21, 19, 18, 20, 20, 18, 19, 21},
        {21, 22, 22, 16, 16, 22, 22, 21},
        {20, 20, 20, 20, 20, 20, 20, 20},
    };
    private static final Integer[][] knightEval = {
        {50, 52, 54, 54, 54, 54, 52, 50},
        {52, 56, 60, 60, 60, 60, 56, 52},
        {54, 60, 62, 63, 63, 62, 60, 54},
        {54, 61, 63, 64, 64, 63, 61, 54},
        {54, 60, 63, 64, 64, 63, 60, 54},
        {54, 61, 62, 63, 63, 62, 61, 54},
        {52, 56, 60, 61, 61, 60, 56, 52},
        {50, 52, 54, 54, 54, 54, 52, 50},
    };
    private static final Integer[][] bishopEval = {
        {56, 58, 58, 58, 58, 58, 58, 56},
        {58, 60, 60, 60, 60, 60, 60, 58},
        {58, 60, 61, 62, 62, 61, 60, 58},
        {58, 61, 61, 62, 62, 61, 61, 58},
        {58, 60, 62, 62, 62, 62, 60, 58},
        {58, 62, 62, 62, 62, 62, 62, 58},
        {58, 61, 60, 60, 60, 60, 61, 58},
        {56, 58, 58, 58, 58, 58, 58, 56},
    };
    private static final Integer[][] rookEval = {
        {100, 100, 100, 100, 100, 100, 100, 100},
        {101, 102, 102, 102, 102, 102, 102, 101},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {100, 100, 100, 101, 101, 100, 100, 100},
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

    static {
        final Map<FigureType, Integer[][]> res = new EnumMap<>(FigureType.class);
        res.put(FigureType.PAWN, pawnEval);
        res.put(FigureType.KNIGHT, knightEval);
        res.put(FigureType.BISHOP, bishopEval);
        res.put(FigureType.ROOK, rookEval);
        res.put(FigureType.QUEEN, queenEval);
        res.put(FigureType.KING, kingEval);
        grades = Collections.unmodifiableMap(res);
        logger.debug("Карта ценностей успешно заполнена");
    }

    @Override
    public int evaluateBoard(Board board) {
        int grade = 0;
        for (Figure figure : board.getAllFigures()) {
            final int column = figure.getCurrentPosition().column;
            final int tempRow = figure.getCurrentPosition().row;
            // разворачиваем массив ценностей для чёрных
            final int row =
                    figure.getColor() == Color.BLACK ? STD_BOARD_SIZE - 1 - tempRow : tempRow;

            final int coef = figure.getColor() == Color.WHITE ? 1 : -1;
            grade += coef * grades.get(figure.figureType)[row][column];
        }
        logger.debug("Текущая оценка доски: {}", grade);
        return grade;
    }
}
