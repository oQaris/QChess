package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;

public class Main {

    public static void main(String[] args) throws ChessError {
        Player firstPlayer = null;
        Player secondPlayer = null;
        Game game = new Game(Board.BoardFilling.STANDARD, firstPlayer, secondPlayer);
        game.run();
    }
}
