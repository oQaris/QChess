package io.deeplay.qchess.game;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.figures.Pawn;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    public final Player firstPlayer;
    public final Player secondPlayer;
    private final GameSettings roomSettings;
    private Player currentPlayerToMove;
    private int pieceMoveCount = 0;

    public Game(GameSettings roomSettings, Player firstPlayer, Player secondPlayer) {
        this.roomSettings = roomSettings;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        currentPlayerToMove = firstPlayer;
    }

    public void run() throws ChessError {
        logger.info(roomSettings.board.toString());     //todo SonarLint ругается
        var notDraw = true;
        while (!roomSettings.moveSystem.isStalemate(currentPlayerToMove.getColor()) && notDraw) {
            // TODO: получать json Move
            var move = currentPlayerToMove.getNextMove();

            if (roomSettings.moveSystem.isCorrectMove(move)) {
                var removedFigure = tryMove(move);
                notDraw = isNotDraw(removedFigure, move);
                currentPlayerToMove = currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;
            } else {
                // TODO: отправлять ответ, что ход некорректный
            }
        }
        if (!notDraw) {
            logger.info("Игра окончена: ничья");
        } else if (roomSettings.moveSystem.isCheckmate(currentPlayerToMove.getColor())) {
            logger.info("Игра окончена: мат {}", currentPlayerToMove.getColor() == Color.WHITE ? "белым" : "черным");
        } else {
            logger.info("Игра окончена: пат {}", currentPlayerToMove.getColor() == Color.WHITE ? "белым" : "черным");
        }
        // TODO: конец игры
    }

    /**
     * @return удаленная фигура или null, если клетка была пуста
     */
    private Figure tryMove(Move move) throws ChessError {
        try {
            var removedFigure = roomSettings.moveSystem.move(move);
            logger.info("{} сделал ход: {} фигурой: {}", currentPlayerToMove, move, roomSettings.board.getFigure(move.getTo()));
            logger.info(roomSettings.board.toString());     //todo SonarLint ругается
            return removedFigure;
        } catch (ChessException e) {
            throw new ChessError("Не удалось записать в лог", e);
        }
    }

    private boolean isNotDraw(Figure removedFigure, Move move) throws ChessError {
        try {
            // условия ничьи:
            // пешка не ходит 50 ходов
            // никто не рубит
            if (removedFigure != null || roomSettings.board.getFigure(move.getTo()).getClass() == Pawn.class) {
                pieceMoveCount = 0;
            } else {
                ++pieceMoveCount;
            }
            return pieceMoveCount != 50;
        } catch (ChessException e) {
            throw new ChessError("Ошибка при проверки на ничью", e);
        }
    }
}