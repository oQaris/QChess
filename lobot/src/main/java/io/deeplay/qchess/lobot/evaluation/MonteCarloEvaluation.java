package io.deeplay.qchess.lobot.evaluation;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.AttackBot;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.game.player.RandomBot;
import io.deeplay.qchess.lobot.montecarloservice.MonteCarloSelfplay;

public class MonteCarloEvaluation implements Evaluation {
    private final int iterationNumber;
    private final PlayerType botType;

    public MonteCarloEvaluation(final int iterationsNumber, boolean attacked) {
        this.iterationNumber = iterationsNumber;
        this.botType = attacked? PlayerType.ATTACK_BOT : PlayerType.RANDOM_BOT;
    }

    @Override
    public int evaluateBoard(final GameSettings gameSettings, final Color color) {
        MonteCarloSelfplay game;
        Player firstPlayer;
        Player secondPlayer;
        int wins = 0;
        for(int i = 0; i < iterationNumber; i++) {
            final GameSettings gs = new GameSettings(gameSettings);
            firstPlayer = botType == PlayerType.ATTACK_BOT? new AttackBot(gs, color) : new RandomBot(gs, color);
            secondPlayer = botType == PlayerType.ATTACK_BOT? new AttackBot(gs, color.inverse()) : new RandomBot(gs, color.inverse());
            try {
                game = new MonteCarloSelfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError chessError) {
                chessError.printStackTrace();
            }
            EndGameType endResult = gs.endGameDetector.getGameResult();
            if(color == Color.WHITE && endResult == EndGameType.CHECKMATE_TO_BLACK || color == Color.BLACK && endResult == EndGameType.CHECKMATE_TO_WHITE) {
                wins++;
            }
        }
        return (wins * 1000) / iterationNumber;
    }
}
