package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NNNBot extends RemotePlayer {

    public NNNBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "n-nn-bot-" + UUID.randomUUID());
    }

    @Override
    public Move getNextMove() throws ChessError {
        // TODO: реализовать. Реализация из рандомного бота:
        List<Move> allMoves = ms.getAllCorrectMoves(color);
        Move move = allMoves.get(new Random().nextInt(allMoves.size()));
        turnIntoInQueen(move);
        return move;
    }

    private void turnIntoInQueen(Move move) {
        if (move.getMoveType() == MoveType.TURN_INTO) move.setTurnInto(FigureType.QUEEN);
    }
}
