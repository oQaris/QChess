package io.deeplay.qchess.client.view.console;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.model.ViewBoard;
import io.deeplay.qchess.client.view.model.ViewColor;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.client.view.model.ViewFigureType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ClientConsole implements IClientView {
    public static final String IP = "localhost";
    public static final int PORT = 8080;
    private final BufferedReader in =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private final ViewBoard board = new ViewBoard();

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
            ClientController.disconnect("Клиент отключен");
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
                if (command != null) ClientController.executeCommand(command);
                return "stop".equals(command) ? -1 : 0;
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

    @Override
    public void drawBoard() {
        for (int row = 0; row < 8; ++row) {
            for (int column = 0; column < 8; ++column) {
                ViewFigure f = board.getFigure(column, row);
                if (f == null) System.out.print("_");
                else System.out.print(figureToIcon(ViewColor.valueOf(f.getColor()), f.getType()));
            }
            System.out.println();
        }
    }

    private char figureToIcon(ViewColor color, ViewFigureType figure) {
        // дублируется код из борды
        return switch (color) {
            case WHITE -> switch (figure) {
                case BISHOP -> '♝';
                case KING -> '♚';
                case KNIGHT -> '♞';
                case PAWN -> '♟';
                case QUEEN -> '♛';
                case ROOK -> '♜';
            };
            case BLACK -> switch (figure) {
                case BISHOP -> '♗';
                case KING -> '♔';
                case KNIGHT -> '♘';
                case PAWN -> '♙';
                case QUEEN -> '♕';
                case ROOK -> '♖';
            };
        };
    }

    @Override
    public ViewBoard getBoard() {
        return board;
    }

    @Override
    public void closeGame(String reason) {
        throw new UnsupportedOperationException("Еще не реализовано");
    }

    @Override
    public void disconnect(String reason) {
        throw new UnsupportedOperationException("Еще не реализовано");
    }
}
