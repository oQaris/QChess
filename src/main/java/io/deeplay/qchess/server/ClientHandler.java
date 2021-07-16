package io.deeplay.qchess.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Устанавливает связь с клиентом */
public class ClientHandler extends Thread {
    private boolean stop;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error to create client handler!");
            tryCloseClient();
        }
    }

    public void terminate() {
        stop = true;
    }

    @Override
    public void run() {
        try {
            name = in.readLine();

            RPC.sendAllClientsAndServerMsg(name + " joined the server!");

            while (!stop) {
                int i = clientHandlerLoop();
                if (i != 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(name + " broke the connection.");
        } finally {
            tryCloseClient();
            try {
                RPC.sendAllClientsAndServerMsg(
                        name + " left the server. Now " + RPC.getCountClients() + " clients.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("RPC send message error!");
            }
        }
    }

    private int clientHandlerLoop() throws Exception {
        if (in.ready()) {
            String str = in.readLine();
            if (str.equals("exit")) {
                return -1;
            }
            RPC.sendAllClientsAndServerMsg(name + "> " + str);
        }

        return 0;
    }

    public void tryCloseClient() {
        try {
            stop = true;

            out.close();
            in.close();
            socket.close();

            RPC.removeClient(this);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error to close client handler!");
        }
    }

    public PrintWriter getOut() {
        return out;
    }
}
