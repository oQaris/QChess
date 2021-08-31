package io.deeplay.qchess.nukebot.bot.searchalg;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.CommonEvaluationConstructor;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;

/** Описывает сигнатуры и контракты методов для базы алгоритмов */
public interface AlgBase extends Runnable {

    /** Алгоритм, основанный на обычном минимаксе с альфа-бета отсечениями */
    abstract class PositiveAlfaNegaBeta extends SearchAlgorithm<PositiveAlfaNegaBeta> {

        protected PositiveAlfaNegaBeta(
                final ResultUpdater resultUpdater,
                final Move mainMove,
                final int moveVersion,
                final GameSettings gs,
                final Color myColor,
                final CommonEvaluationConstructor commonEvaluationConstructor,
                final EvaluationFunc evaluationFunc,
                final int maxDepth) {
            super(
                    resultUpdater,
                    mainMove,
                    moveVersion,
                    gs,
                    myColor,
                    commonEvaluationConstructor,
                    evaluationFunc,
                    maxDepth);
            updateLastWrapper(this);
        }

        protected PositiveAlfaNegaBeta(final PositiveAlfaNegaBeta alg) {
            super(alg);
            alg.updateLastWrapper(this);
        }

        /**
         * @param isMyMove true, если это максимизирующий игрок, false - минимизирующий
         * @param alfa лучшая оценка из гарантированных для максимизирующего игрока
         * @param beta лучшая оценка из гарантированных для минимизирующего игрока
         * @param depth текущая глубина
         * @return лучшая оценка из гарантированных для максимизирующего игрока
         */
        public int positiveAlfaNegaBeta(
                final boolean isMyMove, final int alfa, final int beta, final int depth)
                throws ChessError {
            return getLastWrapper().positiveAlfaNegaBeta(isMyMove, alfa, beta, depth);
        }
    }

    /** Алгоритм, основанный на свойстве игр с нулевой суммой, где alfa == -beta */
    abstract class NegaAlfaBeta extends SearchAlgorithm<NegaAlfaBeta> {

        protected NegaAlfaBeta(
                final ResultUpdater resultUpdater,
                final Move mainMove,
                final int moveVersion,
                final GameSettings gs,
                final Color myColor,
                final CommonEvaluationConstructor commonEvaluationConstructor,
                final EvaluationFunc evaluationFunc,
                final int maxDepth) {
            super(
                    resultUpdater,
                    mainMove,
                    moveVersion,
                    gs,
                    myColor,
                    commonEvaluationConstructor,
                    evaluationFunc,
                    maxDepth);
            updateLastWrapper(this);
        }

        protected NegaAlfaBeta(final NegaAlfaBeta alg) {
            super(alg);
            alg.updateLastWrapper(this);
        }

        /**
         * @param isMyMove true, если это максимизирующий игрок, false - минимизирующий
         * @param alfa лучшая оценка из гарантированных для текущего игрока
         * @param beta лучшая оценка из гарантированных для противника
         * @param depth текущая глубина
         * @return лучшая оценка из гарантированных для текущего игрока
         */
        public int negaSearch(
                final boolean isMyMove, final int alfa, final int beta, final int depth)
                throws ChessError {
            return getLastWrapper().negaSearch(isMyMove, alfa, beta, depth);
        }
    }

    /**
     * Алгоритм, основанный на свойстве игр с нулевой суммой, где alfa == -beta. А также реализует
     * нулевой ход
     */
    abstract class NegaNullMoveAlfaBeta extends SearchAlgorithm<NegaNullMoveAlfaBeta> {

        protected static final int DEPTH_REDUCTION = 2;

        protected NegaNullMoveAlfaBeta(
                final ResultUpdater resultUpdater,
                final Move mainMove,
                final int moveVersion,
                final GameSettings gs,
                final Color myColor,
                final CommonEvaluationConstructor commonEvaluationConstructor,
                final EvaluationFunc evaluationFunc,
                final int maxDepth) {
            super(
                    resultUpdater,
                    mainMove,
                    moveVersion,
                    gs,
                    myColor,
                    commonEvaluationConstructor,
                    evaluationFunc,
                    maxDepth);
            updateLastWrapper(this);
        }

