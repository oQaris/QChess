package io.deeplay.qchess.server.handlers;

import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.service.ConnectionControlService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Подключает новых клиентов */
public class ClientHandlerManager extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerManager.class);
    private static final Map<Integer, ClientHandler> clients =
            Collections.synchronizedMap(new HashMap<>(ServerController.getMaxClients()));

    private static final Object mutexLastID = new Object();
    private static int lastID;

    private final ServerSocket server;

    private volatile boolean stop;
    private volatile boolean allClientsWasClosed;

    public ClientHandlerManager(ServerSocket server) {
        this.server = server;
    }

    public void terminate() {
        stop = true;
    }

    @Override
    public void run() {
        logger.debug("Менеджер обработчиков клиентов запущен");
        while (!stop) {
            try {
                // TODO: replace to non-blocking NIO
                Socket socket = server.accept();

                if (socket != null) {
                    int id;
                    synchronized (mutexLastID) {
                        id = lastID++;
                    }

                    ClientHandler client =
                            new ClientHandler(socket, this::removeClientFromClientList, id);

                    if (clients.size() == ServerController.getMaxClients()) {
                        client.sendIfNotNull(ConnectionControlService.getJsonToDisconnect());
                        client.terminate();
                    } else {
                        // синхронизация нужна, чтобы нельзя было отправить клиенту запрос, пока
                        // поток не будет создан
                        synchronized (clients) {
                            clients.put(id, client);
                            new Thread(client).start();
                        }
                    }
                }
            } catch (IOException | ServerException e) {
                logger.warn("Ошибка при подключении клиента: {}", e.getMessage());
            }
        }
        closeAllClients();
        logger.debug("Менеджер обработчиков клиентов остановил свою работу");
    }

    private void removeClientFromClientList(int id) {
        synchronized (clients) {
            clients.remove(id);
            if (clients.isEmpty()) allClientsWasClosed = true;
        }
    }

    private void closeAllClients() {
        logger.debug("Закрытие всех обработчиков клиентов");
        synchronized (clients) {
            if (clients.isEmpty()) allClientsWasClosed = true;
            else for (ClientHandler clientHandler : clients.values()) clientHandler.terminate();
        }
        while (!allClientsWasClosed) Thread.onSpinWait();
    }

    /** Отправляет всем подключенным клиентам строку */
    public void sendAll(String json) {
        synchronized (clients) {
            for (ClientHandler clientHandler : clients.values()) clientHandler.sendIfNotNull(json);
        }
    }

    /** Отправляет клиенту строку, если он подключен */
    public void send(String json, int toClientID) {
        clients.get(toClientID).sendIfNotNull(json);
    }

    /** Закрывает соединение с клиентом */
    public void closeConnection(int clientID) {
        clients.get(clientID).terminate();
    }
}
