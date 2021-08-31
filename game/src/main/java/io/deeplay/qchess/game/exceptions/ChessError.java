package io.deeplay.qchess.game.exceptions;

/** Класс для ошибок в комнате */
public class ChessError extends Exception {

    public ChessError(final ChessErrorCode code, final Throwable cause) {
        super(code.getMessage(), cause);
    }

    public ChessError(final ChessErrorCode code) {
        super(code.getMessage());
    }
}
