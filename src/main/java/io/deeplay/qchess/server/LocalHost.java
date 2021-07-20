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
    private static int maxClients = 2;
    private static int port = 8080;
    private static LocalHost localHost;
    private final Object mutex = new Object();
    private ServerSocket server;
    private ClientHandlerManager clientHandlerManager;
    private volatile boolean isOpen;

    private LocalHost() {}

    /** @return возвращает экземпляр сервера */
    public static LocalHost getInstance() {
        localHost = localHost != null ? localHost : new LocalHost();
        return localHost;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) throws ServerException {
        synchronized (mutex) {
            if (isOpen) {
                logger.warn("Сервер уже открыт");
                throw new ServerException(SERVER_IS_ALREADY_OPEN);
            }
            LocalHost.port = port;
        }
    }

    @Override
    public void executeCommand(String command) throws ServerException {
        ServerCommandService.handleCommand(command);
    }

    @Override
    public void sendAll(String json) throws ServerException {
        synchronized (mutex) {
            if (!isOpen) {
                logger.warn("Сервер еще не запущен");
                throw new ServerException(SERVER_IS_NOT_OPENED);
            }
            clientHandlerManager.sendAll(json);
        }
    }

    @Override
    public void send(String json, int clientID) throws ServerException {
        synchronized (mutex) {
            if (!isOpen) {
                logger.warn("Сервер еще не запущен");
                throw new ServerException(SERVER_IS_NOT_OPENED);
            }
            clientHandlerManager.send(json, clientID);
        }
    }

    @Override
    public int getMaxClients() {
        return maxClients;
    }

    @Override
    public void setMaxClients(int maxClients) throws ServerException {
        synchronized (mutex) {
            if (isOpen) {
                logger.warn("Сервер уже открыт");
                throw new ServerException(SERVER_IS_ALREADY_OPEN);
            }
            LocalHost.maxClients = maxClients;
        }
    }

    @Override
    public void startServer() throws ServerException {
        logger.debug("Запуск сервера...");
        synchronized (mutex) {
            if (isOpen) {
                logger.warn("Сервер уже открыт");
                throw new ServerException(SERVER_IS_ALREADY_OPEN);
            }
            isOpen = true;
            try {
                server = new ServerSocket(port);
                logger.info("Сервер открыт!");
            } catch (IllegalArgumentException e) {
                logger.error("Некорректный порт");
                stopServer();
                throw new ServerException(ERROR_PORT, e);
            } catch (IOException e) {
                logger.error("Ошибка при открытии сервера: {}", e.getMessage());
                stopServer();
                throw new ServerException(ERROR_WHILE_SERVER_OPENING, e);
            }
            clientHandlerManager = new ClientHandlerManager(server, this::getMaxClients);
            clientHandlerManager.start();
        }
    }

    @Override
    public void stopServer() throws ServerException {
        logger.debug("Остановка сервера...");
        synchronized (mutex) {
            if (!isOpen) {
                logger.warn("Сервер еще не запущен");
                throw new ServerException(SERVER_IS_NOT_OPENED);
            }
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
        } catch (IOException | InterruptedException e) {
            logger.error("Обработчик клиентов убил сервер: {}", e.getMessage());
        }
    }

    private void closeServerSocket() {
        logger.debug("Закрытие серверного сокета...");
        if (server == null) return;
        try {
            server.close();
        } catch (IOException e) {
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
