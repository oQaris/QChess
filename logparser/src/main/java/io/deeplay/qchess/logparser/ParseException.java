package io.deeplay.qchess.logparser;

public class ParseException extends Exception {
    private final ParseErrorCode pec;

    public ParseException(final ParseErrorCode pec) {
        this.pec = pec;
    }

    public ParseErrorCode getErrorCode() {
        return pec;
    }

    @Override
    public String getMessage() {
        return pec.getMessage();
    }
}
