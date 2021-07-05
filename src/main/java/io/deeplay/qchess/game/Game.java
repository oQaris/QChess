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

    private Board board = Board.getBoard();
    private IPlayer firstPlayer;
    private IPlayer secondPlayer;
    private IPlayer currentPlayerToMove;

    public void start() {
        // TODO: сделать условие выхода
        while (true) {
            Move move = currentPlayerToMove.getMove(board);

            board.moveFigure(move);

            currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
        }
    }
}
