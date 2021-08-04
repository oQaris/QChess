package io.deeplay.qchess.qbot.strategy;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;

public class MonteCarloStrategy implements IStrategy {
    @Override
    public int evaluateBoard(Board board) {
        int wins = 0;
        for (int i = 0; i < 10; i++) {
            GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer = new RandomBot(gs, Color.WHITE);
            Player secondPlayer = new RandomBot(gs, Color.BLACK);
            try {
                Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }
            EndGameDetector.EndGameType result = gs.endGameDetector.getGameResult();
            wins += switch (result) {
                case CHECKMATE_TO_BLACK -> 2;
                case CHECKMATE_TO_WHITE -> -2;
                case STALEMATE_TO_BLACK -> 1;
                case STALEMATE_TO_WHITE -> -1;
                default -> 0;
            };
        }
        return wins;
    }
}
