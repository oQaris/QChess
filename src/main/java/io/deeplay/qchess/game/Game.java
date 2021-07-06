package io.deeplay.qchess.game;

import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.IPlayer;

public class Game {

    public Game(IPlayer firstPlayer, IPlayer secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.currentPlayerToMove = firstPlayer;
        this.moveSystem = new MoveSystem();
    }

    private MoveSystem moveSystem;
    private Board board = new Board();
    private IPlayer firstPlayer;
    private IPlayer secondPlayer;
    private IPlayer currentPlayerToMove;

    public void start() {
        // TODO: сделать условие выхода
        while (true) {
            // TODO: отправлять json доски (или не отправлять), получать json Move
            Move move = currentPlayerToMove.getMove(board);

            if (moveSystem.isCorrectMove(board, move)) {
                moveSystem.move(board, move);
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
    }
}
