package io.deeplay.qchess.client.view;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ClientConsole implements IClientView {
    private final BufferedReader in =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private final String ip = "localhost";
    private final int port = 8081;
    private ClientController client;

    public ClientConsole() {}

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void startView() {
        client = new ClientController(this);
        try {
            client.connect(ip, port);
        } catch (ClientException e) {
            System.out.println("Ошибка при подключении клиента");
            e.printStackTrace();
        }
        System.out.printf(
                "Подключение к серверу %s:%d установлено%n", client.getIp(), client.getPort());

        while (client.isConnected()) {
            if (update() != 0) break;
        }

        try {
            client.disconnect();
        } catch (ClientException e) {
            System.out.println("Ошибка при отключении от сервера");
            e.printStackTrace();
        }
        System.out.println("Клиент отключился от сервера");
    }

    private int update() {
        try {
            if (in.ready()) {
                String command = in.readLine();
                client.sendMessageAll(command);
                if (command.equals("stop")) {
                    return -1;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при вводе команды");
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void print(String message) {
        System.out.println(message);
    }
}
