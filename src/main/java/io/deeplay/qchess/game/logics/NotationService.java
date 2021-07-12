package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.figures.*;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;


public class NotationService {
    public static boolean checkValidityPlacement(String placement) {
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
}
