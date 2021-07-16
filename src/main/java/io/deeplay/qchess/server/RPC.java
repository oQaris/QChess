package io.deeplay.qchess.server;

import java.util.List;

public class RPC {
    private static List<ClientHandler> clients;

    public static int getCountClients() {
        return clients.size();
    }

    public static void addClient(ClientHandler client) throws Exception {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public static void removeClient(ClientHandler client) throws Exception {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public static void setClients(List<ClientHandler> clients) {
        RPC.clients = clients;
    }

    public static void sendAllClientsAndServerMsg(String s) throws Exception {
        sendServerMsg(s);
        sendAllClientsMsg(s);
    }

    public static void sendAllClientsMsg(String s) throws Exception {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.getOut().println(s);
            }
        }
    }

    public static void sendServerMsg(String s) throws Exception {
        System.err.println(s);
    }
}
