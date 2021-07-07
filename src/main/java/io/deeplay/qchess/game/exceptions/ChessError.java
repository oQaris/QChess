package io.deeplay.qchess.game.exceptions;

/**
 * Класс для невозможных ситуаций во время выполнения
 */
public class ChessError extends Exception {

    public ChessError(String msg) {
        super(msg);
    }
}