package io.deeplay.qchess.client;

import static io.deeplay.qchess.client.exceptions.ClientErrorCode.CLIENT_IS_ALREADY_CONNECTED;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.CLIENT_IS_NOT_CONNECTED;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.CONNECTION_WAS_BROKEN;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.FAILED_CONNECT;

import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.handlers.InputTrafficHandler;
import io.deeplay.qchess.client.handlers.TrafficRequestHandler;
import io.deeplay.qchess.client.service.ClientCommandService;
import io.deeplay.qchess.clientserverconversation.dto.GetRequestType;
import io.deeplay.qchess.clientserverconversation.dto.MainRequestType;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.dto.other.GetRequestDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalClient implements IClient {
    private static final Logger logger = LoggerFactory.getLogger(LocalClient.class);
    private static LocalClient localClient;
    private static String ip;
    private static int port;
    private static volatile boolean waitForResponse;
    private static volatile ServerToClientDTO lastResponse;
    private final Object mutex = new Object();
    private final Object mutexWaitForResponse = new Object();
    private final Object mutexLastResponse = new Object();
    private InputTrafficHandler inputTrafficHandler;
    private volatile boolean isConnected;

    private LocalClient() {}

    /** @return возвращает экземпляр клиента */
    public static LocalClient getInstance() {
        localClient = localClient != null ? localClient : new LocalClient();
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
            logger.debug("Подключение клиента {} к серверу {}:{}", this, ip, port);
            checkIsConnected();
            Socket socket;
            try {
                LocalClient.ip = ip;
                LocalClient.port = port;
                socket = new Socket(ip, port);
                logger.info("Клиент {} успешно подключился к серверу {}:{}", this, ip, port);
            } catch (IOException e) {
                logger.warn("Ошибка получения сокета для подключения клиента {}", this);
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
        }
    }

    @Override
    public void disconnect() throws ClientException {
        synchronized (mutex) {
            logger.debug("Отключение клиента от сервера {}:{}", ip, port);
            checkIsNotConnected();
            logger.info("Отключение клиента от сервера...");
            inputTrafficHandler.sendIfNotNull(
                    TrafficRequestHandler.convertToClientToServerDTO(
                            MainRequestType.DISCONNECT, null));
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
            logger.error("Обработчик трафика убил клиента: {}", e.getMessage());
        }
    }

    /** @throws ClientException если клиент не подключен */
    private void checkIsNotConnected() throws ClientException {
        if (!isConnected) {
            logger.warn("Клиент еще не подключен");
            throw new ClientException(CLIENT_IS_NOT_CONNECTED);
        }
    }

    /** @throws ClientException если клиент подключен */
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
    public GetRequestDTO waitForResponse(GetRequestType getRequestType) throws ClientException {
        logger.debug("Начало ожидания запроса {}...", getRequestType);
        checkIsNotConnected();

        synchronized (mutexWaitForResponse) {
            synchronized (mutexLastResponse) {
                lastResponse = null;
                waitForResponse = true;
            }
            inputTrafficHandler.sendIfNotNull(
                    TrafficRequestHandler.convertToClientToServerDTO(
                            MainRequestType.GET,
                            SerializationService.serialize(
                                    new GetRequestDTO(getRequestType, null))));
            while (true) {
                while (waitForResponse) {
                    if (!isConnected) {
                        logger.warn("Соединение было разорвано");
                        throw new ClientException(CONNECTION_WAS_BROKEN);
                    }
                    Thread.onSpinWait();
                }

                synchronized (mutexLastResponse) {
                    GetRequestDTO getDTO = null;
                    try {
                        // TODO: ХЕРНЯ НЕ РАБОТАЕТ ХЗ ПОЧЕМУ МЬЮТЕКС ТУПОЙ ЗАСТЫВАЕТ ПРОГРА ЮЛЯТЬ
                        if (lastResponse.mainRequestType == MainRequestType.DISCONNECT) {
                            logger.warn("Соединение было разорвано");
                            throw new ClientException(CONNECTION_WAS_BROKEN);
                        }
                        getDTO =
                                SerializationService.deserialize(
                                        lastResponse.request, GetRequestDTO.class);
                    } catch (IOException | NullPointerException e) {
                        // Ожидается другой ответ
                    }
                    if (lastResponse.mainRequestType
                                    == MainRequestType.GET // TODO: заменить на POST
                            && getDTO != null
                            && getDTO.requestType == getRequestType) return getDTO;
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
}
