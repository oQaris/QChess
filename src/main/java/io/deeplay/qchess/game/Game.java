package io.deeplay.qchess.game;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.IPlayer;

public final class Game {

    private static Game game = new Game();

    private Game() {
    }

    public static Game initGame() {
        return game;
    }

    private Board board;
    private IPlayer firstPlayer;
    private IPlayer secondPlayer;
    private IPlayer currentPlayerToMove;

    public void start() {
        Move move = currentPlayerToMove.getMove();

        update(move);

        currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
    }

    private void update(Move move) {
        // TODO: применение хода и обновление доски
    }
}
