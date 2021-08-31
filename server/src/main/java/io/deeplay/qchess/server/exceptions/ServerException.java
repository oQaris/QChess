package io.deeplay.qchess.server.exceptions;

public class ServerException extends Exception {

    public ServerException(final ServerErrorCode code, final Throwable cause) {
        super(code.getMessage(), cause);
    }

    public ServerException(final ServerErrorCode code) {
        super(code.getMessage());
    }
}
