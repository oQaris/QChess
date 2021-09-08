package io.deeplay.qchess.nukebot.bot.exceptions;

import io.deeplay.qchess.game.exceptions.ChessError;

public class SearchAlgException extends RuntimeException {

    public SearchAlgException(final SearchAlgErrorCode code, final ChessError e) {
        super(code.message, e);
    }
}
