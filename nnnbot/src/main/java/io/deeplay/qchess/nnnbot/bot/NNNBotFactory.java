package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nnnbot.bot.evaluationfunc.FigurePositionsEvaluation;
import io.deeplay.qchess.nnnbot.bot.searchfunc.alfabetadeepsearch.AlfaBetaDeepSearch;
import io.deeplay.qchess.nnnbot.bot.searchfunc.alfabetadeepsearch.MinimaxAlfaBetaDeepSearch;
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

        final int maxDepth = 3;
        gs.history.setMinBoardStateToSave(maxDepth);

        AlfaBetaDeepSearch deepSearch = new MinimaxAlfaBetaDeepSearch(maxDepth);
        EvaluationFunc evaluationFunc = new FigurePositionsEvaluation();

        NNNBot nnnBot = new NNNBot(gs, color, deepSearch, evaluationFunc);

        nnnBot.setId(++lastBotId);

        logger.info("Создан бот #{} с глубиной поиска {}", lastBotId, deepSearch.maxDepth);
        return nnnBot;
    }
}
