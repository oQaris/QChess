package io.deeplay.qchess.game.exceptions;

public enum ChessErrorCode {
    INCORRECT_COORDINATES("Координаты выходят за границу доски"),
    KING_NOT_FOUND("Король не найден"),
    KING_WAS_KILLED_IN_VIRTUAL_MOVE("Срубили короля при проверке виртуального хода"),
    INCORRECT_FILLING_BOARD("Заполнение доски некорректное"),
    INCORRECT_STRING_FOR_FILLING_BOARD("Заполнение из доски из некорректной строки"),
    BOT_ERROR("В боте возникло исключение"),
    CONSOLE_PLAYER_ERROR("Произошла ошибка в классе игрока"),
    ERROR_WHEN_MOVING_FIGURE("Проверенный ход выдал ошибку при перемещении фигуры"),
    ERROR_WHILE_ADD_PEACE_MOVE_COUNT("Ошибка при добавлении ходов для ничьи"),
    PARSE_FIGURE_FROM_CHAR_FAILED("Ошибка создания фигуры по символу"),
    UNKNOWN_FIGURE_SELECTED("Выбрана неизвестная фигура"),
    GAME_RESULT_ERROR("Игра еще не закончена");

    private final String message;

    ChessErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
