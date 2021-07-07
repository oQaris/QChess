package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    private Board board;
    private MoveSystem moveSystem;
    private Player firstPlayer;
    private Player secondPlayer;
    private Player currentPlayerToMove;

    public Game(Board.BoardFilling boardType, Player firstPlayer, Player secondPlayer) throws ChessError {
        this.board = new Board();
        this.moveSystem = new MoveSystem(board);
        board.initBoard(moveSystem, boardType);

        firstPlayer.init(moveSystem, board, true);
        secondPlayer.init(moveSystem, board, false);

        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.currentPlayerToMove = firstPlayer;
    }

    public void run() throws ChessError {
        // TODO: сделать условие выхода
        while (!moveSystem.isCheckmate(currentPlayerToMove.getColor())) {
            // TODO: получать json Move
            Move move = currentPlayerToMove.getNextMove();
            try {
                logger.info("{} сделал ход: {} фигурой: {}", currentPlayerToMove, move, board.getFigure(move.getFrom()));
                logger.info(board.toString());
            } catch (ChessException ignored) {
            }

            if (moveSystem.isCorrectMove(move)) {
                tryMove(move);
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
        // TODO: конец игры
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
