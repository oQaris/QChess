package io.deeplay.qchess.game.exceptions;

public class ChessException extends Exception {

    private String msg;

    public ChessException() {
    }

    public ChessException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
