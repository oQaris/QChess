package io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval;

import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;

@FunctionalInterface
public interface CommonEvaluationConstructor {
    CommonEvaluation newInstance(SearchAlgorithm<?> alg, EvaluationFunc evaluationFunc);
}
