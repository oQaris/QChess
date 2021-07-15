package io.deeplay.qchess.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private BufferedReader clientInput;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private InputTrafficHandler inputTrafficHandler;

    public Client() {}

    public void startClient() {
        if (tryConnect() != 0) {
            return;
        }
        try {
            inputTrafficHandler = new InputTrafficHandler(in);
            inputTrafficHandler.start();

            while (!inputTrafficHandler.isFail()) {
                int i = clientLoop();
                if (i != 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fatal client shutdown!");
        } finally {
            tryCloseAll();
        }
    }

    private int tryConnect() {
        try {
            clientInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Input server ip:");
            final String IP = clientInput.readLine();
            System.out.println("Input server port:");
            final int PORT = Integer.parseInt(clientInput.readLine());

            System.out.println("Input your name:");
            String name = clientInput.readLine();

            socket = new Socket(IP, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(name);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the server.");
            tryCloseAll();
            return -1;
        }
        return 0;
    }

    private int clientLoop() throws Exception {
        if (clientInput.ready()) {
            String str = clientInput.readLine();
            out.println(str);

            if (str.equals("exit")) {
                return -1;
            }
        }
        return 0;
    }

    private void tryCloseAll() {
        try {
            inputTrafficHandler.terminate();
            clientInput.close();
            out.close();
            in.close();
            socket.close();
            clientInput.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error appeared while closing the client!");
        }
    }
}
