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
    public static void main(final String[] args) throws ChessError, IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
        final Player firstPlayer = new ConsolePlayer(roomSettings, Color.WHITE, in);
        final Player secondPlayer = new AttackBot(roomSettings, Color.BLACK);
        final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
        game.run();

        in.close();
    }
}
