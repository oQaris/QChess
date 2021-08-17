package io.deeplay.qchess.nukebot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.BotFactory;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.MatrixEvaluation;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.PestoEvaluation;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl.ParallelExecutorsSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class NukeBotFactory implements BotFactory {

    private static final Logger logger = LoggerFactory.getLogger(NukeBotFactory.class);

    private static String time;

    private static int lastBotId;

    /** Устанавливает время для записи логов */
    public static void setTime(final String time) {
        NukeBotFactory.time = time;
    }

    public static synchronized NukeBot getNukeBot(final GameSettings gs, final Color color) {
        MDC.put("time", time);
        ++lastBotId;

        final int maxDepth = 3;
        gs.history.setMinBoardStateToSave(maxDepth);

        final EvaluationFunc evaluationFunc = MatrixEvaluation::figurePositionHeuristics;
        final SearchFunc deepSearch =
                new ParallelExecutorsSearch(gs, color, evaluationFunc, maxDepth);

        final NukeBot nukeBot = new NukeBot(gs, color, deepSearch);

        nukeBot.setId(lastBotId);

        logger.info(
                "[NukeBotFactory] Создан бот #{} цвета {} с глубиной поиска {}",
                lastBotId,
                color,
                deepSearch.maxDepth);
        return nukeBot;
    }

    @Override
    public RemotePlayer newBot(final String name, final GameSettings gs, final Color myColor) {
        // todo учитывать name
        return getNukeBot(gs, myColor);
    }
}
