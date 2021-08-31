package io.deeplay.qchess.nukebot.bot.features;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgErrorCode;
import io.deeplay.qchess.nukebot.bot.exceptions.SearchAlgException;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaNullMoveAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaVerifiedNullMoveAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.PositiveAlfaNegaBeta;
import io.deeplay.qchess.nukebot.bot.searchalg.SearchAlgorithm;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;

/** Добавляет алгоритму возможность итеративного поиска */
public abstract class MTDFFactory {

    public static PositiveAlfaNegaBetaMTDF getAlgWithMTDF(
            final int startDepth, final int firstGuess, final PositiveAlfaNegaBeta alg) {
        return new PositiveAlfaNegaBetaMTDF(alg, startDepth, firstGuess);
    }

    public static NegaAlfaBetaMTDF getAlgWithMTDF(
            final int startDepth, final int firstGuess, final NegaAlfaBeta alg) {
        return new NegaAlfaBetaMTDF(alg, startDepth, firstGuess);
    }

    public static NegaNullMoveAlfaBetaMTDF getAlgWithMTDF(
            final int startDepth, final int firstGuess, final NegaNullMoveAlfaBeta alg) {
        return new NegaNullMoveAlfaBetaMTDF(alg, startDepth, firstGuess);
    }

    public static NegaVerifiedNullMoveAlfaBetaMTDF getAlgWithMTDF(
            final int startDepth, final int firstGuess, final NegaVerifiedNullMoveAlfaBeta alg) {
        return new NegaVerifiedNullMoveAlfaBetaMTDF(alg, startDepth, firstGuess);
    }

    // ----------------------------------------------------- //
    // Реализация общих методов, необходимых для работы MTDF //

    private static void updateResult(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int estimation,
            final int startDepth,
            final int moveVersion) {
        resultUpdater.updateResult(mainMove, estimation, startDepth, moveVersion);
    }

    /** Общий метод запуска алгоритмов поиска */
    private static <T extends SearchAlgorithm<?> & MTDFCompatible> void run(
            final T alg,
            final ResultUpdater resultUpdater,
            final int moveVersion,
            final Move mainMove,
            int startDepth,
            final int maxDepth,
            int firstGuess) {
        try {
            alg.makeMove(mainMove);
            for (; startDepth <= maxDepth; ++startDepth) {
                firstGuess = MTDF(alg, firstGuess, startDepth);
                if (alg.isInvalidMoveVersion()) break;
                updateResult(resultUpdater, mainMove, firstGuess, startDepth, moveVersion);
            }
            alg.undoMove();
        } catch (final ChessError e) {
            throw new SearchAlgException(SearchAlgErrorCode.SEARCH_ALG, e);
        }
    }

    /** Проводит поиск с нулевым окном, основываясь на первом предположении firstGuess */
    private static <T extends SearchAlgorithm<?> & MTDFCompatible> int MTDF(
            final T alg, final int firstGuess, final int depth) throws ChessError {
        int est = firstGuess;
        int lowerBound = EvaluationFunc.MIN_ESTIMATION;
        int upperBound = EvaluationFunc.MAX_ESTIMATION;
        int beta;
        do {
            if (est == lowerBound) beta = est + 1;
            else beta = est;

            est = alg.alfaBetaWithTT(beta - 1, beta, depth);

            if (alg.isInvalidMoveVersion()) return est;

            if (est < beta) upperBound = est;
            else lowerBound = est;
        } while (lowerBound < upperBound);
        return est;
    }

    /** Определяет стандартные методы, необходимые для минимальной работы MTDF */
    public interface MTDFCompatible {
        /**
         * Для эффективной работы алгоритма требуется алгоритм поиска с альфа-бета отсечениями и
         * таблицами транспозиции, но последним можно пренебречь
         */
        int alfaBetaWithTT(final int alfa, final int beta, final int depth) throws ChessError;
    }

