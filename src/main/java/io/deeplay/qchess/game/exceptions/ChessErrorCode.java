package io.deeplay.qchess.game.exceptions;

public enum ChessErrorCode {
    INCORRECT_COORDINATES("Координаты выходят за границу доски"),
    KING_NOT_FOUND("Король не найден"),
    INCORRECT_FILLING_BOARD("Заполнение доски некорректное"),
    BOT_ERROR("В боте возникло исключение"),
    CONSOLE_PLAYER_ERROR("Произошла ошибка в классе игрока");

    private final String message;

    ChessErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}