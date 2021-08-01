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

    private static int lastId;

    /** Устанавливает время для записи логов */
    public static void setTime(String time) {
        MDC.put("time", time);
    }

    public static synchronized NNNBot getNNNBot(GameSettings gs, Color color) {
        final int maxDepth = 3;
        gs.history.setMinBoardStateToSave(maxDepth);

        AlfaBetaDeepSearch deepSearch = new MinimaxAlfaBetaDeepSearch(maxDepth);
        EvaluationFunc evaluationFunc = new FigurePositionsEvaluation();

        NNNBot nnnBot = new NNNBot(gs, color, deepSearch, evaluationFunc);

        nnnBot.setId(++lastId);

        logger.info("Создан бот #{} с глубиной поиска {}", lastId, deepSearch.maxDepth);
        return nnnBot;
    }
}
