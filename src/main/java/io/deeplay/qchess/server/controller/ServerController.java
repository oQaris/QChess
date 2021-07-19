package io.deeplay.qchess.server.controller;

import io.deeplay.qchess.server.IServer;
import io.deeplay.qchess.server.LocalHost;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.view.IServerView;
import java.util.Optional;

public class ServerController {
    private static final IServer server = LocalHost.getInstance();
    private static IServerView view;

    /** @return окружение сервера */
    public static Optional<IServerView> getView() {
        return Optional.of(view);
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
    public static void setView(IServerView view) {
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
    public static void setMaxClients(int maxClients) throws ServerException {
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
    public static void setPort(int port) throws ServerException {
        server.setPort(port);
    }

    /**
     * Выполняет команду сервера
     *
     * @throws ServerException если при выполнении команды возникла ошибка
     */
    public static void executeCommand(String command) throws ServerException {
        server.executeCommand(command);
    }

    /**
     * Отправляет сообщение всем клиентам
     *
     * @throws ServerException если сервер закрыт
     */
    public static void sendAll(String json) throws ServerException {
        server.sendAll(json);
    }

    /**
     * Отправляет сообщение клиенту
     *
     * @throws ServerException если сервер закрыт
     */
    public static void send(String json, int clientID) throws ServerException {
        server.send(json, clientID);
    }
}
