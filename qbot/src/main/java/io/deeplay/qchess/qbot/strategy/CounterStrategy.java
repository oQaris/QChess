package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;

public class CounterStrategy implements IStrategy {
    @Override
    public int evaluateBoard(Board board) {
        return board.getFigureCount(Color.WHITE) - board.getFigureCount(Color.BLACK);
    }
}
