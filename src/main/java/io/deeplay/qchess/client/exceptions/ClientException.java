package io.deeplay.qchess.client.exceptions;

public class ClientException extends RuntimeException {

    public ClientException(ClientErrorCode code, Throwable cause) {
        super(code.getMessage(), cause);
    }

    public ClientException(ClientErrorCode code) {
        super(code.getMessage());
    }
}
