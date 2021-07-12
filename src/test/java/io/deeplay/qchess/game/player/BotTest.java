package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import junit.framework.TestCase;

public class BotTest extends TestCase {

  public void testBots() throws ChessError {
    final int COUNT = 1;
    for (int i = 0; i < COUNT; i++) {
      GameSettings roomSettings = new GameSettings(Board.BoardFilling.STANDARD);
      Player firstPlayer = new Bot(roomSettings, Color.WHITE);
      Player secondPlayer = new Bot(roomSettings, Color.BLACK);
      Game game = new Game(roomSettings, firstPlayer, secondPlayer);
      game.run();
    }
  }
}
