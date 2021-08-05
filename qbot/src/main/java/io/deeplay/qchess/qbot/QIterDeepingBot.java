package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.RemotePlayer;

public class QIterDeepingBot extends RemotePlayer {

    public QIterDeepingBot(GameSettings roomSettings, Color color, String sessionToken) {
        super(roomSettings, color, sessionToken);
    }

    /*public QIterDeepingBot(GameSettings roomSettings,
        Color color, String sessionToken) {
      super(roomSettings, color, sessionToken);
    }

    //I return a string, int pair. The string represents the best move found, while the int represents the engine evaluation for the node after said move is made
    static std::pair<string, int> iterativeDeepeningSearch(Board initialPosition, int maxDepth, long maxSearchTime)
    {
      std::pair<string, int> bestMove;
      long startTime = System.currentTimeMillis();

      for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++)
      {

        long currentTime = System.currentTimeMillis();
        if (currentTime > startTime + maxSearchTime)
        {
          return bestMove;
        }
        std::pair<string, int> bestMoveFoundFromMinimax = minimax(initialPosition, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, "", "", startTime + maxSearchTime);
        if (bestMoveFoundFromMinimax.first != "") {
          bestMove = bestMoveFoundFromMinimax;
        }
      }
      return bestMove;
    }*/
}
