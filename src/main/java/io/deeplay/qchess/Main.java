package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.player.IPlayer;

public class Main {

    public static void main(String[] args) {
        IPlayer firstPlayer = null;
        IPlayer secondPlayer = null;
        Game game = new Game(firstPlayer, secondPlayer);
        try {
            game.start();
        } catch (ChessException ex) {
        }
    }
}
