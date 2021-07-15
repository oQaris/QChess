package io.deeplay.qchess.game;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.ERROR_WHILE_ADD_PEACE_MOVE_COUNT;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_FILLING_BOARD;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Selfplay {
    private static final Logger logger = LoggerFactory.getLogger(Selfplay.class);
    private final Player secondPlayer;
    private final Player firstPlayer;
    private final GameSettings roomSettings;
    private Player currentPlayerToMove;

    public Selfplay(GameSettings roomSettings, Player firstPlayer, Player secondPlayer) {
        this.roomSettings = roomSettings;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        currentPlayerToMove = firstPlayer;
    }

    public void run() throws ChessError {
        logger.info(roomSettings.board.toString());
        try {
            roomSettings.history.addRecord(null);
        } catch (ChessException | ChessError e) {
            logger.error("Возникло исключение в истории {}", e.getMessage());
            throw new ChessError(INCORRECT_FILLING_BOARD, e);
        }
        boolean isDraw = false;
        while (!roomSettings.endGameDetector.isStalemate(currentPlayerToMove.getColor())
                && !isDraw) {
            // TODO: получать json Move
            Move move = currentPlayerToMove.getNextMove();
            logger.info("От игрока пришел ход: {}", move);

            if (roomSettings.moveSystem.isCorrectMove(move)) {
                tryMove(move);
                isDraw = roomSettings.endGameDetector.isDraw();
                currentPlayerToMove =
                        currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
        if (isDraw) {
            logger.info("Игра окончена: ничья");
            if (roomSettings.endGameDetector.isDrawWithPeaceMoves())
                logger.info(
                        "Причина ничьи: на протяжении {} ходов ни одна пешка не ходила и никто не рубил",
                        EndGameDetector.END_PEACE_MOVE_COUNT);
            if (roomSettings.endGameDetector.isDrawWithRepetitions())
                logger.info(
                        "Причина ничьи: было {} повторений позиций доски",
                        EndGameDetector.END_REPETITIONS_COUNT);
            if (roomSettings.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate())
                logger.info("Причина ничьи: недостаточно фигур, чтобы поставить мат");
        } else if (roomSettings.endGameDetector.isCheckmate(currentPlayerToMove.getColor()))
            logger.info(
                    "Игра окончена: мат {}",
                    currentPlayerToMove.getColor() == Color.WHITE ? "белым" : "черным");
        else
            logger.info(
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
        } catch (ChessException | NullPointerException e) {
            logger.error("Не удалось выполнить проверенный ход: {}", move);
            throw new ChessError(ERROR_WHILE_ADD_PEACE_MOVE_COUNT, e);
        }
    }
}
