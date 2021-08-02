package io.deeplay.qchess.client;

import io.deeplay.qchess.client.dao.SessionDAO;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.handlers.InputTrafficHandler;
import io.deeplay.qchess.client.service.ClientCommandService;
import io.deeplay.qchess.clientserverconversation.dto.clienttoserver.ConnectionDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.IClientToServerDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.IServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientType;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

import static io.deeplay.qchess.client.exceptions.ClientErrorCode.*;

public class LocalClient implements IClient {
    private static final Logger logger = LoggerFactory.getLogger(LocalClient.class);
    private static LocalClient localClient;
    private static String ip;
    private static int port;
    private static volatile boolean waitForResponse;

    /**
     * volatile используется, т.к. нужно следить за изменением ссылки, а не объекта
     */
    private static volatile ServerToClientDTO lastResponse;

    private final Object mutex = new Object();
    private final Object mutexWaitForResponse = new Object();
    private final Object mutexLastResponse = new Object();
    private InputTrafficHandler inputTrafficHandler;
    private volatile boolean isConnected;
    private volatile boolean killClient;

    private LocalClient() {
    }

    /**
     * @return возвращает экземпляр клиента
     */
    public static LocalClient getInstance() {
        if (localClient == null) localClient = new LocalClient();
        return localClient;
    }

    private void setLastResponse(final ServerToClientDTO lastResponse) {
        synchronized (mutexLastResponse) {
            LocalClient.lastResponse = lastResponse;
            waitForResponse = false;
        }
    }

    @Override
    public void connect(String ip, int port) throws ClientException {
        synchronized (mutex) {
            logger.debug("Подключение клиента к серверу {}:{}", ip, port);
            checkIsConnected();
            Socket socket;
            try {
                LocalClient.ip = ip;
                LocalClient.port = port;
                socket = new Socket(ip, port);
                logger.info("Клиент успешно подключился к серверу {}:{}", ip, port);
            } catch (IOException e) {
                logger.warn("Ошибка получения сокета для подключения клиента: {}", e.getMessage());
                disconnect();
                throw new ClientException(FAILED_CONNECT, e);
            }
            try {
                inputTrafficHandler =
                        new InputTrafficHandler(
                                socket, this::setLastResponse, () -> waitForResponse);
                logger.debug("Обработчик входящего трафика для клиента успешно создан");
                inputTrafficHandler.start();
                isConnected = true;
            } catch (ClientException e) {
                disconnect();
                throw e;
            }
            new ClientKiller().start();
        }
    }

    @Override
    public void disconnect() throws ClientException {
        logger.debug("Отключение клиента от сервера {}:{}", ip, port);
        checkIsNotConnected();
        killClient = true;
    }

    /**
     * @throws ClientException если клиент не подключен
     */
    private void checkIsNotConnected() throws ClientException {
        if (!isConnected) {
            logger.warn("Клиент еще не подключен");
            throw new ClientException(CLIENT_IS_NOT_CONNECTED);
        }
    }

    /**
     * @throws ClientException если клиент подключен
     */
    private void checkIsConnected() throws ClientException {
        if (isConnected) {
            logger.warn("Клиент уже подключен к серверу {}:{}", ip, port);
            throw new ClientException(CLIENT_IS_ALREADY_CONNECTED);
        }
    }

    @Override
    public boolean isConnected() {
        synchronized (mutex) {
            return isConnected;
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) throws ClientException {
        synchronized (mutex) {
            checkIsConnected();
            LocalClient.port = port;
        }
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) throws ClientException {
        synchronized (mutex) {
            checkIsConnected();
            LocalClient.ip = ip;
        }
    }

    @Override
    public <T extends IServerToClientDTO> T waitForResponse(
            IClientToServerDTO dto, Class<T> forDTOClass) throws ClientException {
        logger.debug("Начало ожидания запроса {}...", dto);
        checkIsNotConnected();

        synchronized (mutexWaitForResponse) {
            synchronized (mutexLastResponse) {
                lastResponse = null;
                waitForResponse = true;
            }
            while (true) {
                inputTrafficHandler.sendIfNotNull(
                        SerializationService.makeMainDTOJsonToServer(dto));

                while (waitForResponse) {
                    if (!isConnected) {
                        logger.warn("Соединение было разорвано");
                        throw new ClientException(CONNECTION_WAS_BROKEN);
                    }
                    Thread.onSpinWait();
                }
                if (!isConnected) {
                    logger.warn("Соединение было разорвано");
                    throw new ClientException(CONNECTION_WAS_BROKEN);
                }

                synchronized (mutexLastResponse) {
                    try {
                        if (lastResponse.type == ServerToClientType.valueOf(forDTOClass))
                            return SerializationService.serverToClientDTORequest(
                                    lastResponse.json, forDTOClass);
                    } catch (SerializationException | NullPointerException e) {
                        // Ожидается другой ответ
                    }
                }
            }
        }
    }

    @Override
    public void executeCommand(String command) throws ClientException {
        ClientCommandService.handleCommand(command);
    }

    @Override
    public void sendIfNotNull(String json) throws ClientException {
        synchronized (mutex) {
            checkIsNotConnected();
            inputTrafficHandler.sendIfNotNull(json);
        }
    }

    private class ClientKiller extends Thread {

        @Override
        public void run() {
            while (!killClient) onSpinWait();
            synchronized (mutex) {
                logger.info("Отключение клиента от сервера...");
                inputTrafficHandler.sendIfNotNull(
                        SerializationService.makeMainDTOJsonToServer(
                                new ConnectionDTO(SessionDAO.getSessionToken(), false)));
                closeInputTrafficHandler();
                isConnected = false;
                logger.info("Клиент отключен от сервера {}:{}", ip, port);
            }
        }

        private void closeInputTrafficHandler() {
            if (inputTrafficHandler == null) return;
            inputTrafficHandler.terminate();
            try {
                inputTrafficHandler.join();
            } catch (InterruptedException e) {
                logger.error(
                        "Обработчик трафика убил убийцу обработчика трафика: {}", e.getMessage());
            }
        }
    }
}
