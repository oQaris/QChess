package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.ConsolePlayer;
import io.deeplay.qchess.game.player.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws ChessError, IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
        Player firstPlayer = new ConsolePlayer(roomSettings, Color.WHITE, in);
        Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
        Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
        game.run();

        in.close();
    }
}
