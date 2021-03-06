package io.deeplay.qchess.server;

import static io.deeplay.qchess.server.exceptions.ServerErrorCode.ERROR_PORT;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.ERROR_WHILE_SERVER_OPENING;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.SERVER_IS_ALREADY_OPEN;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.SERVER_IS_NOT_OPENED;

import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.handlers.ClientHandlerManager;
import io.deeplay.qchess.server.service.ServerCommandService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalHost implements IServer {
    private static final Logger logger = LoggerFactory.getLogger(LocalHost.class);
    private static int maxClients = 20;
    private static int port = 8080;
    private static LocalHost localHost;
    private final Object mutex = new Object();
    private ServerSocket server;
    private ClientHandlerManager clientHandlerManager;
    private volatile boolean isOpen;

    private LocalHost() {}

    /** @return возвращает экземпляр сервера */
    public static LocalHost getInstance() {
        if (localHost == null) localHost = new LocalHost();
        return localHost;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(final int port) throws ServerException {
        synchronized (mutex) {
            checkIsOpen();
            LocalHost.port = port;
        }
    }

    @Override
    public void executeCommand(final String command) throws ServerException {
        ServerCommandService.handleCommand(command);
    }

    @Override
    public void sendAll(final String json) throws ServerException {
        synchronized (mutex) {
            checkIsNotOpen();
            clientHandlerManager.sendAll(json);
        }
    }

    @Override
    public void send(final String json, final int clientId) throws ServerException {
        synchronized (mutex) {
            checkIsNotOpen();
            clientHandlerManager.send(json, clientId);
        }
    }

    @Override
    public void closeConnection(final int clientId) throws ServerException {
        synchronized (mutex) {
            checkIsNotOpen();
            clientHandlerManager.closeConnection(clientId);
        }
    }

    /** @throws ServerException если сервер закрыт */
    private void checkIsNotOpen() throws ServerException {
        synchronized (mutex) {
            if (!isOpen) {
                logger.warn("Сервер еще не запущен");
                throw new ServerException(SERVER_IS_NOT_OPENED);
            }
        }
    }

    /** @throws ServerException если сервер открыт */
    private void checkIsOpen() throws ServerException {
        synchronized (mutex) {
            if (isOpen) {
                logger.warn("Сервер уже открыт");
                throw new ServerException(SERVER_IS_ALREADY_OPEN);
            }
        }
    }

    @Override
    public int getMaxClients() {
        return maxClients;
    }

    @Override
    public void setMaxClients(final int maxClients) throws ServerException {
        synchronized (mutex) {
            checkIsOpen();
            LocalHost.maxClients = maxClients;
        }
    }

    @Override
    public void startServer() throws ServerException {
        logger.debug("Запуск сервера...");
        synchronized (mutex) {
            checkIsOpen();
            isOpen = true;
            try {
                server = new ServerSocket(port);
                logger.info("Сервер открыт!");
            } catch (final IllegalArgumentException e) {
                logger.error("Некорректный порт");
                stopServer();
                throw new ServerException(ERROR_PORT, e);
            } catch (final IOException e) {
                logger.error("Ошибка при открытии сервера: {}", e.getMessage());
                stopServer();
                throw new ServerException(ERROR_WHILE_SERVER_OPENING, e);
            }
            clientHandlerManager = new ClientHandlerManager(server);
            clientHandlerManager.start();
        }
    }

    @Override
    public void stopServer() throws ServerException {
        logger.debug("Остановка сервера...");
        synchronized (mutex) {
            checkIsNotOpen();
            logger.info("Закрытие сервера...");
            closeClientHandlerManager();
            closeServerSocket();
            isOpen = false;
        }
        logger.info("Сервер закрыт.");
    }

    private void closeClientHandlerManager() {
        logger.debug("Закрытие менеджера обработчиков...");
        if (clientHandlerManager == null) return;
        clientHandlerManager.terminate();
        try {
            // TODO: will remove with NIO
            new Socket("localhost", port);
            clientHandlerManager.join();
        } catch (final IOException | InterruptedException e) {
            logger.error("Обработчик клиентов убил сервер: {}", e.getMessage());
        }
    }

    private void closeServerSocket() {
        logger.debug("Закрытие серверного сокета...");
        if (server == null) return;
        try {
            server.close();
        } catch (final IOException e) {
            logger.error("Возникла ошибка при закрытии серверного сокета: {}", e.getMessage());
        }
    }

    @Override
    public boolean isOpen() {
        synchronized (mutex) {
            return isOpen;
        }
    }
}
