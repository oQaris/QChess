package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.IPlayer;

public final class Game {

    private static Game game;
    private Board board = Board.getBoard();
    private IPlayer firstPlayer;
    private IPlayer secondPlayer;
    private IPlayer currentPlayerToMove;

    private Game(IPlayer firstPlayer, IPlayer secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.currentPlayerToMove = firstPlayer;
    }

    public static Game initGame(IPlayer firstPlayer, IPlayer secondPlayer) {
        if (game == null) {
            game = new Game(firstPlayer, secondPlayer);
        }
        return game;
    }

    public void start() {
        // TODO: сделать условие выхода
        while (true) {
            // TODO: отправлять json доски (или не отправлять), получать json Move
            Move move = currentPlayerToMove.getMove(board);

            boolean moveSuccess = false;
            try {
                moveSuccess = board.moveFigure(move);
            } catch (ChessException e) {
                // TODO: отправлять ответ, что ход некорректный
            }

            if (moveSuccess) {
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            }
        }
    }
}
