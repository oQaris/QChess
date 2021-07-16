package io.deeplay.qchess.game;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.ERROR_WHILE_ADD_PEACE_MOVE_COUNT;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_FILLING_BOARD;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Figure;
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
        // logger.info(roomSettings.board.toString());
        try {
            roomSettings.history.addRecord(null);
        } catch (ChessException | ChessError e) {
            logger.error("Возникло исключение в истории {}", e.getMessage());
            throw new ChessError(INCORRECT_FILLING_BOARD, e);
        }
        boolean isDraw = false;
        while (!roomSettings.endGameDetector.isStalemate(currentPlayerToMove.getColor())
                && !isDraw) {
            // TODO: получать Action, сделать предложение ничьи и возможность сдаться
            Move move = currentPlayerToMove.getNextMove();
            logger.info("От игрока пришел ход: {}", move);

            if (roomSettings.moveSystem.isCorrectMove(move)) {
                tryMove(move);
                isDraw = roomSettings.endGameDetector.isDraw();
                currentPlayerToMove =
                        currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ GameResponse, что ход некорректный
            }
        }
        if (isDraw) {
            logger.info("Игра окончена: ничья");
            if (roomSettings.endGameDetector.isDrawWithPeaceMoves())
                logger.error(
                        // "Причина ничьи: на протяжении {} ходов ни одна пешка не ходила и никто не
                        // рубил",
                        "Ничья: {} ходов без взятия и хода пешки",
                        EndGameDetector.END_PEACE_MOVE_COUNT);
            if (roomSettings.endGameDetector.isDrawWithRepetitions())
                logger.error(
                        // "Причина ничьи: было {} повторений позиций доски",
                        "Ничья: {} повторений позиций доски",
                        EndGameDetector.END_REPETITIONS_COUNT);
            if (roomSettings.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate())
                logger.error(
                        // "Причина ничьи: недостаточно фигур, чтобы поставить мат",
                        "Ничья: недостаточно фигур, чтобы поставить мат");
        } else if (roomSettings.endGameDetector.isCheckmate(currentPlayerToMove.getColor()))
            logger.error(
                    // "Игра окончена: мат {}",
                    "Мат: {}", currentPlayerToMove.getColor() == Color.WHITE ? "белым" : "черным");
        else
            logger.error(
                    // "Игра окончена: пат {}",
                    "Пат: {}", currentPlayerToMove.getColor() == Color.WHITE ? "белым" : "черным");

        // TODO: конец игры, отправлять GameResponse
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
            // logger.info(roomSettings.board.toString());
            return removedFigure;
        } catch (ChessException | NullPointerException e) {
            logger.error("Не удалось выполнить проверенный ход: {}", move);
            throw new ChessError(ERROR_WHILE_ADD_PEACE_MOVE_COUNT, e);
        }
    }
}
