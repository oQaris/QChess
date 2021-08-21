package io.deeplay.qchess.nukebot.bot.searchalg;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.searchalg.features.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import java.util.List;

public abstract class SearchAlgorithm implements Runnable {

    public final EvaluationFunc evaluationFunc;
    public final Color myColor;
    public final Color enemyColor;
    public final int maxDepth;

    protected final ResultUpdater resultUpdater;
    protected final Move mainMove;
    protected final int moveVersion;
    protected final GameSettings gs;

    protected SearchAlgorithm(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color color,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        this.resultUpdater = resultUpdater;
        this.mainMove = mainMove;
        this.moveVersion = moveVersion;
        this.gs = gs;
        this.evaluationFunc = evaluationFunc;
        myColor = color;
        enemyColor = color.inverse();
        this.maxDepth = maxDepth;
    }

    protected boolean isTerminalNode(final List<Move> allMoves) {
        return allMoves.isEmpty() || gs.endGameDetector.isDraw();
    }

    public int getEvaluation(final List<Move> allMoves, final boolean isMyMove, final int depth)
            throws ChessError {
        return getEvaluation(
                gs.endGameDetector.isCheck(myColor),
                gs.endGameDetector.isCheck(enemyColor),
                allMoves,
                true,
                isMyMove,
                depth,
                null);
    }

    /**
     * @param probablyAllMoves возможно все ходы
     * @param areExactAllMoves true, если probablyAllMoves - точно все ходы, иначе
     *     probablyAttackMoves - возможно не все ходы
     * @param isMyMove true, если сейчас ход максимизирующего игрока
     * @param depth текущая глубина (без вычитания 1)
     */
    public int getEvaluation(
            final boolean isCheckToMe,
            final boolean isCheckToEnemy,
            final List<Move> probablyAllMoves,
            final boolean areExactAllMoves,
            final boolean isMyMove,
            final int depth,
            final TranspositionTable table)
            throws ChessError {
        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        final int checkBonus;
        if (isCheckToMe) checkBonus = -EvaluationFunc.KING_COST;
        else if (isCheckToEnemy) checkBonus = EvaluationFunc.KING_COST;
        else checkBonus = 0;

        if (isMyMove) { // probablyAllMoves are mine
            boolean isStalemateToMe = probablyAllMoves.isEmpty();
            // Если поставлен пат, но не факт, что мы посмотрели все ходы, нужно пересчитать:
            if (isStalemateToMe && !areExactAllMoves) {
                isStalemateToMe = gs.endGameDetector.isStalemate(myColor, table);
            }
            if (isStalemateToMe) {
                if (isCheckToMe) return EvaluationFunc.MIN_ESTIMATION + 1000 - depth;
                return checkBonus - depth;
            }

            if (gs.endGameDetector.isStalemate(enemyColor, table)) {
                if (isCheckToEnemy) return EvaluationFunc.MAX_ESTIMATION - 1000 + depth;
                return checkBonus - depth; // расширяем ничью - чем глубже, тем лучше
            }
        } else { // probablyAllMoves are enemy's
            boolean isStalemateToEnemy = probablyAllMoves.isEmpty();
            // Если поставлен пат, но не факт, что мы посмотрели все ходы, нужно пересчитать:
            if (isStalemateToEnemy && !areExactAllMoves) {
                isStalemateToEnemy = gs.endGameDetector.isStalemate(enemyColor, table);
            }
            if (isStalemateToEnemy) {
                if (isCheckToEnemy) return EvaluationFunc.MAX_ESTIMATION - 1000 + depth;
                return checkBonus - depth; // расширяем ничью - чем глубже, тем лучше
            }

            if (gs.endGameDetector.isStalemate(myColor, table)) {
                if (isCheckToMe) return EvaluationFunc.MIN_ESTIMATION + 1000 - depth;
                return checkBonus - depth;
            }
        }

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        // Проверка на ничью должна быть после проверок на пат и мат
        if (gs.endGameDetector.isDraw())
            return checkBonus - depth; // расширяем ничью - чем глубже, тем лучше

        if (resultUpdater.isInvalidMoveVersion(moveVersion)) return EvaluationFunc.MIN_ESTIMATION;

        return evaluationFunc.getHeuristics(gs, myColor) + checkBonus;
    }

    @FunctionalInterface
    public interface SearchAlgConstructor {
        SearchAlgorithm newInstance(
                ResultUpdater resultUpdater,
                Move mainMove,
                int moveVersion,
                GameSettings gs,
                Color color,
                EvaluationFunc evaluationFunc,
                int maxDepth);
    }
}
