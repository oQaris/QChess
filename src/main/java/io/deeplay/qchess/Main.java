package io.deeplay.qchess;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.ConsolePlayer;
import io.deeplay.qchess.game.player.MinimaxBot;
import io.deeplay.qchess.game.player.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws ChessError, IOException {
        // TODO: при создании комнаты
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
        Player firstPlayer = new ConsolePlayer(roomSettings, Color.WHITE, in);
        Player secondPlayer = new MinimaxBot(roomSettings, Color.BLACK, 3);
        Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
        game.run();

        /*switch (new Scanner(System.in).nextLine()) {
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
        }*/
    }
}
