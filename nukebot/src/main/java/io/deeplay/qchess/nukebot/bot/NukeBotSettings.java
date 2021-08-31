package io.deeplay.qchess.nukebot.bot;

import com.google.gson.Gson;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.MatrixEvaluation;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.PestoEvaluation;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.CommonEvaluationConstructor;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.QuiescenceEval;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.SimpleEval;

public class NukeBotSettings {

    private static final transient Gson gson = new Gson();

    public final int maxDepth = 3;
    public final BaseAlgEnum baseAlg = BaseAlgEnum.Minimax;
    public final EvaluationEnum evaluation = EvaluationEnum.Pesto;
    public final boolean parallelSearch = true;
    public final boolean useMTDFsIterativeDeepening = false; // TODO: не работает
    public final boolean useTT = true;
    public final CommonEvaluationConstructorEnum commonEvaluation =
            CommonEvaluationConstructorEnum.Simple;

    public static String getStandardSettings() {
        return gson.toJson(new NukeBotSettings());
    }

    public enum BaseAlgEnum {
        Minimax,
        Negamax,
        NegaScout,
        NullMove,
        VerifiedNullMove
    }

    public enum CommonEvaluationConstructorEnum {
        Simple(SimpleEval::new),
        Quiesce(QuiescenceEval::new);

        public final CommonEvaluationConstructor commonEvaluationConstructor;

        CommonEvaluationConstructorEnum(
                final CommonEvaluationConstructor commonEvaluationConstructor) {
            this.commonEvaluationConstructor = commonEvaluationConstructor;
        }
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
}
