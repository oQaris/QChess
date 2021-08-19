package io.deeplay.qchess.logparser;

public enum ParseErrorCode {
    NULL_PARSE_MODE("Передан пустой ParseMode"),
    NON_POSITIVE_PARSE_MODE_PARAMETER("Передано неположительное значение для ParseMode"),
    WRONG_DIRECTORY("Переданная сущность не является директорией");
    private final String message;

    ParseErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
