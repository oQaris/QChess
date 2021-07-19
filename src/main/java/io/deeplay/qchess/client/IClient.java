package io.deeplay.qchess.client;

import io.deeplay.qchess.client.exceptions.ClientException;

public interface IClient {

    /**
     * Подключается к серверу
     *
     * @throws ClientException если клиент уже подключен к серверу или возникла ошибка при
     *     подключении
     */
    void connect(String ip, int port) throws ClientException;

    /**
     * Отключается от сервера
     *
     * @throws ClientException если клиент не подключен к серверу
     */
    void disconnect() throws ClientException;

    /** @return true, если клиент подключен к серверу, false иначе */
    boolean isConnected();

    /** @return порт сервера, к которому подключен клиент */
    int getPort();

    /**
     * Устанавливает порт сервера, к которому будет подключен клиент
     *
     * @throws ClientException если клиент уже подключен к серверу
     */
    void setPort(int port) throws ClientException;

    /** @return IP сервера, к которому подключен клиент */
    String getIp();

    /**
     * Устанавливает IP сервера, к которому будет подключен клиент
     *
     * @throws ClientException если клиент уже подключен к серверу
     */
    void setIp(String ip) throws ClientException;

    /**
     * Отправляет команду клиенту
     *
     * @throws ClientException если клиент не подключен к серверу
     */
    void sendCommand(String command) throws ClientException;
}
