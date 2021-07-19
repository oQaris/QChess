package io.deeplay.qchess.client.exceptions;

public enum ClientErrorCode {
    FAILED_CONNECT("Не удалось подключиться к серверу"),
    ERROR_GET_SOCKET_INPUT("Ошибка получения потока ввода сокета"),
    ERROR_GET_SOCKET_OUTPUT("Ошибка получения потока вывода сокета"),
    CLIENT_IS_ALREADY_CONNECTED("Клиент уже подключен к серверу"),
    CLIENT_IS_NOT_CONNECTED("Клиент еще не подключен"),
    ERROR_CREATE_TRAFFIC_HANDLER("Ошибка создания обработчика трафика");

    private final String message;

    ClientErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
