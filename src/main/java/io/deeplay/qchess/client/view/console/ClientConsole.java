package io.deeplay.qchess.client.view.console;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.IClientView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ClientConsole implements IClientView {
    public static final String IP = "localhost";
    public static final int PORT = 8081;
    private final BufferedReader in =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public ClientConsole() {}

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void startView() {
        ClientController.setView(this);
        try {
            ClientController.connect(IP, PORT);
        } catch (ClientException e) {
            System.out.println("Ошибка при подключении клиента");
            e.printStackTrace();
        }
        System.out.printf(
                "Подключение к серверу %s:%d установлено%n",
                ClientController.getIp(), ClientController.getPort());

        while (ClientController.isConnected()) {
            if (update() != 0) break;
        }

        try {
            ClientController.disconnect();
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
                ClientController.executeCommand(command);
                return switch (command) {
                    case "stop" -> -1;
                    default -> 0;
                };
            }
        } catch (IOException | ClientException e) {
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
