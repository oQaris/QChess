package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.player.IPlayer;

public class Main {

    public static void main(String[] args) throws ChessError {
        IPlayer firstPlayer = null;
        IPlayer secondPlayer = null;
        Game game = new Game(Board.BoardFilling.STANDARD, firstPlayer, secondPlayer);
        game.run();
    }
}
