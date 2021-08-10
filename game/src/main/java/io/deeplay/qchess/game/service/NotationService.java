package io.deeplay.qchess.game.service;

import static io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE;

import io.deeplay.qchess.game.exceptions.ChessErrorCode;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.King;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.Rook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Используется для нотации Форсайта-Эдвардса (FEN) */
public class NotationService {
    private static final Set<Character> appropriateCharacters =
            Set.of(
                    '1', '2', '3', '4', '5', '6', '7', '8', 'K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q',
                    'r', 'b', 'n', 'p');

    private static final int PAWN_COUNT = 8;
    private static final int KQ_COUNT = 1;
    private static final int RNB_COUNT = 2;

    /** @return true если строка с расстаовкой является корректной, false - некорректной */
    public static boolean checkValidityPlacement(final String placement) {
        final String[] placementRows = placement.split("/");
        for (final String placementRow : placementRows) {
            if (!checkInappropriateCharacters(placementRow) || !checkSumInRows(placementRow)) {
                return false;
            }
        }
        return checkRowsNumber(placementRows)
                && checkTwoKings(getPlacementTable(placementRows))
                && checkFigureTypes(getAllFigureSymbols(placement, Color.BLACK))
                && checkFigureTypes(getAllFigureSymbols(placement, Color.WHITE));
    }

    /** @return возвращает конкретную фигуру по входному символу, в позиции x,y */
    public static Figure getFigureByChar(final Character symbol, final int x, final int y)
            throws ChessException {
        final char lowerSymbol = Character.toLowerCase(symbol);
        final Color figureColor = Character.isLowerCase(symbol) ? Color.BLACK : Color.WHITE;
        final Cell figureCell = new Cell(x, y);

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

    private static boolean checkInappropriateCharacters(final String placementRow) {
        for (final Character c : placementRow.toCharArray()) {
            if (!appropriateCharacters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkRowsNumber(final String[] placementRows) {
        return placementRows.length == 8;
    }

    private static boolean checkSumInRows(final String placementRow) {
        int place = 0;
        for (final Character c : placementRow.toCharArray()) {
            if (Character.isDigit(c)) {
                place += Integer.parseInt(String.valueOf(c));
            } else {
                ++place;
            }
        }
        return place == 8;
    }

    private static char[][] getPlacementTable(final String[] placementRows) {
        final char[][] placementTable = new char[STD_BOARD_SIZE + 2][STD_BOARD_SIZE + 2];
        for (final char[] row : placementTable) {
            Arrays.fill(row, 'e');
        }

        for (int y = 1; y < STD_BOARD_SIZE + 1; y++) {
            int x = 1;
            for (final Character c : placementRows[y - 1].toCharArray()) {
                if (Character.isDigit(c)) {
                    x += Integer.parseInt(String.valueOf(c));
                } else {
                    placementTable[y][x] = Character.toLowerCase(c);
                    ++x;
                }
            }
        }
        return placementTable;
    }

    private static boolean checkTwoKings(final char[][] placementTable) {
        for (int y = 1; y < STD_BOARD_SIZE + 2; y++) {
            for (int x = 1; x < STD_BOARD_SIZE + 2; x++) {
                if (placementTable[y][x] == 'k') {
                    for (int detour = 0; detour < 9; detour++) {
                        if (detour != 4
                                && placementTable[(detour / 3) - 1 + y][(detour % 3) - 1 + x]
                                        == 'k') {
                            return false;
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }

    private static List<Character> getAllFigureSymbols(final String placement, final Color color) {
        final List<Character> result = new ArrayList<>(16);
        for (final Character c : placement.toCharArray()) {
            if (!Character.isDigit(c) && !c.equals('/')) {
                if ((Character.isLowerCase(c) && (color == Color.BLACK))
                        || (Character.isUpperCase(c) && (color == Color.WHITE))) {
                    result.add(c);
                }
            }
        }
        return result;
    }

    private static boolean checkFigureTypes(final List<Character> figureList) {
        final Map<Character, Integer> figureMap = new HashMap<>();
        for (final Character c : figureList) {
            figureMap.put(
                    Character.toLowerCase(c),
                    figureMap.getOrDefault(Character.toLowerCase(c), 0) + 1);
        }

        if (figureMap.getOrDefault('k', 0) != KQ_COUNT) {
            return false;
        }

        final int[] figureOverflow = {
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
