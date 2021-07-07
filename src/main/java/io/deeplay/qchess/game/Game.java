package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.IPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

    public Game(Board.BoardFilling boardType, IPlayer firstPlayer, IPlayer secondPlayer) throws ChessError {
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
    private IPlayer firstPlayer;
    private IPlayer secondPlayer;
    private IPlayer currentPlayerToMove;

    public void run() throws ChessError {
        // TODO: сделать условие выхода
        while (true) {
            // TODO: получать json Move
            Move move = currentPlayerToMove.getNextMove();

            if (moveSystem.isCorrectMove(move)) {
                tryMove(move);
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
    }

    private void tryMove(Move move) {
        try {
            moveSystem.move(move);
        } catch (ChessException e) {
            logger.error("Проверенный ход выдал ошибку при перемещении фигуры: {}", e.getMessage());
            // TODO: выкинуть из комнаты
        }
    }
}
