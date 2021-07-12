package io.deeplay.qchess.game.exceptions;

/**
 * Класс для ошибок в комнате
 */
public class ChessError extends Exception {

    public ChessError(String msg) {
        super(msg);
    }

    public ChessError(ChessErrorCode code, Throwable cause) {
        super(code.getMessage(), cause);
    }

    public ChessError(ChessErrorCode code) {
        super(code.getMessage());
    }
}