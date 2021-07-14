package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessErrorCode;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotationService {
    private static final Character[] arr = {
        '1', '2', '3', '4', '5', '6', '7', '8', 'K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r',
        'b', 'n', 'p'
    };

    private static final int PAWN_COUNT = 8;
    private static final int KQ_COUNT = 1;
    private static final int RNB_COUNT = 2;

    /**
     * @return true если строка с расстаовкой является корректной, false - некорректной
     */
    public static boolean checkValidityPlacement(String placement) {
        String[] placementRows = placement.split("/");
        for (String placementRow : placementRows) {
            if (!checkInappropriateCharacters(placementRow) || !checkSumInRows(placementRow)) {
                return false;
            }
        }
        return checkRowsNumber(placementRows)
                && checkFigureTypes(getAllFigureSymbols(placement, Color.BLACK))
                && checkFigureTypes(getAllFigureSymbols(placement, Color.WHITE));
    }

    /**
     * @return возвращает конкретную фигуру по входному символу, в позиции x,y
     */
    public static Figure getFigureByChar(Character symbol, int x, int y) throws ChessException {
        char lowerSymbol = Character.toLowerCase(symbol);
        Color figureColor = Character.isLowerCase(symbol) ? Color.BLACK : Color.WHITE;
        Cell figureCell = new Cell(x, y);

        return switch (lowerSymbol) {
            case 'k' -> new King(figureColor, figureCell);
            case 'q' -> new Queen(figureColor, figureCell);
            case 'r' -> new Rook(figureColor, figureCell);
            case 'b' -> new Bishop(figureColor, figureCell);
            case 'n' -> new Knight(figureColor, figureCell);
            case 'p' -> new Pawn(figureColor, figureCell);
            default -> throw new ChessException(ChessErrorCode.PARSE_FIGURE_FROM_CHAR_FAILED);
        };
    }

    private static boolean checkInappropriateCharacters(String placementRow) {
        Set<Character> set = new HashSet<>(Arrays.asList(arr));
        for (Character c : placementRow.toCharArray()) {
            if (!set.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkRowsNumber(String[] placementRows) {
        return placementRows.length == 8;
    }

    private static boolean checkSumInRows(String placementRow) {
        int place = 0;
        for (Character c : placementRow.toCharArray()) {
            if (Character.isDigit(c)) {
                place += Integer.parseInt(String.valueOf(c));
            } else {
                place++;
            }
        }
        return place == 8;
    }

    private static List<Character> getAllFigureSymbols(String placement, Color color) {
        List<Character> result = new ArrayList<>(16);
        for (Character c : placement.toCharArray()) {
            if (!Character.isDigit(c) && !c.equals('/')) {
                if ((Character.isLowerCase(c) && (color == Color.BLACK)) || (Character.isUpperCase(c) && (color == Color.WHITE))) {
                    result.add(c);
                }
            }
        }
        return result;
    }

    private static boolean checkFigureTypes(List<Character> figureList) {
        Map<Character, Integer> figureMap = new HashMap<>();
        for (Character c : figureList) {
            figureMap.put(
                    Character.toLowerCase(c),
                    figureMap.getOrDefault(Character.toLowerCase(c), 0) + 1);
        }

        if (figureMap.get('k') != KQ_COUNT) {
            return false;
        }

        int[] figureOverflow = {
            PAWN_COUNT - figureMap.getOrDefault('p', 0),
            figureMap.getOrDefault('b', 0) - RNB_COUNT,
            figureMap.getOrDefault('r', 0) - RNB_COUNT,
            figureMap.getOrDefault('n', 0) - RNB_COUNT,
            figureMap.getOrDefault('q', 0) - KQ_COUNT
        };

        for (int i = 1; i < figureOverflow.length; i++) {
            if (figureOverflow[i] > 0) {
                figureOverflow[0] -= figureOverflow[i];
            }
        }

        return figureOverflow[0] >= 0;
    }
}
