package io.deeplay.qchess.lobot.profiler;

public class ProfileException extends Exception {
    private final ProfileErrorCode pec;

    public ProfileException(final ProfileErrorCode pec) {
        this.pec = pec;
    }

    public ProfileErrorCode getErrorCode() {
        return pec;
    }

    @Override
    public String getMessage() {
        return pec.getMessage();
    }
}

