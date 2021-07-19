package io.deeplay.qchess.client;

import static io.deeplay.qchess.client.exceptions.ClientErrorCode.CLIENT_IS_ALREADY_CONNECTED;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.CLIENT_IS_NOT_CONNECTED;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.FAILED_CONNECT;

import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.handlers.InputTrafficHandler;
import io.deeplay.qchess.client.service.special.ClientCommandService;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements IClient {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static Client client;
    private static String ip;
    private static int port;
    private final Object mutex = new Object();
    private InputTrafficHandler inputTrafficHandler;
    private volatile boolean isConnected;

    private Client() {}

    /** @return возвращает экземпляр клиента */
    public static Client getInstance() {
        client = client != null ? client : new Client();
        return client;
    }

    @Override
    public void connect(String ip, int port) throws ClientException {
        logger.debug("Подключение клиента {} к серверу {}:{}", this, ip, port);
        synchronized (mutex) {
            if (isConnected) {
                logger.warn("Клиент {} уже подключен к серверу {}:{}", this, ip, port);
                throw new ClientException(CLIENT_IS_ALREADY_CONNECTED);
            }
            Socket socket;
            try {
                Client.ip = ip;
                Client.port = port;
                socket = new Socket(ip, port);
                logger.info("Клиент {} успешно подключился к серверу {}:{}", this, ip, port);
            } catch (IOException e) {
                logger.warn("Ошибка получения сокета для подключения клиента {}", this);
                disconnect();
                throw new ClientException(FAILED_CONNECT, e);
            }
            try {
                inputTrafficHandler = new InputTrafficHandler(socket);
                logger.debug("Обработчик входящего трафика для клиента {} успешно создан", this);
                inputTrafficHandler.start();
                isConnected = true;
            } catch (ClientException e) {
                disconnect();
                throw e;
            }
        }
    }

    @Override
    public void disconnect() throws ClientException {
        logger.debug("Отключение клиента {} от сервера {}:{}", this, ip, port);
        synchronized (mutex) {
            if (!isConnected) {
                logger.warn("Клиент {} еще не подключен", this);
                throw new ClientException(CLIENT_IS_NOT_CONNECTED);
            }
            logger.info("Отключение клиента {} от сервера...", this);
            closeInputTrafficHandler();
            isConnected = false;
        }
        logger.info("Клиент {} отключен от сервера {}:{}", this, ip, port);
    }

    private void closeInputTrafficHandler() {
        if (inputTrafficHandler == null) return;
        inputTrafficHandler.terminate();
        try {
            inputTrafficHandler.join();
        } catch (InterruptedException e) {
            logger.error("Обработчик трафика убил клиента: {}", e.getMessage());
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
            if (isConnected) {
                logger.warn("Клиент {} уже подключен к серверу {}:{}", this, ip, port);
                throw new ClientException(CLIENT_IS_ALREADY_CONNECTED);
            }
        }
        Client.port = port;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) throws ClientException {
        synchronized (mutex) {
            if (isConnected) {
                logger.warn("Клиент {} уже подключен к серверу {}:{}", this, ip, port);
                throw new ClientException(CLIENT_IS_ALREADY_CONNECTED);
            }
        }
        Client.ip = ip;
    }

    @Override
    public void executeCommand(String command) throws ClientException {
        ClientCommandService.handleCommand(command);
    }

    @Override
    public void send(String json) throws ClientException {
        synchronized (mutex) {
            if (!isConnected) {
                logger.warn("Клиент {} еще не подключен", this);
                throw new ClientException(CLIENT_IS_NOT_CONNECTED);
            }
            inputTrafficHandler.send(json);
        }
    }
}
