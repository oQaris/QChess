package io.deeplay.qchess.game.exceptions;

/** Класс для ошибок в игре */
public class ChessException extends Exception {

    public ChessException(final ChessErrorCode code) {
        super(code.getMessage());
    }
}
