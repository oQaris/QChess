package io.deeplay.qchess.client.config;

public enum ConfigExceptionEnum {
    READ_CONFIG_FILE("Ошибка чтения конфигурационного файла"),

    ABSENT_IP("Ошибка валидации ip (поле ip отсутствует или пусто)"),
    ABSENT_PORT("Ошибка валидации port (поле port отсутствует или пусто)"),
    ABSENT_BOOLEAN( "Ошибка валидации одного из boolean значений (boolean поле отсутствует или пусто)"),
    ABSENT_PLAYER_TYPE("Ошибка валидации playerType (поле playerType отсутствует или пусто)"),
    ABSENT_PATH("Ошибка валидации одного из path (path поле отсутствует или пусто)"),
    ABSENT_COLOR("Ошибка валидации color (поле color отсутствует или пусто)"),

    INCORRECT_PORT_VALUE("Ошибка валидации port (неудалось распарсить)"),
    INCORRECT_BOOLEAN_VALUE("Ошибка валидации одного из boolean значений (неудалось распарсить)"),
    INCORRECT_PLAYER_TYPE_VALUE("Ошибка валидации playerType parse error (неудалось распарсить)"),
    INCORRECT_PATH("Ошибка валидации одного из path (путь записан некорректно)"),
    INCORRECT_COLOR("Ошибка валидации color (неудалось распарсить)"),

    INCORRECT_IP_OCTETS_NUMBER("Ошибка валидации ip (ip состоит не из 4 октетов)"),
    INCORRECT_IP_OCTET_VALUE("Ошибка валидации ip (неудалось распарсить октет)"),
    RANGE_OUT_IP_OCTET("Ошибка валидации ip (октет вне диапаона [0..255])"),
    NON_POSITIVE_PORT_VALUE("Ошибка валидации port (неположительное значение)");

    private final String message;
    ConfigExceptionEnum(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
