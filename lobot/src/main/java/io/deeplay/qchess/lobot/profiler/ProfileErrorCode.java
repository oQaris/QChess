package io.deeplay.qchess.lobot.profiler;

public enum ProfileErrorCode {
    SAVE_ERROR("Ошибка сохранения профиля");
    private final String message;

    ProfileErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
