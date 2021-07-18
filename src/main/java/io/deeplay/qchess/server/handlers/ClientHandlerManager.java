package io.deeplay.qchess.server.handlers;

import io.deeplay.qchess.server.exceptions.ServerException;
import io.deeplay.qchess.server.service.ChatService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Подключает новых клиентов */
public class ClientHandlerManager extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerManager.class);
    private final List<ClientHandler> clients;
    private final ServerSocket server;
    private final Supplier<Integer> maxClients;
    private volatile boolean stop;
    private volatile boolean allClientsWasClosed;

    public ClientHandlerManager(ServerSocket server, Supplier<Integer> maxClients) {
        this.server = server;
        this.maxClients = maxClients;
        clients = Collections.synchronizedList(new ArrayList<>(maxClients.get()));
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
                            new ClientHandler(socket, this::removeClientFromClientList);
                    clients.add(client);
                    new Thread(client).start();
                }
            } catch (IOException | ServerException e) {
                logger.warn("Ошибка при подключении клиента: {}", e.getMessage());
            }
        }
        closeAllClients();
        logger.debug("Менеджер обработчиков клиентов остановил свою работу");
    }

    private void removeClientFromClientList(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
            if (clients.isEmpty()) allClientsWasClosed = true;
        }
    }

    private void closeAllClients() {
        logger.debug("Закрытие всех обработчиков клиентов");
        synchronized (clients) {
            for (ClientHandler clientHandler : clients) clientHandler.terminate();
        }
        while (!allClientsWasClosed) Thread.onSpinWait();
    }

    public void sendMessageAll(String message) {
        synchronized (clients) {
            for (ClientHandler clientHandler : clients)
                clientHandler.send(ChatService.convertMessageToServerToClientDTO(message));
        }
    }
}
