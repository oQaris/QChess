package io.deeplay.qchess.game.exceptions;

import java.io.Serial;

/** Класс для ошибок в игре */
public class ChessException extends Exception {
    @Serial private static final long serialVersionUID = 2392363526073814885L;

    public ChessException(ChessErrorCode code) {
        super(code.getMessage());
    }
}
