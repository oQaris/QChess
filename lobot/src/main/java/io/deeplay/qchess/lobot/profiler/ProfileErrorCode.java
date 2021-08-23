package io.deeplay.qchess.lobot.profiler;

public enum ProfileErrorCode {
    SAVE_ERROR("Ошибка сохранения профиля"),
    LOAD_ERROR("Ошибка загрузки профиля"),
    REGEX_ERROR("Ошибка парса профиля");
    private final String message;

    ProfileErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
