package io.deeplay.qchess.server.exceptions;

public class ServerException extends RuntimeException {

    public ServerException(ServerErrorCode code, Throwable cause) {
        super(code.getMessage(), cause);
    }

    public ServerException(ServerErrorCode code) {
        super(code.getMessage());
    }
}
