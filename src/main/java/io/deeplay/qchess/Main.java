package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.Bot;
import io.deeplay.qchess.game.player.ConsolePlayer;
import io.deeplay.qchess.game.player.Player;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws ChessError {
        // TODO: при создании комнаты
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
        Player firstPlayer = new ConsolePlayer(roomSettings, Color.WHITE, in);
        Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
        Game game = new Game(roomSettings, firstPlayer, secondPlayer);
        game.run();
    }
}
