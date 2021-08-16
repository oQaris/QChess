package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomBot extends RemotePlayer {

    public RandomBot(final GameSettings roomSettings, final Color color) {
        super(roomSettings, color, "random-bot-" + UUID.randomUUID(), "Рандомный_Бот");
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> allMoves = ms.getAllPreparedMoves(color);
        return allMoves.get(new Random().nextInt(allMoves.size()));
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.RANDOM_BOT;
    }
}
