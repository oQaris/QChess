package io.deeplay.qchess.nukebot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.UUID;

public class NukeBot extends RemotePlayer {

    private final SearchFunc<?> searchFunc;

    public NukeBot(
            final GameSettings roomSettings, final Color color, final SearchFunc<?> searchFunc) {
        super(roomSettings, color, "nuke-bot-" + UUID.randomUUID(), "nuke-bot");
        this.searchFunc = searchFunc;
    }

    @Override
    public Move getNextMove() throws ChessError {
        return searchFunc.findBest();
    }
}
