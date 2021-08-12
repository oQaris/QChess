package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Простая стратегия оценки доски, учитывающая количество и ценность фигур. */
public class SimpleStrategy implements Strategy {
    private static final Logger logger = LoggerFactory.getLogger(SimpleStrategy.class);
    private static final Map<FigureType, Integer> grades;

    static {
        final Map<FigureType, Integer> res = new EnumMap<>(FigureType.class);
        res.put(FigureType.PAWN, 1);
        res.put(FigureType.KNIGHT, 3);
        res.put(FigureType.BISHOP, 3);
        res.put(FigureType.ROOK, 5);
        res.put(FigureType.QUEEN, 9);
        res.put(FigureType.KING, 100);
        grades = Collections.unmodifiableMap(res);
        logger.debug("Карта ценностей успешно заполнена");
    }

    @Override
    public int evaluateBoard(final Board board) {
        int grade = 0;
        for (final Figure figure : board.getAllFigures()) {
            final int coef = figure.getColor() == Color.WHITE ? 1 : -1;
            grade += coef * grades.get(figure.figureType);
        }
        logger.debug("Текущая оценка доски: {}", grade);
        return grade;
    }
}
