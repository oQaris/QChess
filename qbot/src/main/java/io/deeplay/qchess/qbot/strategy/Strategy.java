package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.qbot.QMinimaxBot;

public abstract class Strategy {

    /**
     * Функция оценки доски для терминальных узлов (может быть переопределена, но не обязательно)
     */
    public int gradeIfTerminalNode(EndGameType endGameStatus) {
        return switch (endGameStatus) {
            case CHECKMATE_TO_BLACK -> Integer.MAX_VALUE - QMinimaxBot.MAX_DEPTH;
            case CHECKMATE_TO_WHITE -> Integer.MIN_VALUE + QMinimaxBot.MAX_DEPTH;
            case STALEMATE_TO_BLACK -> Integer.MAX_VALUE / 2;
            case STALEMATE_TO_WHITE -> Integer.MIN_VALUE / 2;
            case NOTHING -> throw new IllegalArgumentException(
                    "Состояние не является терминальным!");
            default -> 0; // Ничьи
        };
    }

    /** Функция оценки позиции на доске. Чем больше значение, тем лучше для белых */
    public abstract int evaluateBoard(Board board);
}
