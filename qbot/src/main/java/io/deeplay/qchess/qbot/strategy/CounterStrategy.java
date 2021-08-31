package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;

/** Супер-простая стратегия оценки доски, учитывающая только количество фигур. */
public class CounterStrategy implements Strategy {
    @Override
    public int evaluateBoard(final Board board) {
        return board.getFigureCount(Color.WHITE) - board.getFigureCount(Color.BLACK);
    }
}
