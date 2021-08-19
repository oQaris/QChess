package io.deeplay.qchess.logparser;

public enum ParseErrorCode {
    NULL_PARSE_MODE("Передан пустой ParseMode"),
    NON_POSITIVE_PARSE_MODE_PARAMETER("Передано неположительное значение для ParseMode"),
    WRONG_DIRECTORY("Переданная сущность не является директорией"),
    DIRECTORY_OPEN_FAILED("Не удалось открыть папку"),
    WRONG_LOGS_COUNT("Передано некорректное количество логов для распознавания"),
    FILE_OPEN_ERROR("Неудалось открыть файл");
    private final String message;

    ParseErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
