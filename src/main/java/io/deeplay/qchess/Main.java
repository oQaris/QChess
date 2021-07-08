package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.player.Bot;
import io.deeplay.qchess.game.player.Player;

public class Main {

    public static void main(String[] args) throws ChessError {
        Player firstPlayer = new Bot();
        Player secondPlayer = new Bot();
        Game game = new Game(Board.BoardFilling.STANDARD, firstPlayer, secondPlayer);
        game.run();
    }
}