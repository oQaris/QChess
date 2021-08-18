package io.deeplay.qchess;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.lobot.LoBot;
import io.deeplay.qchess.lobot.Strategy;

public class Main {

    public static void main(final String[] args) throws ChessError {
        final GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);

        final Player firstPlayer = new LoBot(roomSettings, Color.WHITE, new Strategy());
        final Player secondPlayer = new RandomBot(roomSettings, Color.BLACK);

        final Selfplay game = new Selfplay(roomSettings, firstPlayer, secondPlayer);
        game.run();
        System.out.println(roomSettings.board.toString());
    }
}
