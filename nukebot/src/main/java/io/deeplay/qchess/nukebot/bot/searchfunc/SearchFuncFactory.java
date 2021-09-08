package io.deeplay.qchess.nukebot.bot.searchfunc;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.NukeBotSettings;
import io.deeplay.qchess.nukebot.bot.features.MTDFFactory;
import io.deeplay.qchess.nukebot.bot.features.TTFactory;
import io.deeplay.qchess.nukebot.bot.features.components.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.MinimaxAlfaBetaPruning;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.NegaScoutAlfaBetaPruning;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.NegamaxAlfaBetaPruning;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.PVSNullMove;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.PVSVerifiedNullMove;
import io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl.LinearSearch;
import io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl.ParallelExecutorsSearch;

public class SearchFuncFactory {

    public static SearchFunc<?> getSearchFunc(
            final GameSettings gs, final Color color, final NukeBotSettings botSettings) {
        return (SearchFunc<?>) getSearchFunc(null, null, 0, gs, color, botSettings, true);
    }

    public static SearchAlgorithm<?> getSearchFunc(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final NukeBotSettings botSettings,
            final boolean createFuncOrElseOnlyAlg) {
        return switch (botSettings.baseAlg) {
            case Minimax -> {
                final var alg =
                        new MinimaxAlfaBetaPruning(
                                resultUpdater,
                                mainMove,
                                moveVersion,
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
                        createFuncOrElseOnlyAlg
                                ? botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg, botSettings)
                                        : new LinearSearch<>(completeAlg)
                                : completeAlg;
                yield searchFunc;
            }
            case Negamax -> {
                final var alg =
                        new NegamaxAlfaBetaPruning(
                                resultUpdater,
                                mainMove,
                                moveVersion,
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
                        createFuncOrElseOnlyAlg
                                ? botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg, botSettings)
                                        : new LinearSearch<>(completeAlg)
                                : completeAlg;
                yield searchFunc;
            }
            case NegaScout -> {
                final var alg =
                        new NegaScoutAlfaBetaPruning(
                                resultUpdater,
                                mainMove,
                                moveVersion,
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
                        createFuncOrElseOnlyAlg
                                ? botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg, botSettings)
                                        : new LinearSearch<>(completeAlg)
                                : completeAlg;
                yield searchFunc;
            }
            case NullMove -> {
                final var alg =
                        new PVSNullMove(
                                resultUpdater,
                                mainMove,
                                moveVersion,
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
                        createFuncOrElseOnlyAlg
                                ? botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg, botSettings)
                                        : new LinearSearch<>(completeAlg)
                                : completeAlg;
                yield searchFunc;
            }
            case VerifiedNullMove -> {
                final var alg =
                        new PVSVerifiedNullMove(
                                resultUpdater,
                                mainMove,
                                moveVersion,
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
                        createFuncOrElseOnlyAlg
                                ? botSettings.parallelSearch
                                        ? new ParallelExecutorsSearch<>(completeAlg, botSettings)
                                        : new LinearSearch<>(completeAlg)
                                : completeAlg;
                yield searchFunc;
            }
        };
    }
}
