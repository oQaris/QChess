package io.deeplay.qchess.game;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.ERROR_WHILE_ADD_PEACE_MOVE_COUNT;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.GAME_RESULT_ERROR;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.INCORRECT_FILLING_BOARD;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
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

    /** @throws ChessError если заполнение доски некорректное */
    public Selfplay(GameSettings roomSettings, Player firstPlayer, Player secondPlayer)
            throws ChessError {
        if (firstPlayer.getColor() == secondPlayer.getColor())
            throw new IllegalArgumentException("Должны быть разные цвета!");
        this.roomSettings = roomSettings;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        currentPlayerToMove = firstPlayer;
        try {
            roomSettings.history.addRecord(null);
        } catch (ChessError | NullPointerException e) {
            logger.error("Возникло исключение в истории {}", e.getMessage());
            throw new ChessError(INCORRECT_FILLING_BOARD, e);
        }
    }

    public static Move createMove(String from, String to, String type) {
        return new Move(MoveType.valueOf(type), Cell.parse(from), Cell.parse(to));
    }

    /**
     * @return true, если ход корректный, иначе false
     * @throws ChessError если во время игры случилась критическая ошибка
     */
    public boolean move(Move move) throws ChessError {
        if (isCorrectPlayerColor(move) && roomSettings.moveSystem.isCorrectMove(move)) {
            tryMove(move);
            currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Обновляется после каждого хода
     *
     * @return игрок, чей ход следующий
     */
    public Player getCurrentPlayerToMove() {
        return currentPlayerToMove;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    private boolean isCorrectPlayerColor(Move move) {
        try {
            return roomSettings.board.getFigure(move.getFrom()).getColor()
                    == currentPlayerToMove.getColor();
        } catch (ChessException | NullPointerException e) {
            return false;
        }
    }

    /** @deprecated Можно запускать только один раз. Используется только для проверки игры */
    @Deprecated
    public void run() throws ChessError {
        long startTime = 0;
        EndGameDetector egd = roomSettings.endGameDetector;
        while (egd.getGameResult() == EndGameDetector.EndGameType.NOTHING) {
            // TODO: получать Action, сделать предложение ничьи и возможность сдаться
            if (logger.isDebugEnabled()) startTime = System.currentTimeMillis();
            Move move = currentPlayerToMove.getNextMove();
            if (logger.isDebugEnabled()) {
                long time = System.currentTimeMillis() - startTime;
                logger.debug("Время на ход: {} sec", time / 1000.);
            }

            if (roomSettings.moveSystem.isCorrectMove(move)) {
                tryMove(move);
                currentPlayerToMove =
                        currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ GameResponse, что ход некорректный
                throw new IllegalArgumentException("некорректный ход");
            }
            egd.updateEndGameStatus(currentPlayerToMove.getColor());
        }
        switch (egd.getGameResult()) {
            case NOTHING -> throw new ChessError(GAME_RESULT_ERROR);
            case CHECKMATE_TO_WHITE -> logger.info("Мат белым");
            case CHECKMATE_TO_BLACK -> logger.info("Мат черным");
            case STALEMATE_TO_WHITE -> logger.info("Пат белым");
            case STALEMATE_TO_BLACK -> logger.info("Пат черным");
            case DRAW_WITH_REPETITIONS -> logger.info(
                    "Ничья: {} повторений позиций доски", EndGameDetector.END_REPETITIONS_COUNT);
            case DRAW_WITH_NOT_ENOUGH_MATERIAL -> logger.info(
                    "Ничья: недостаточно фигур, чтобы поставить мат");
            case DRAW_WITH_PEACE_MOVE_COUNT -> logger.info(
                    "Ничья: {} ходов без взятия и хода пешки",
                    EndGameDetector.END_PEACE_MOVE_COUNT);
        }

        // TODO: конец игры, отправлять GameResponse
    }

    /** @return удаленная фигура или null, если клетка была пуста */
    private Figure tryMove(Move move) throws ChessError {
        try {
            Figure removedFigure = roomSettings.moveSystem.move(move);
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "{} сделал ход: {} фигурой: {}",
                        currentPlayerToMove,
                        move,
                        roomSettings.board.getFigureUgly(move.getTo()));
                logger.debug(roomSettings.board.toString());
                logger.debug("FEN: {}", roomSettings.history.getBoardToStringForsythEdwards());
                logger.debug(
                        "board hash: {}",
                        roomSettings.history.getLastBoardState().boardSnapshotHash);
                logger.debug(
                        "board snapshot: {}",
                        roomSettings.history.getLastBoardState().boardSnapshot);
                logger.debug("<---------------------------------------------------------------->");
            }
            return removedFigure;
        } catch (NullPointerException e) {
            logger.error("Не удалось выполнить проверенный ход: {}", move);
            throw new ChessError(ERROR_WHILE_ADD_PEACE_MOVE_COUNT, e);
        }
    }
}
