package io.deeplay.qchess.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Server {
    private List<ClientHandler> clients;
    private ServerSocket server;

    private BufferedReader serverInput;
    private ClientHandlerManager clientHandlerManager;

    public Server() {}

    public void startServer() {
        if (tryOpenServer() != 0) {
            return;
        }

        try {
            clientHandlerManager = new ClientHandlerManager(server);
            clientHandlerManager.start();

            while (true) {
                int t = serverLoop();
                if (t != 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fatal server shutdown!");
        } finally {
            tryCloseServer();
        }
    }

    private int tryOpenServer() {
        try {
            clients = Collections.synchronizedList(new LinkedList<ClientHandler>());
            RPC.setClients(clients);

            System.out.println("Input server port:");
            serverInput = new BufferedReader(new InputStreamReader(System.in));
            final int PORT = Integer.parseInt(serverInput.readLine());

            server = new ServerSocket(PORT);

            System.out.println("Server is opened!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to open the server!");
            tryCloseServer();
            return -1;
        }
        return 0;
    }

    private int serverLoop() throws Exception {
        if (serverInput.ready()) {
            String str = serverInput.readLine();
            // TODO: send RPC to clients about closing the server
            RPC.sendAllClientsAndServerMsg("[server]: " + str);
            if (str.equals("stop")) {
                return -1;
            }
        }
        return 0;
    }

    private void tryCloseServer() {
        try {
            serverInput.close();

            synchronized (clients) {
                Iterator<ClientHandler> it = clients.iterator();
                while (it.hasNext()) {
                    it.next().tryCloseClient();
                }
            }
            clientHandlerManager.terminate();

            server.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error appeared while closing the server!");
        }
    }
}
