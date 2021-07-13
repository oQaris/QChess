package io.deeplay.qchess.game;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.LOG_FAILED;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    public final Player secondPlayer;
    public final Player firstPlayer;
    private final GameSettings roomSettings;
    private Player currentPlayerToMove;

    public Game(GameSettings roomSettings, Player firstPlayer, Player secondPlayer) {
        this.roomSettings = roomSettings;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        currentPlayerToMove = firstPlayer;
    }

    public void run() throws ChessError {
        logger.info(roomSettings.board.toString());
        boolean notDraw = true;
        while (!roomSettings.endGameDetector.isStalemate(currentPlayerToMove.getColor())
                && notDraw) {
            // TODO: получать json Move
            Move move = currentPlayerToMove.getNextMove();

            if (roomSettings.moveSystem.isCorrectMove(move)) {
                Figure removedFigure = tryMove(move);
                notDraw = roomSettings.endGameDetector.isNotDraw(removedFigure, move);
                currentPlayerToMove =
                        currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
        if (!notDraw) Game.logger.info("Игра окончена: ничья");
        else if (roomSettings.endGameDetector.isCheckmate(currentPlayerToMove.getColor()))
            Game.logger.info(
                    "Игра окончена: мат {}",
                    currentPlayerToMove.getColor() == Color.WHITE ? "белым" : "черным");
        else
            Game.logger.info(
                    "Игра окончена: пат {}",
                    currentPlayerToMove.getColor() == Color.WHITE ? "белым" : "черным");

        // TODO: конец игры
    }

    /** @return удаленная фигура или null, если клетка была пуста */
    private Figure tryMove(Move move) throws ChessError {
        try {
            Figure removedFigure = roomSettings.moveSystem.move(move);
            logger.info(
                    "{} сделал ход: {} фигурой: {}",
                    currentPlayerToMove,
                    move,
                    roomSettings.board.getFigure(move.getTo()));
            logger.info(roomSettings.board.toString());
            return removedFigure;
        } catch (ChessException e) {
            throw new ChessError(LOG_FAILED, e);
        }
    }
}
