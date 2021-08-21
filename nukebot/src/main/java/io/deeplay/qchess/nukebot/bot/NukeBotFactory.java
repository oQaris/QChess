package io.deeplay.qchess.nukebot.bot;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.BotFactory;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nukebot.bot.NukeBotFactory.NukeBotSettings.EvaluationEnum;
import io.deeplay.qchess.nukebot.bot.NukeBotFactory.NukeBotSettings.SearchEnum;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.MatrixEvaluation;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.PestoEvaluation;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc.SearchFuncConstructor;
import io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl.LinearSearch;
import io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl.ParallelExecutorsSearch;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NukeBotFactory implements BotFactory {

    private static final transient Logger logger = LoggerFactory.getLogger(NukeBotFactory.class);
    private static final transient Gson gson = new Gson();

    public static NukeBot getNukeBot(final GameSettings gs, final Color color) {
        return getNukeBot(gs, color, new NukeBotSettings());
    }

    public static NukeBot getNukeBot(
            final GameSettings gs, final Color color, final NukeBotSettings botSettings) {
        gs.history.setMinBoardStateToSave(botSettings.maxDepth);

        final SearchFunc deepSearch =
                botSettings.search.searchFunc.newInstance(
                        gs, color, botSettings.evaluation.evaluationFunc, botSettings.maxDepth);

        final NukeBot nukeBot = new NukeBot(gs, color, deepSearch);

        if (logger.isInfoEnabled()) {
            synchronized (logger) {
                logger.info("NukeBot успешно создан! Текущие настройки бота:");
                logger.info("JSON: {}", gson.toJson(botSettings));
                logger.info("Максимальная глубина: {}", botSettings.maxDepth);
                logger.info("Функция оценки: {}", botSettings.evaluation);
                logger.info("\t\tВарианты функций: {}", Arrays.toString(EvaluationEnum.values()));
                logger.info("Поиск: {}", botSettings.search);
                logger.info("\t\tВарианты поиска: {}", Arrays.toString(SearchEnum.values()));
            }
        }

        return nukeBot;
    }

    @Override
    public RemotePlayer newBot(
            final String settingsJson, final GameSettings gs, final Color myColor) {
        NukeBotSettings botSettings;
        try {
            botSettings = gson.fromJson(settingsJson, NukeBotSettings.class);
        } catch (final JsonSyntaxException e) {
            logger.warn("Ошибка при парсинге настроек NukeBot! Установка стандартных настроек...");
            botSettings = new NukeBotSettings();
        }
        return getNukeBot(gs, myColor, botSettings);
    }

    public static class NukeBotSettings {

        private final int maxDepth = 3;
        private final EvaluationEnum evaluation = EvaluationEnum.Pesto;
        private final SearchEnum search = SearchEnum.Parallel;

        public static String getStandardSettings() {
            return gson.toJson(new NukeBotSettings());
        }

        public enum EvaluationEnum {
            Pesto(PestoEvaluation::pestoHeuristic),
            Position(MatrixEvaluation::figurePositionHeuristics),
            Attack(MatrixEvaluation::figureAttackHeuristics),
            Ultimate(MatrixEvaluation::ultimateHeuristics);

            public final EvaluationFunc evaluationFunc;

            EvaluationEnum(final EvaluationFunc evaluationFunc) {
                this.evaluationFunc = evaluationFunc;
            }
        }

        public enum SearchEnum {
            Linear(LinearSearch::new),
            Parallel(ParallelExecutorsSearch::new);

            public final SearchFuncConstructor searchFunc;

            SearchEnum(final SearchFuncConstructor searchFunc) {
                this.searchFunc = searchFunc;
            }
        }
    }
}