        protected NegaNullMoveAlfaBeta(final NegaNullMoveAlfaBeta alg) {
            super(alg);
            alg.updateLastWrapper(this);
        }

        /**
         * @param isMyMove true, если это максимизирующий игрок, false - минимизирующий
         * @param alfa лучшая оценка из гарантированных для текущего игрока
         * @param beta лучшая оценка из гарантированных для противника
         * @param depth текущая глубина
         * @param isPrevNullMove true, если предыдущий ход был нулевым
         * @return лучшая оценка из гарантированных для текущего игрока
         */
        public int negaNullMoveSearch(
                final boolean isMyMove,
                final int alfa,
                final int beta,
                final int depth,
                final boolean isPrevNullMove)
                throws ChessError {
            return getLastWrapper().negaNullMoveSearch(isMyMove, alfa, beta, depth, isPrevNullMove);
        }

        protected boolean isAllowNullMove(final Color color, final boolean isPrevNullMove) {
            return !isPrevNullMove
                    && !isStalemate(color.inverse())
                    && !isCheck(color)
                    && !isCheck(color.inverse())
                    && gs.board.getFigureCount(color.inverse()) > 9;
            /*
             * TODO: (улучшить) null-move запрещен, если выполнено одно из следующих условий:
             *  1. Противник имеет только короля и пешки
             *  2. У противника осталось мало материала
             *  3. Осталось мало материала на доске
             *  4. Число ходов превышает.
             */
        }
    }

    /**
     * Алгоритм, основанный на свойстве игр с нулевой суммой, где alfa == -beta. А также реализует
     * проверенный нулевой ход
     */
    abstract class NegaVerifiedNullMoveAlfaBeta
            extends SearchAlgorithm<NegaVerifiedNullMoveAlfaBeta> {

        protected static final int DEPTH_REDUCTION = 2;

        protected NegaVerifiedNullMoveAlfaBeta(
                final ResultUpdater resultUpdater,
                final Move mainMove,
                final int moveVersion,
                final GameSettings gs,
                final Color myColor,
                final CommonEvaluationConstructor commonEvaluationConstructor,
                final EvaluationFunc evaluationFunc,
                final int maxDepth) {
            super(
                    resultUpdater,
                    mainMove,
                    moveVersion,
                    gs,
                    myColor,
                    commonEvaluationConstructor,
                    evaluationFunc,
                    maxDepth);
            updateLastWrapper(this);
        }

        protected NegaVerifiedNullMoveAlfaBeta(final NegaVerifiedNullMoveAlfaBeta alg) {
            super(alg);
            alg.updateLastWrapper(this);
        }

        /**
         * @param isMyMove true, если это максимизирующий игрок, false - минимизирующий
         * @param alfa лучшая оценка из гарантированных для текущего игрока
         * @param beta лучшая оценка из гарантированных для противника
         * @param depth текущая глубина
         * @param verify true, если мы не в позиции Цугцванга (скорее всего)
         * @param isPrevNullMove true, если предыдущий ход был нулевым
         * @return лучшая оценка из гарантированных для текущего игрока
         */
        public int negaVNMSearch(
                final boolean isMyMove,
                final int alfa,
                final int beta,
                final int depth,
                final boolean verify,
                final boolean isPrevNullMove)
                throws ChessError {
            return getLastWrapper()
                    .negaVNMSearch(isMyMove, alfa, beta, depth, verify, isPrevNullMove);
        }

        protected boolean isAllowNullMove(
                final Color color,
                final boolean isPrevNullMove,
                final boolean verify,
                final int depth) {
            return !isPrevNullMove
                    && (!verify || depth > 1)
                    && !isStalemate(color.inverse())
                    && !isCheck(color)
                    && !isCheck(color.inverse())
                    && gs.board.getFigureCount(color.inverse()) > 9;
            /*
             * TODO: (улучшить) null-move запрещен, если выполнено одно из следующих условий:
             *  1. Противник имеет только короля и пешки
             *  2. У противника осталось мало материала
             *  3. Осталось мало материала на доске
             *  4. Число ходов превышает.
             */
        }
    }
}
