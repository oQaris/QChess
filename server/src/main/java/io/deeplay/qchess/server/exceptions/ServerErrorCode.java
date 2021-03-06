package io.deeplay.qchess.server.exceptions;

public enum ServerErrorCode {
    ERROR_WHILE_SERVER_OPENING("Ошибка при открытии сервера"),
    ERROR_PORT("Некорректный порт"),
    SERVER_IS_ALREADY_OPEN("Сервер уже открыт"),
    SERVER_IS_NOT_OPENED("Сервер еще не запущен"),
    ERROR_GET_SOCKET_INPUT("Ошибка получения потока ввода сокета"),
    ERROR_GET_SOCKET_OUTPUT("Ошибка получения потока вывода сокета"),
    ERROR_CREATE_CLIENT_HANDLER("Ошибка создания обработчика для клиента"),
    UNKNOWN_REQUEST("Неизвестный запрос");

    private final String message;

    ServerErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
