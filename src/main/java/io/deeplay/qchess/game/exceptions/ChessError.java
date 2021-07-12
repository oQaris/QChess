package io.deeplay.qchess.game.exceptions;

/**
 * Класс для ошибок в комнате
 */
public class ChessError extends Exception {
    public ChessError(String msg) {
        super(msg);
    }

    public ChessError(String message, Throwable cause) {
        super(message, cause);
    }
}
