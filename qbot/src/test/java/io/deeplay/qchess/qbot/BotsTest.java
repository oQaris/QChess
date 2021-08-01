package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.Player;
import io.deeplay.qchess.game.player.RandomBot;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BotsTest {
    private static final Logger logger = LoggerFactory.getLogger(BotsTest.class);

    private static final int COUNT = 5;
    private static final Map<EndGameType, Integer> gameResultsWhite = initGameResults();
    private static final Map<EndGameType, Integer> gameResultsBlack = initGameResults();
    private static final AtomicInteger doneTasks = new AtomicInteger(0);

    private static Map<EndGameType, Integer> initGameResults() {
        return new ConcurrentHashMap<>(
                Map.of(
                        EndGameType.CHECKMATE_TO_BLACK, 0,
                        EndGameType.CHECKMATE_TO_WHITE, 0,
                        EndGameType.STALEMATE_TO_BLACK, 0,
                        EndGameType.STALEMATE_TO_WHITE, 0,
                        EndGameType.DRAW_WITH_NOT_ENOUGH_MATERIAL, 0,
                        EndGameType.DRAW_WITH_PEACE_MOVE_COUNT, 0,
                        EndGameType.DRAW_WITH_REPETITIONS, 0));
    }

    @Test
    void testGame() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= COUNT; i++) {
            executor.execute(
                    i % 2 == 0
                            ? new Game(Color.WHITE, gameResultsWhite)
                            : new Game(Color.BLACK, gameResultsBlack));
        }
        executor.shutdown();
        executor.awaitTermination(9999, TimeUnit.DAYS);
        long timeInSec = (System.currentTimeMillis() - startTime) / 1000;

        logger.info("\n<------------------------------------------------------>");
        logger.info("Time: {} min {} sec", timeInSec / 60, timeInSec % 60);
        /*logger.info(
            "Draw count: {}\n"
                    + " Draw with peace move count: {}\n"
                    + " Draw with repetitions: {}\n"
                    + " Draw with not enough material: {}\n"
                    + "Checkmate to QBot: {}\n"
                    + "Checkmate to opponent: {}\n"
                    + "Stalemate to QBot: {}\n"
                    + "Stalemate to opponent: {}",
        gameResults.get(DRAW_WITH_PEACE_MOVE_COUNT)
                    + gameResults.get(DRAW_WITH_REPETITIONS)
                    + gameResults.get(DRAW_WITH_NOT_ENOUGH_MATERIAL),
        gameResults.get(DRAW_WITH_PEACE_MOVE_COUNT),gameResults.get(DRAW_WITH_REPETITIONS),
            gameResults.get(DRAW_WITH_NOT_ENOUGH_MATERIAL),
            checkmateToNNNBot,
            checkmateToOpponent,
            stalemateToNNNBot,
            stalemateToOpponent);*/
    }

    private static class Game implements Runnable {
        private final Color QBotColor;
        private final Map<EndGameType, Integer> resultsOutput;

        public Game(Color QBotColor, Map<EndGameType, Integer> resultsOutput) {
            this.QBotColor = QBotColor;
            this.resultsOutput = resultsOutput;
        }

        @Override
        public void run() {
            GameSettings gs = new GameSettings(Board.BoardFilling.STANDARD);
            Player firstPlayer;
            Player secondPlayer;

            if (QBotColor == Color.WHITE) {
                firstPlayer = new QBot(gs, Color.WHITE);
                secondPlayer = new RandomBot(gs, Color.BLACK);
            } else {
                firstPlayer = new RandomBot(gs, Color.WHITE);
                secondPlayer = new QBot(gs, Color.BLACK);
            }

            try {
                Selfplay game = new Selfplay(gs, firstPlayer, secondPlayer);
                game.run();
            } catch (ChessError e) {
                e.printStackTrace();
            }

            resultsOutput.computeIfPresent(gs.endGameDetector.getGameResult(), (k, v) -> v + 1);

            /*switch (gs.endGameDetector.getGameResult()){
                case DRAW_WITH_REPETITIONS -> {}
                case DRAW_WITH_PEACE_MOVE_COUNT -> {}
                case DRAW_WITH_NOT_ENOUGH_MATERIAL -> {}
                case CHECKMATE_TO_BLACK -> {}
                case CHECKMATE_TO_WHITE -> {}
                case STALEMATE_TO_BLACK -> {}
                case STALEMATE_TO_WHITE -> {}
            }*/

            /*if (gs.endGameDetector.isDraw()) {
                if (gs.endGameDetector.isDrawWithPeaceMoves()) ++drawWithPeaceMoveCount;
                if (gs.endGameDetector.isDrawWithRepetitions()) ++drawWithRepetitions;
                if (gs.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate())
                    ++drawWithNotEnoughMaterial;
            } else if (gs.endGameDetector.isCheckmate(game.getCurrentPlayerToMove().getColor())) {
                if (game.getCurrentPlayerToMove().getColor() == QBotColor) ++checkmateToNNNBot;
                else ++checkmateToOpponent;
            } else {
                if (game.getCurrentPlayerToMove().getColor() == QBotColor) ++stalemateToNNNBot;
                else ++stalemateToOpponent;
            }*/
            System.out.println("Games completed: " + (doneTasks.incrementAndGet()) + "/" + COUNT);
        }
    }
}
