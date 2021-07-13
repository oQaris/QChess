package io.deeplay.qchess.game.exceptions;

public enum ChessErrorCode {
    INCORRECT_COORDINATES("Координаты выходят за границу доски"),
    KING_NOT_FOUND("Король не найден"),
    INCORRECT_FILLING_BOARD("Заполнение доски некорректное"),
    INCORRECT_STRING_FOR_FILLING_BOARD("Заполнение из доски из некорректной строки"),
    BOT_ERROR("В боте возникло исключение"),
    CONSOLE_PLAYER_ERROR("Произошла ошибка в классе игрока"),
    ERROR_WHEN_MOVING_FIGURE("Проверенный ход выдал ошибку при перемещении фигуры"),
    ERROR_WHILE_CHECKING_FOR_DRAW("Ошибка при проверки на ничью"),
    EXCEPTION_IN_HISTORY("Возникло исключение в истории"),
    LOG_FAILED("Не удалось записать в лог"),
    PARSE_FIGURE_FROM_CHAR_FAILED(""),
    UNKNOWN_FIGURE_SELECTED("Выбрана неизвестная фигура");

    private final String message;

    ChessErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
