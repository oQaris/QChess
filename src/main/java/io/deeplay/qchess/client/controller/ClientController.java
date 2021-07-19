package io.deeplay.qchess.client.controller;

import io.deeplay.qchess.client.Client;
import io.deeplay.qchess.client.IClient;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.IClientView;
import java.util.Optional;

public class ClientController {
    private static final IClient client = Client.getInstance();
    private static IClientView view;

    /** @return окружение клиента */
    public static Optional<IClientView> getView() {
        return Optional.of(view);
    }

    /**
     * Устанавливает окружение клиента
     *
     * <p>Необходимо использовать рабочее окружение перед использованием клиента, иначе клиент может
     * работать некорректно либо вообще не работать. Если окружение равно null, оно будет отключено
     * и не будет использоваться клиентом. По умолчанию окружение стоит null
     *
     * @param view окружение клиента
     */
    public static void setView(IClientView view) {
        ClientController.view = view;
    }

    /**
     * Подключается к серверу
     *
     * @throws ClientException если клиент уже подключен к серверу или возникла ошибка при
     *     подключении
     */
    public static void connect(String ip, int port) throws ClientException {
        client.connect(ip, port);
    }

    /**
     * Отключается от сервера
     *
     * @throws ClientException если клиент не подключен к серверу
     */
    public static void disconnect() throws ClientException {
        client.disconnect();
    }

    /** @return true, если клиент подключен к серверу, false иначе */
    public static boolean isConnected() {
        return client.isConnected();
    }

    /** @return порт сервера, к которому подключен клиент */
    public static int getPort() {
        return client.getPort();
    }

    /**
     * Устанавливает порт сервера, к которому будет подключен клиент
     *
     * @throws ClientException если клиент уже подключен к серверу
     */
    public static void setPort(int port) throws ClientException {
        client.setPort(port);
    }

    /** @return IP сервера, к которому подключен клиент */
    public static String getIp() {
        return client.getIp();
    }

    /**
     * Устанавливает IP сервера, к которому будет подключен клиент
     *
     * @throws ClientException если клиент уже подключен к серверу
     */
    public static void setIp(String ip) throws ClientException {
        client.setIp(ip);
    }

    /**
     * Отправляет команду клиенту
     *
     * @throws ClientException если клиент не подключен к серверу
     */
    public static void sendCommand(String command) throws ClientException {
        client.sendCommand(command);
    }
}
