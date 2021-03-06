package io.deeplay.qchess.server.controller;

import io.deeplay.qchess.server.IServer;
import io.deeplay.qchess.server.LocalHost;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.view.IServerView;

/** Нужен для связи: View <-> Controller <-> Model */
public class ServerController {
    private static final IServer server = LocalHost.getInstance();
    private static IServerView view;

    private ServerController() {}

    /** Отправляет сообщение View, если view и message не null */
    public static void print(final String message) {
        if (view != null && message != null) view.print(message);
    }

    /**
     * Устанавливает окружение сервера
     *
     * <p>Необходимо использовать рабочее окружение перед использованием сервера, иначе сервер может
     * работать некорректно либо вообще не работать. Если окружение равно null, оно будет отключено
     * и не будет использоваться сервером. По умолчанию окружение стоит null
     *
     * @param view окружение сервера
     */
    public static void setView(final IServerView view) {
        ServerController.view = view;
    }

    /**
     * Открывает сервер
     *
     * @throws ServerException если сервер уже открыт или возникла ошибка при открытии
     */
    public static void startServer() throws ServerException {
        server.startServer();
    }

    /**
     * Закрывает сервер
     *
     * @throws ServerException если сервер еще не запущен
     */
    public static void stopServer() throws ServerException {
        server.stopServer();
    }

    /** @return true, если сервер открыт, false иначе */
    public static boolean isOpen() {
        return server.isOpen();
    }

    /** @return максимально допустимое количество клиентов на сервере */
    public static int getMaxClients() {
        return server.getMaxClients();
    }

    /**
     * Устанавливает максимально допустимое количество клиентов на сервере
     *
     * @throws ServerException если сервер уже открыт
     */
    public static void setMaxClients(final int maxClients) throws ServerException {
        server.setMaxClients(maxClients);
    }

    /** @return порт, на котором запущен сервер */
    public static int getPort() {
        return server.getPort();
    }

    /**
     * Устанавливает порт, на котором будет запущен сервер
     *
     * @throws ServerException если сервер уже открыт
     */
    public static void setPort(final int port) throws ServerException {
        server.setPort(port);
    }

    /**
     * Выполняет команду сервера
     *
     * @throws ServerException если при выполнении команды возникла ошибка
     */
    public static void executeCommand(final String command) throws ServerException {
        server.executeCommand(command);
    }

    /**
     * Отправляет сообщение всем клиентам
     *
     * @throws ServerException если сервер закрыт
     */
    public static void sendAll(final String json) throws ServerException {
        server.sendAll(json);
    }

    /**
     * Отправляет сообщение клиенту
     *
     * @throws ServerException если сервер закрыт
     */
    public static void send(final String json, final int clientId) throws ServerException {
        server.send(json, clientId);
    }

    /**
     * Закрывает соединение с клиентом
     *
     * @throws ServerException если сервер закрыт
     */
    public static void closeConnection(final int clientId) throws ServerException {
        server.closeConnection(clientId);
    }
}
