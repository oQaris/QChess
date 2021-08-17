package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.HashMap;
import java.util.Map;

public class FigureService {
    private static final Map<FigureType, Character> charFigureMap = new HashMap<>();
    private static final Map<Character, Integer> intCharMap = new HashMap<>();
    private static final Map<FigureType, Integer> figureValMap = new HashMap<>();

    static {
        charFigureMap.put(FigureType.PAWN, 'p');
        charFigureMap.put(FigureType.ROOK, 'r');
        charFigureMap.put(FigureType.KNIGHT, 'n');
        charFigureMap.put(FigureType.BISHOP, 'b');
        charFigureMap.put(FigureType.QUEEN, 'q');
        charFigureMap.put(FigureType.KING, 'k');

        intCharMap.put('P', 10);
        intCharMap.put('N', 30);
        intCharMap.put('B', 30);
        intCharMap.put('R', 50);
        intCharMap.put('Q', 90);
        intCharMap.put('K', 900);
        intCharMap.put('p', -10);
        intCharMap.put('n', -30);
        intCharMap.put('b', -30);
        intCharMap.put('r', -50);
        intCharMap.put('q', -90);
        intCharMap.put('k', -900);

        figureValMap.put(FigureType.PAWN, 20);
        figureValMap.put(FigureType.KNIGHT, 60);
        figureValMap.put(FigureType.BISHOP, 60);
        figureValMap.put(FigureType.ROOK, 100);
        figureValMap.put(FigureType.QUEEN, 180);
        figureValMap.put(FigureType.KING, 1800);
    }

    @Deprecated
    public static char convertFigureToChar(final Figure figure) {
        return charFigureMap.get(figure.figureType);
    }

    @Deprecated
    public static int convertCharFigureToInt(final char cell) {
        return intCharMap.get(Character.toLowerCase(cell));
    }

    public static int convertFigureToVal(final Figure figure) {
        return figureValMap.get(figure.figureType);
    }
}
