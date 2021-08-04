package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Board;

public interface IStrategy {
    static int gradeIfTerminalNode(GameSettings gs) {
        gs.endGameDetector.updateEndGameStatus();
        EndGameDetector.EndGameType result = gs.endGameDetector.getGameResult();
        gs.endGameDetector.revertEndGameStatus();
        return switch (result) {
            case CHECKMATE_TO_BLACK -> Integer.MAX_VALUE - 100;
            case CHECKMATE_TO_WHITE -> Integer.MIN_VALUE + 100;
            case STALEMATE_TO_BLACK -> Integer.MAX_VALUE / 2;
            case STALEMATE_TO_WHITE -> Integer.MIN_VALUE / 2;
            case NOTHING -> throw new IllegalArgumentException("Состояние не является терминальным!");
            // Ничьи
            default -> 0;
        };
    }

    /**
     * Функция оценки позиции на доске. Чем больше значение, тем лучше для белых
     */
    int evaluateBoard(Board board);
}
