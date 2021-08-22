package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.ConsolePlayer;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(final String[] args) throws ChessError {
        final GameSettings gameSettings = new GameSettings(BoardFilling.STANDARD);
        final ConsolePlayer bot1 =
                new ConsolePlayer(
                        gameSettings,
                        Color.WHITE,
                        new BufferedReader(new InputStreamReader(System.in)));
        final QBot bot2 = new QMinimaxBot(gameSettings, Color.BLACK, 4);

        final Selfplay game = new Selfplay(gameSettings, bot1, bot2);
        game.run();
    }
}
