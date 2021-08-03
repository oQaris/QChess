package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.model.Move;

public class BotBoardUtility {
    private final BotBoard bb;
    private final Move move;
    private final int utility;

    public BotBoardUtility(BotBoard bb, Move move, int utility) {
        this.bb = bb;
        this.move = move;
        this.utility = utility;
    }

    public BotBoard getBb() {
        return bb;
    }

    public Move getMove() {
        return move;
    }

    public int getUtility() {
        return utility;
    }
}
