package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.MatrixEvaluation;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.NegaScoutAlfaBetaPruning;
import io.deeplay.qchess.nnnbot.bot.searchfunc.parallelsearch.ParallelSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class NNNBotFactory {

    private static final Logger logger = LoggerFactory.getLogger(NNNBotFactory.class);

    private static String time;

    private static int lastBotId;

    /** Устанавливает время для записи логов */
    public static void setTime(String time) {
        NNNBotFactory.time = time;
    }

    public static synchronized NNNBot getNNNBot(GameSettings gs, Color color) {
        MDC.put("time", time);

        final int maxDepth = 2;
        gs.history.setMinBoardStateToSave(maxDepth);

        EvaluationFunc evaluationFunc = MatrixEvaluation::defenseHeuristics;
        ParallelSearch deepSearch =
                new NegaScoutAlfaBetaPruning(gs, color, evaluationFunc, maxDepth);

        NNNBot nnnBot = new NNNBot(gs, color, deepSearch);

        nnnBot.setId(++lastBotId);

        logger.info(
                "[NNNBotFactory] Создан бот #{} цвета {} с глубиной поиска {}",
                lastBotId,
                color,
                deepSearch.maxDepth);
        return nnnBot;
    }
}
