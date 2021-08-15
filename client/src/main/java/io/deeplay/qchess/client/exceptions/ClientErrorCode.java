package io.deeplay.qchess.client.exceptions;

public enum ClientErrorCode {
    FAILED_CONNECT("Не удалось подключиться к серверу"),
    ERROR_GET_SOCKET_INPUT("Ошибка получения потока ввода сокета"),
    ERROR_GET_SOCKET_OUTPUT("Ошибка получения потока вывода сокета"),
    CLIENT_IS_ALREADY_CONNECTED("Клиент уже подключен к серверу"),
    CLIENT_IS_NOT_CONNECTED("Клиент еще не подключен"),
    CONNECTION_WAS_BROKEN("Соединение было разорвано"),
    ERROR_CREATE_TRAFFIC_HANDLER("Ошибка создания обработчика трафика"),
    UNKNOWN_REQUEST("Неизвестный запрос");

    private final String message;

    ClientErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