    public static final class PositiveAlfaNegaBetaMTDF extends PositiveAlfaNegaBeta
            implements MTDFCompatible {

        private final PositiveAlfaNegaBeta alg;
        private final int startDepth;
        private final int firstGuess;

        private PositiveAlfaNegaBetaMTDF(
                final PositiveAlfaNegaBeta alg, final int startDepth, final int firstGuess) {
            super(alg);
            this.alg = alg;
            this.startDepth = startDepth;
            this.firstGuess = firstGuess;
        }

        @Override
        public void setSettings(
                final Move mainMove,
                final GameSettings gs,
                final int maxDepth,
                final int moveVersion) {
            this.mainMove = mainMove;
            this.gs = gs;
            this.maxDepth = maxDepth;
            this.moveVersion = moveVersion;
            alg.setSettings(mainMove, gs, maxDepth, moveVersion);
        }

        @Override
        public void run() {
            MTDFFactory.run(
                    this, resultUpdater, moveVersion, mainMove, startDepth, maxDepth, firstGuess);
        }

        @Override
        public void updateResult(final int estimation) {
            MTDFFactory.updateResult(resultUpdater, mainMove, estimation, startDepth, moveVersion);
        }

        @Override
        public int alfaBetaWithTT(final int alfa, final int beta, final int depth)
                throws ChessError {
            return alg.positiveAlfaNegaBeta(false, alfa, beta, depth);
        }
    }

    // ------------------------------------------------------------------------------------------//
    // Конкретные реализации всех функций, совместимых с MTDF, для определенных типов алгоритмов //

    public static final class NegaAlfaBetaMTDF extends NegaAlfaBeta implements MTDFCompatible {

        private final NegaAlfaBeta alg;
        private final int startDepth;
        private final int firstGuess;

        private NegaAlfaBetaMTDF(
                final NegaAlfaBeta alg, final int startDepth, final int firstGuess) {
            super(alg);
            this.alg = alg;
            this.startDepth = startDepth;
            this.firstGuess = firstGuess;
        }

        @Override
        public void run() {
            MTDFFactory.run(
                    this, resultUpdater, moveVersion, mainMove, startDepth, maxDepth, firstGuess);
        }

        @Override
        public void updateResult(final int estimation) {
            MTDFFactory.updateResult(resultUpdater, mainMove, estimation, startDepth, moveVersion);
        }

        @Override
        public int alfaBetaWithTT(final int alfa, final int beta, final int depth)
                throws ChessError {
            return -alg.negaSearch(false, -beta, -alfa, depth);
        }
    }

    public static final class NegaNullMoveAlfaBetaMTDF extends NegaNullMoveAlfaBeta
            implements MTDFCompatible {

        private final NegaNullMoveAlfaBeta alg;
        private final int startDepth;
        private final int firstGuess;

        private NegaNullMoveAlfaBetaMTDF(
                final NegaNullMoveAlfaBeta alg, final int startDepth, final int firstGuess) {
            super(alg);
            this.alg = alg;
            this.startDepth = startDepth;
            this.firstGuess = firstGuess;
        }

        @Override
        public void run() {
            MTDFFactory.run(
                    this, resultUpdater, moveVersion, mainMove, startDepth, maxDepth, firstGuess);
        }

        @Override
        public void updateResult(final int estimation) {
            MTDFFactory.updateResult(resultUpdater, mainMove, estimation, startDepth, moveVersion);
        }

        @Override
        public int alfaBetaWithTT(final int alfa, final int beta, final int depth)
                throws ChessError {
            return -alg.negaNullMoveSearch(false, -beta, -alfa, depth, false);
        }
    }

    public static final class NegaVerifiedNullMoveAlfaBetaMTDF extends NegaVerifiedNullMoveAlfaBeta
            implements MTDFCompatible {

        private final NegaVerifiedNullMoveAlfaBeta alg;
        private final int startDepth;
        private final int firstGuess;

        private NegaVerifiedNullMoveAlfaBetaMTDF(
                final NegaVerifiedNullMoveAlfaBeta alg,
                final int startDepth,
                final int firstGuess) {
            super(alg);
            this.alg = alg;
            this.startDepth = startDepth;
            this.firstGuess = firstGuess;
        }

        @Override
        public void run() {
            MTDFFactory.run(
                    this, resultUpdater, moveVersion, mainMove, startDepth, maxDepth, firstGuess);
        }

        @Override
        public void updateResult(final int estimation) {
            MTDFFactory.updateResult(resultUpdater, mainMove, estimation, startDepth, moveVersion);
        }

        @Override
        public int alfaBetaWithTT(final int alfa, final int beta, final int depth)
                throws ChessError {
            return -alg.negaVNMSearch(false, -beta, -alfa, depth, true, false);
        }
    }
}
