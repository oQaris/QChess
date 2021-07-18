package io.deeplay.qchess.server;

import io.deeplay.qchess.server.exceptions.ServerException;

public interface IServer {

    /**
     * Открывает сервер
     *
     * @throws ServerException если сервер уже открыт или возникла ошибка при открытии
     */
    void startServer() throws ServerException;

    /**
     * Закрывает сервер
     *
     * @throws ServerException если сервер не запущен или возникла ошибка при закрытии
     */
    void stopServer() throws ServerException;

    /** @return true, если сервер открыт, false иначе */
    boolean isOpen();

    /** @return максимально допустимое количество клиентов на сервере */
    int getMaxClients();

    /**
     * Устанавливает максимально допустимое количество клиентов на сервере
     *
     * @throws ServerException если сервер уже открыт
     */
    void setMaxClients(int maxClients) throws ServerException;

    /** @return порт, на котором запущен сервер */
    int getPort();

    /**
     * Устанавливает порт, на котором будет запущен сервер
     *
     * @throws ServerException если сервер уже открыт
     */
    void setPort(int port) throws ServerException;

    void sendMessageAll(String message);
}
