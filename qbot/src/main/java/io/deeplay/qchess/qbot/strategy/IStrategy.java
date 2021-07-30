package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.model.Board;

public interface IStrategy {
    /** Функция оценки позиции на доске. Чем больше значение, тем лучше для белых */
    int evaluateBoard(Board board);
}
