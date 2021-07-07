package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.player.ConsolePlayer;
import io.deeplay.qchess.game.player.Player;

public class Main {

    public static void main(String[] args) throws ChessError {
        Player firstPlayer = new ConsolePlayer();
        Player secondPlayer = new ConsolePlayer();
        Game game = new Game(Board.BoardFilling.STANDARD, firstPlayer, secondPlayer);
        game.run();
    }
}
