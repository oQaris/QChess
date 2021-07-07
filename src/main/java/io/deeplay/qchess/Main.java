package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.player.Player;

public class Main {

    public static void main(String[] args) {
        Player firstPlayer = null;
        Player secondPlayer = null;
        Game game = new Game(Board.BoardFilling.STANDARD, firstPlayer, secondPlayer);
        game.start();
    }
}
