package io.deeplay.qchess.game.exceptions;

import java.io.Serial;

/** Класс для ошибок в комнате */
public class ChessError extends Exception {
    @Serial
    private static final long serialVersionUID = 232601964039528998L;

    public ChessError(ChessErrorCode code, Throwable cause) {
        super(code.getMessage(), cause);
    }

    public ChessError(ChessErrorCode code) {
        super(code.getMessage());
    }
}
