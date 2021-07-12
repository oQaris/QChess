package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.figures.*;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;

import java.util.*;


public class NotationService {
    public static boolean checkValidityPlacement(String placement) {
        String[] placementRows = placement.split("/");
        return true;
    }

    public static Figure getFigureByChar(Character symbol, int x, int y) {
        Figure figure = null;
        Character lowerSymbol = Character.toLowerCase(symbol);
        Color figureColor = Character.isLowerCase(symbol)? Color.BLACK : Color.WHITE;
        Cell figureCell = new Cell(y, x);
        if(symbol == 'k') {
            figure = new King(figureColor, figureCell);
        } else if(symbol == 'q') {
            figure = new Queen(figureColor, figureCell);
        } else if(symbol == 'r') {
            figure = new Rook(figureColor, figureCell);
        } else if(symbol == 'b') {
            figure = new Bishop(figureColor, figureCell);
        } else if(symbol == 'n') {
            figure = new Knight(figureColor, figureCell);
        } else {
            figure = new Pawn(figureColor, figureCell);
        }
        return figure;
    }

    private boolean checkInappropriateCharacters(String placementRow) {
        char[] list = {'1', '2', '3', '4', '5', '6', '7', '8', 'K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r', 'b', 'n', 'p'};
        //String regex="\\d{3}"
        return true;
    }

    private boolean checkRowsNumber(String[] placementRows) {
        return placementRows.length == 8;
    }

    private boolean checkSumInRows() {
        return true;
    }

    private boolean checkFigureTypes() {
        return true;
    }
}
