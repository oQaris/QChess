package io.deeplay.qchess.server.view;

import io.deeplay.qchess.server.controller.ServerController;
import io.deeplay.qchess.server.exceptions.ServerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ServerConsole implements IServerView {
    private final BufferedReader in =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public ServerConsole() {}

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void startView() {
        ServerController.setView(this);
        try {
            ServerController.startServer();
            System.out.println("Сервер запущен");
        } catch (ServerException e) {
            System.out.println("Ошибка при запуске сервера");
            e.printStackTrace();
        }

        while (ServerController.isOpen()) {
            if (update() != 0) {
                System.out.println("Сервер закрыт");
                break;
            }
        }
    }

    private int update() {
        try {
            if (in.ready()) {
                String command = in.readLine();
                ServerController.executeCommand(command);
                if (command.equals("stop")) {
                    return -1;
                }
            }
        } catch (IOException | ServerException e) {
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
