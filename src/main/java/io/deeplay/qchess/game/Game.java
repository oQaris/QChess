package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

    public Game(Board.BoardFilling boardType, Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.currentPlayerToMove = firstPlayer;
        this.board = new Board();
        this.moveSystem = new MoveSystem(board);
        board.initBoard(moveSystem, boardType);
    }

    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    private Board board;
    private MoveSystem moveSystem;
    private Player firstPlayer;
    private Player secondPlayer;
    private Player currentPlayerToMove;

    public void start() {
        // TODO: сделать условие выхода
        while (true) {
            // TODO: получать json Move
            Move move = currentPlayerToMove.getNextMove();

            if (tryIsCorrectMove(move)) {
                tryMove(move);
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
    }

    private boolean tryIsCorrectMove(Move move) {
        try {
            return moveSystem.isCorrectMove(move);
        } catch (ChessException e) {
            logger.error("Возникла невозможная ситуация: {}", e.getMessage());
            return false;
        }
    }

    private void tryMove(Move move) {
        try {
            moveSystem.move(move);
        } catch (ChessException e) {
            logger.error("Проверенный ход выдал ошибку при перемещении фигуры: {}", e.getMessage());
        }
    }
}
