package io.deeplay.qchess.nukebot.bot;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.BotFactory;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.nukebot.bot.NukeBotSettings.BaseAlgEnum;
import io.deeplay.qchess.nukebot.bot.NukeBotSettings.CommonEvaluationConstructorEnum;
import io.deeplay.qchess.nukebot.bot.NukeBotSettings.EvaluationEnum;
import io.deeplay.qchess.nukebot.bot.features.MTDFFactory;
import io.deeplay.qchess.nukebot.bot.features.TTFactory;
import io.deeplay.qchess.nukebot.bot.features.components.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.MinimaxAlfaBetaPruning;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.NegaScoutAlfaBetaPruning;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.NegamaxAlfaBetaPruning;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.PVSNullMove;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.PVSVerifiedNullMove;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
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

        final SearchFunc<?> deepSearch =
                switch (botSettings.baseAlg) {
                    case Minimax -> {
                        final var alg =
                                new MinimaxAlfaBetaPruning(
                                        null,
                                        null,
                                        0,
                                        gs,
                                        color,
                                        botSettings.commonEvaluation.commonEvaluationConstructor,
                                        botSettings.evaluation.evaluationFunc,
                                        botSettings.maxDepth);
                        final var algTT =
                                botSettings.useTT
                                        ? TTFactory.getAlgWithTT(new TranspositionTable(), alg)
                                        : alg;
                        final var algMTDF =
                                botSettings.useMTDFsIterativeDeepening
                                        ? MTDFFactory.getAlgWithMTDF(1, 0, algTT)
                                        : algTT;
                        final var completeAlg = algMTDF;
                        final var searchFunc =
                                botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg)
                                        : new LinearSearch<>(completeAlg);
                        yield searchFunc;
                    }
                    case Negamax -> {
                        final var alg =
                                new NegamaxAlfaBetaPruning(
                                        null,
                                        null,
                                        0,
                                        gs,
                                        color,
                                        botSettings.commonEvaluation.commonEvaluationConstructor,
                                        botSettings.evaluation.evaluationFunc,
                                        botSettings.maxDepth);
                        final var algTT =
                                botSettings.useTT
                                        ? TTFactory.getAlgWithTT(new TranspositionTable(), alg)
                                        : alg;
                        final var algMTDF =
                                botSettings.useMTDFsIterativeDeepening
                                        ? MTDFFactory.getAlgWithMTDF(1, 0, algTT)
                                        : algTT;
                        final var completeAlg = algMTDF;
                        final var searchFunc =
                                botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg)
                                        : new LinearSearch<>(completeAlg);
                        yield searchFunc;
                    }
                    case NegaScout -> {
                        final var alg =
                                new NegaScoutAlfaBetaPruning(
                                        null,
                                        null,
                                        0,
                                        gs,
                                        color,
                                        botSettings.commonEvaluation.commonEvaluationConstructor,
                                        botSettings.evaluation.evaluationFunc,
                                        botSettings.maxDepth);
                        final var algTT =
                                botSettings.useTT
                                        ? TTFactory.getAlgWithTT(new TranspositionTable(), alg)
                                        : alg;
                        final var algMTDF =
                                botSettings.useMTDFsIterativeDeepening
                                        ? MTDFFactory.getAlgWithMTDF(1, 0, algTT)
                                        : algTT;
                        final var completeAlg = algMTDF;
                        final var searchFunc =
                                botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg)
                                        : new LinearSearch<>(completeAlg);
                        yield searchFunc;
                    }
                    case NullMove -> {
                        final var alg =
                                new PVSNullMove(
                                        null,
                                        null,
                                        0,
                                        gs,
                                        color,
                                        botSettings.commonEvaluation.commonEvaluationConstructor,
                                        botSettings.evaluation.evaluationFunc,
                                        botSettings.maxDepth);
                        final var algTT =
                                botSettings.useTT
                                        ? TTFactory.getAlgWithTT(new TranspositionTable(), alg)
                                        : alg;
                        final var algMTDF =
                                botSettings.useMTDFsIterativeDeepening
                                        ? MTDFFactory.getAlgWithMTDF(1, 0, algTT)
                                        : algTT;
                        final var completeAlg = algMTDF;
                        final var searchFunc =
                                botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg)
                                        : new LinearSearch<>(completeAlg);
                        yield searchFunc;
                    }
                    case VerifiedNullMove -> {
                        final var alg =
                                new PVSVerifiedNullMove(
                                        null,
                                        null,
                                        0,
                                        gs,
                                        color,
                                        botSettings.commonEvaluation.commonEvaluationConstructor,
                                        botSettings.evaluation.evaluationFunc,
                                        botSettings.maxDepth);
                        final var algTT =
                                botSettings.useTT
                                        ? TTFactory.getAlgWithTT(new TranspositionTable(), alg)
                                        : alg;
                        final var algMTDF =
                                botSettings.useMTDFsIterativeDeepening
                                        ? MTDFFactory.getAlgWithMTDF(1, 0, algTT)
                                        : algTT;
                        final var completeAlg = algMTDF;
                        final var searchFunc =
                                botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg)
                                        : new LinearSearch<>(completeAlg);
                        yield searchFunc;
                    }
                };

        final NukeBot nukeBot = new NukeBot(gs, color, deepSearch);

        if (logger.isInfoEnabled()) {
            synchronized (logger) {
                logger.info("NukeBot успешно создан! Текущие настройки бота:");
                logger.info("JSON: {}", gson.toJson(botSettings));
                logger.info("Максимальная глубина: {}", botSettings.maxDepth);
                logger.info("Базовый алгоритм: {}", botSettings.baseAlg);
                logger.info("\t\tВарианты алгоритмов: {}", Arrays.toString(BaseAlgEnum.values()));
                logger.info("Функция оценки: {}", botSettings.evaluation);
                logger.info("\t\tВарианты функций: {}", Arrays.toString(EvaluationEnum.values()));
                logger.info("Параллельный поиск: {}", botSettings.parallelSearch);
                logger.info(
                        "Итеративное углубление с MTDF: {}",
                        botSettings.useMTDFsIterativeDeepening);
                logger.info("Таблицы транспозиции (кеширование): {}", botSettings.useTT);
                logger.info("Улучшение функции оценки: {}", botSettings.commonEvaluation);
                logger.info(
                        "\t\tВарианты улучшений: {}",
                        Arrays.toString(CommonEvaluationConstructorEnum.values()));
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
}
