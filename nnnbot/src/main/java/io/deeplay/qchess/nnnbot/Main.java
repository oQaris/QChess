package io.deeplay.qchess.nnnbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.nnnbot.bot.NNNBotFactory;

public class Main {

    public static void main(String[] args) throws ChessError {
        GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);

        Player firstPlayer = NNNBotFactory.getNNNBot(roomSettings, Color.WHITE);
        Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);

        Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
        game.run();
    }
}
