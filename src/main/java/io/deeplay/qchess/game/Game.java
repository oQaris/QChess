package io.deeplay.qchess.game;

import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.IPlayer;

public class Game {

    private MoveSystem moveControl;
    private Board board = new Board();
    private IPlayer firstPlayer;
    private IPlayer secondPlayer;
    private IPlayer currentPlayerToMove;
    public Game(IPlayer firstPlayer, IPlayer secondPlayer /* , TODO: одно из правил игры (enum?) */) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.currentPlayerToMove = firstPlayer;
        this.moveControl = new MoveSystem(/* TODO: правила игры */);
    }

    public void start() {
        // TODO: сделать условие выхода
        while (true) {
            // TODO: отправлять json доски (или не отправлять), получать json Move
            Move move = currentPlayerToMove.getMove(board);

            if (moveControl.isCorrectMove(board, move)) {
                board.moveFigure(move);
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
    }
}
