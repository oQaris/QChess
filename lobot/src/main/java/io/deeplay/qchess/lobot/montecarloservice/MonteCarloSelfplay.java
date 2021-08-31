package io.deeplay.qchess.lobot.montecarloservice;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.Player;

public class MonteCarloSelfplay {

    private final Player secondPlayer;
    private final Player firstPlayer;
    private final GameSettings roomSettings;
    private Player currentPlayerToMove;

    public MonteCarloSelfplay(
        final GameSettings roomSettings, final Player firstPlayer, final Player secondPlayer) {
        this.roomSettings = roomSettings;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        currentPlayerToMove = firstPlayer;
    }

    public void run() throws ChessError {
        final EndGameDetector egd = roomSettings.endGameDetector;
        while (egd.getGameResult() == EndGameDetector.EndGameType.NOTHING) {
            final Move move = currentPlayerToMove.getNextMove();

            roomSettings.moveSystem.move(move);
            currentPlayerToMove =
                currentPlayerToMove == firstPlayer ? secondPlayer : firstPlayer;

            egd.updateEndGameStatus(currentPlayerToMove.getColor());
        }
    }
}

