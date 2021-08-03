package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;

public interface IStrategy {
    /**
     * Функция оценки позиции на доске. Чем больше значение, тем лучше для белых
     */
    int evaluateBoard(Board board);

    static int gradeIfTerminalNode(GameSettings gs, Color activeColor) {
        gs.endGameDetector.updateEndGameStatus();
        EndGameDetector.EndGameType result = gs.endGameDetector.getGameResult();
        return switch (result) {
            case CHECKMATE_TO_BLACK -> activeColor == Color.WHITE ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            case CHECKMATE_TO_WHITE -> activeColor == Color.BLACK ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            case STALEMATE_TO_BLACK -> (activeColor == Color.WHITE ? Integer.MAX_VALUE : Integer.MIN_VALUE) / 2;
            case STALEMATE_TO_WHITE -> (activeColor == Color.BLACK ? Integer.MAX_VALUE : Integer.MIN_VALUE) / 2;
            case NOTHING -> throw new IllegalArgumentException("Состояние не является терминальным!");
            // Ничьи
            default -> 0;
        };
    }
}
