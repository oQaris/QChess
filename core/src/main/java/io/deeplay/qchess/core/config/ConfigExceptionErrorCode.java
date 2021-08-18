package io.deeplay.qchess.core.config;

public enum ConfigExceptionErrorCode {
    READ_CONFIG_FILE("Ошибка чтения конфигурационного файла"),
    ABSENT_PATH("Ошибка валидации одного из path (поле отсутствует или пусто)"),
    ABSENT_TOURNAMENT(
            "Ошибка валидации значения поля tournamentNumberGame (поле отсутствует или пусто)"),
    INCORRECT_PATH("Ошибка валидации одного из path (путь записан некорректно)"),
    INCORRECT_TOURNAMENT_VALUE(
            "Ошибка валидации значения поля tournamentNumberGame (не удалось распарсить)"),
    NON_POSITIVE_TOURNAMENT_VALUE(
            "Ошибка валидации значения поля tournamentNumberGame (неположительное значение)");

    private final String message;

    ConfigExceptionErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
