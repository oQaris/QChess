package io.deeplay.qchess.server.handlers;

import io.deeplay.qchess.server.exceptions.ServerException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Подключает новых клиентов */
public class ClientHandlerManager extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerManager.class);
    private final Map<Integer, ClientHandler> clients;
    private final ServerSocket server;
    private final Supplier<Integer> maxClients;
    private int lastID;
    private volatile boolean stop;
    private volatile boolean allClientsWasClosed;

    public ClientHandlerManager(ServerSocket server, Supplier<Integer> maxClients) {
        this.server = server;
        this.maxClients = maxClients;
        clients = Collections.synchronizedMap(new HashMap<>(maxClients.get()));
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
                if (clients.size() == maxClients.get()) {
                    socket.close();
                    continue;
                }

                if (socket != null) {
                    ClientHandler client =
                            new ClientHandler(socket, this::removeClientFromClientList, lastID);
                    clients.put(lastID++, client);
                    new Thread(client).start();
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
            for (ClientHandler clientHandler : clients.values()) clientHandler.terminate();
        }
        while (!allClientsWasClosed) Thread.onSpinWait();
    }

    /** Отправляет всем подключенным клиентам строку */
    public void sendAll(String json) {
        synchronized (clients) {
            for (ClientHandler clientHandler : clients.values()) clientHandler.send(json);
        }
    }

    /** Отправляет клиенту строку, если он подключен */
    public void send(String json, int toClientID) {
        clients.get(toClientID).send(json);
    }
}
