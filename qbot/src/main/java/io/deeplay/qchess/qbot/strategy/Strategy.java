package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.qbot.QBot;

public interface Strategy {
    int MAX_VAL = Integer.MAX_VALUE;
    int MIN_VAL = Integer.MIN_VALUE + 1;
    int MAX_EST = MAX_VAL - QBot.MAX_DEPTH;
    int MIN_EST = MIN_VAL + QBot.MAX_DEPTH;

    /**
     * Функция оценки доски для терминальных узлов (может быть переопределена, но не обязательно)
     */
    default int gradeIfTerminalNode(final EndGameType endGameStatus, final int curDepth) {
        return switch (endGameStatus) {
            case CHECKMATE_TO_BLACK -> MAX_EST + curDepth;
            case CHECKMATE_TO_WHITE -> MIN_EST - curDepth;
            default -> 0; // Ничьи и пат
        };
    }

    /** Функция оценки позиции на доске. Чем больше значение, тем лучше для белых */
    int evaluateBoard(Board board);
}
