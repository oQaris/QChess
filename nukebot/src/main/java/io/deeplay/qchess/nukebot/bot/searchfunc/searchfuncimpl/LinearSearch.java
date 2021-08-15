package io.deeplay.qchess.nukebot.bot.searchfunc.searchfuncimpl;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithmFactory;
import io.deeplay.qchess.nukebot.bot.searchalg.features.SearchImprovements;
import io.deeplay.qchess.nukebot.bot.searchalg.searchalgimpl.mtdfcompatible.MTDFSearch;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.List;

/** Простой линейный поиск без параллельных вычислений */
public class LinearSearch extends SearchFunc implements ResultUpdater {

    /** Лучшая оценка для текущего цвета myColor */
    private int theBestEstimation;
    /** Лучший ход для текущего цвета myColor */
    private Move theBestMove;
    /** Лучшая глубина для текущего цвета myColor, на которой была основана оценка */
    private int theBestMaxDepth;

    public LinearSearch(
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(gs, color, evaluationFunc, maxDepth);
    }

    @Override
    public Move findBest() throws ChessError {
        final List<Move> allMoves = gs.board.getAllPreparedMoves(gs, myColor);
        SearchImprovements.prioritySort(allMoves);

        theBestEstimation = EvaluationFunc.MIN_ESTIMATION;

        for (final Move move : allMoves) {
            final MTDFSearch searchAlgorithm =
                    SearchAlgorithmFactory.getMTDFCompatibleAlgorithm(
                            this, move, 0, gs, myColor, evaluationFunc, maxDepth);

            // searchAlgorithm.MTDFStart(0, maxDepth, TIME_TO_MOVE);
            searchAlgorithm.run();
        }

        return theBestMove;
    }

    @Override
    public void updateResult(
            final Move move, final int estimation, final int maxDepth, final int moveVersion) {
        if (maxDepth > theBestMaxDepth || estimation > theBestEstimation) {
            theBestEstimation = estimation;
            theBestMove = move;
            theBestMaxDepth = maxDepth;
        }
    }

    @Override
    public boolean isInvalidMoveVersion(final int myMoveVersion) {
        return false;
    }
}
