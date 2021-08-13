package io.deeplay.qchess.server.service.config;

public enum ConfigExceptionErrorCode {
    READ_CONFIG_FILE("Ошибка чтения конфигурационного файла"),

    ABSENT_PORT("Ошибка валидации значения поля port (поле отсутствует или пусто)"),
    ABSENT_MAX_PLAYERS("Ошибка валидации значения поля maxPlayers (поле отсутствует или пусто)"),
    ABSENT_PATH("Ошибка валидации одного из path (поле отсутствует или пусто)"),
    ABSENT_TOURNAMENT(
        "Ошибка валидации значения поля tournamentNumberGame (поле отсутствует или пусто)"),

    INCORRECT_PORT_VALUE("Ошибка валидации значения поля port (неудалось распарсить)"),
    INCORRECT_MAX_PLAYERS("Ошибка валидации значения поля maxPlayers (неудалось распарсить)"),
    INCORRECT_PATH("Ошибка валидации одного из path (путь записан некорректно)"),
    INCORRECT_TOURNAMENT_VALUE(
        "Ошибка валидации значения поля tournamentNumberGame (неудалось распарсить)"),

    NON_POSITIVE_PORT_VALUE("Ошибка валидации значения поля port (неположительное значение)"),
    NON_POSITIVE_MAX_PLAYERS_VALUE(
        "Ошибка валидации значения поля maxPlayers (неположительное значение)"),
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
