package io.deeplay.qchess.client.exceptions;

public class ClientException extends Exception {

    public ClientException(final ClientErrorCode code, final Throwable cause) {
        super(code.getMessage(), cause);
    }

    public ClientException(final ClientErrorCode code) {
        super(code.getMessage());
    }
}
