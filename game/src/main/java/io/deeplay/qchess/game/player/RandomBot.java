package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomBot extends RemotePlayer {

    public RandomBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "random-bot-" + UUID.randomUUID());
    }

    @Override
    public Move getNextMove() throws ChessError {
        List<Move> allMoves = ms.getAllCorrectMoves(color);
        Move move = allMoves.get(new Random().nextInt(allMoves.size()));
        turnIntoInQueen(move);
        return move;
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.RANDOM_BOT;
    }

    protected void turnIntoInQueen(Move move) {
        if (move.getMoveType() == MoveType.TURN_INTO) move.setTurnInto(FigureType.QUEEN);
    }
}
