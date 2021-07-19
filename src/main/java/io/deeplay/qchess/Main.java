package io.deeplay.qchess;

import io.deeplay.qchess.client.view.console.ClientConsole;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.server.view.IServerView;
import io.deeplay.qchess.server.view.ServerConsole;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ChessError, IOException {
        // TODO: при создании комнаты
        // BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //
        // GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
        // Player firstPlayer = new ConsolePlayer(roomSettings, Color.WHITE, in);
        // Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
        // Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
        // game.run();

        switch (new Scanner(System.in).nextLine()) {
            case "s" -> // server
            {
                IServerView view = new ServerConsole();
                view.startView();
                view.close();
            }
            case "c" -> // client
            {
                IClientView view = new ClientConsole();
                view.startView();
                view.close();
            }
        }
    }
}
