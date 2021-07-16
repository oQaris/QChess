package io.deeplay.qchess.server;

import java.net.ServerSocket;
import java.net.Socket;

/** Подключает новых клиентов */
public class ClientHandlerManager extends Thread {
    private ServerSocket server;
    private boolean stop;

    public ClientHandlerManager(ServerSocket server) {
        this.server = server;
    }

    public void terminate() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                // TODO: make non-blocking code
                Socket socket = server.accept();

                if (socket != null) {
                    ClientHandler client = new ClientHandler(socket);
                    RPC.addClient(client);
                    client.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Client was not processed!");
            }
        }
    }
}
