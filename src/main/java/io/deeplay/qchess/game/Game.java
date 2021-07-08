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
        board = new Board();
        moveSystem = new MoveSystem(board);
        board.initBoard(moveSystem, boardType);

        firstPlayer.init(moveSystem, board, true);
        secondPlayer.init(moveSystem, board, false);

        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        currentPlayerToMove = firstPlayer;
    }

    public void run() throws ChessError {
        boolean notDraw = true;
        while (!moveSystem.isStalemate(currentPlayerToMove.getColor()) && notDraw) {
            // TODO: получать json Move
            Move move = currentPlayerToMove.getNextMove();

            if (moveSystem.isCorrectMove(move)) {
                notDraw = tryMove(move);
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
        if (!notDraw) {
            logger.info("Игра окончена: ничья");
        } else if (moveSystem.isCheckmate(currentPlayerToMove.getColor())) {
            logger.info("Игра окончена: мат {}", currentPlayerToMove.getColor() ? "белым" : "черным");
        } else {
            logger.info("Игра окончена: пат {}", currentPlayerToMove.getColor() ? "белым" : "черным");
        }
        // TODO: конец игры
    }

    private boolean tryMove(Move move) throws ChessError {
        try {
            logger.info("{} сделал ход: {} фигурой: {}", currentPlayerToMove, move, board.getFigure(move.getFrom()));
            logger.info(board.toString());
            return moveSystem.move(move);
        } catch (ChessException e) {
            throw new ChessError("Не удалось записать в лог", e);
        }
    }
}