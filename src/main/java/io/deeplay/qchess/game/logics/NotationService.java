package io.deeplay.qchess.game.logics;

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

    public static Figure getFigureByChar(Character symbol, int x, int y) {
        Figure figure;
        char lowerSymbol = Character.toLowerCase(symbol);
        Color figureColor = Character.isLowerCase(symbol) ? Color.BLACK : Color.WHITE;
        Cell figureCell = new Cell(x, y);

        figure = switch (lowerSymbol) {
            case 'k' -> new King(figureColor, figureCell);
            case 'q' -> new Queen(figureColor, figureCell);
            case 'r' -> new Rook(figureColor, figureCell);
            case 'b' -> new Bishop(figureColor, figureCell);
            case 'n' -> new Knight(figureColor, figureCell);
            case 'p' -> new Pawn(figureColor, figureCell);
            default -> null;
        };

        return figure;
    }

    private static boolean checkInappropriateCharacters(String placementRow) {
        Character[] arr = {
            '1', '2', '3', '4', '5', '6', '7', '8', 'K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r',
            'b', 'n', 'p'
        };
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
                if (Character.isLowerCase(c) && (color == Color.BLACK)) {
                    result.add(c);
                } else if (Character.isUpperCase(c) && (color == Color.WHITE)) {
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

        if (figureMap.get('k') != 1) {
            return false;
        }

        int[] figureOverflow = {
            8 - figureMap.getOrDefault('p', 0),
            figureMap.getOrDefault('b', 0) - 2,
            figureMap.getOrDefault('r', 0) - 2,
            figureMap.getOrDefault('n', 0) - 2,
            figureMap.getOrDefault('q', 0) - 1
        };

        for (int i = 1; i < figureOverflow.length; i++) {
            if (figureOverflow[i] > 0) {
                figureOverflow[0] -= figureOverflow[i];
            }
        }

        return figureOverflow[0] >= 0;
    }
}
