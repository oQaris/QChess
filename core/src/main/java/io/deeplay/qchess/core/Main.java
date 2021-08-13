package io.deeplay.qchess.core;

import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.gui.ClientGUI;
import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(final String[] args) throws IOException {
        System.out.println(
                "Введите \"s\", чтобы запустить сервер или \"c\", чтобы запустить клиент");
        System.out.println("Ещё можно ввести \"a\", чтобы лицезреть бесконечную мощь Qbot'a");

        String input;
        if (args.length > 0) input = args[0];
        else input = new Scanner(System.in).nextLine().strip();

        switch (input) {
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
                // Aрена
            case "a", "-a", "arena" -> {
                Arena arena = new Arena();
                try {
                    arena.battle();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            default -> System.out.println("Некорректная команда");
        }
    }
}
