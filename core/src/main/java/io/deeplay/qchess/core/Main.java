package io.deeplay.qchess.core;

import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.ClientGUI;
import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(
                "Введите \"s\", чтобы запустить сервер или \"c\", чтобы запустить клиент");

        switch (new Scanner(System.in).nextLine()) {
            // Сервер
            case "s", "-s", "server" -> {
                IServerView view = new ServerConsole();
                view.startView();
                view.close();
            }
            // Клиент
            case "c", "-c", "client" -> {
                IClientView view = new ClientGUI();
                view.startView();
                view.close();
            }
            default -> System.out.println("Некорректная команда");
        }
    }
}
