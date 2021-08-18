package io.deeplay.qchess.lobot.evaluation;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.lobot.montecarloservice.MonteCarloSelfplay;

public class MonteCarloEvaluation implements Evaluation {

    private final int iterationNumber;

    public MonteCarloEvaluation(final int iterationsNumber) {
        iterationNumber = iterationsNumber;
    }

    @Override
    public int evaluateBoard(final GameSettings gameSettings, final Color color) {
        MonteCarloSelfplay game;
        GameSettings gs;
        Player firstPlayer;
        Player secondPlayer;
        int wins = 0;
        for (int i = 0; i < iterationNumber; i++) {
            gs = new GameSettings(gameSettings, 100);
            firstPlayer = new AttackBot(gs, color);
            secondPlayer = new AttackBot(gs, color.inverse());
            try {
                game = new MonteCarloSelfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (final ChessError chessError) {
                chessError.printStackTrace();
            }
            final EndGameType endResult = gs.endGameDetector.getGameResult();
            if (color == Color.WHITE && endResult == EndGameType.CHECKMATE_TO_BLACK
                    || color == Color.BLACK && endResult == EndGameType.CHECKMATE_TO_WHITE) {
                wins++;
            }
        }
        // System.out.println((wins * 1000) / iterationNumber);
        return (wins * 1000) / iterationNumber;
    }
}
