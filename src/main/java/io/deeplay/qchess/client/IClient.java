package io.deeplay.qchess.client;

import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.clientserverconversation.dto.GetRequestType;
import io.deeplay.qchess.clientserverconversation.dto.GetRequestDTO;

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
     * Эта операция блокирует поток, пока не будет получено сообщение от сервера или не возникнет
     * исключение
     *
     * @param getRequestType запрос, на который нужно ждать ответ
     * @return сообщение от сервера
     * @throws ClientException если клиент не подключен к серверу или во время ожидания соединение
     *     было разорвано
     */
    GetRequestDTO waitForResponse(GetRequestType getRequestType) throws ClientException;

    /**
     * Выполняет команду клиента
     *
     * @throws ClientException если при выполнении команды возникла ошибка
     */
    void executeCommand(String command) throws ClientException;

    /**
     * Отправляет серверу строку, если она не null
     *
     * @throws ClientException если клиент не подключен к серверу
     */
    void sendIfNotNull(String json) throws ClientException;
}
