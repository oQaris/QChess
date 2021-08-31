package io.deeplay.qchess.nukebot.bot.features;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.features.components.TranspositionTable;
import io.deeplay.qchess.nukebot.bot.features.components.TranspositionTable.TTEntry;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaNullMoveAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.NegaVerifiedNullMoveAlfaBeta;
import io.deeplay.qchess.nukebot.bot.searchalg.AlgBase.PositiveAlfaNegaBeta;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.List;

/** Добавляет алгоритму возможность кеширования в таблицах транспозиции */
public abstract class TTFactory {

    public static <T extends PositiveAlfaNegaBeta> PositiveAlfaNegaBetaTT getAlgWithTT(
            final TranspositionTable table, final T alg) {
        return new PositiveAlfaNegaBetaTT(table, alg);
    }

    public static <T extends NegaAlfaBeta> NegaAlfaBetaTT getAlgWithTT(
            final TranspositionTable table, final T alg) {
        return new NegaAlfaBetaTT(table, alg);
    }

    public static <T extends NegaNullMoveAlfaBeta> NegaNullMoveAlfaBetaTT getAlgWithTT(
            final TranspositionTable table, final T alg) {
        return new NegaNullMoveAlfaBetaTT(table, alg);
    }

    public static <T extends NegaVerifiedNullMoveAlfaBeta>
            NegaVerifiedNullMoveAlfaBetaTT getAlgWithTT(
                    final TranspositionTable table, final T alg) {
        return new NegaVerifiedNullMoveAlfaBetaTT(table, alg);
    }

    // ----------------------------------------------//
    // Реализация методов, необходимых для работы ТТ //

    private static boolean isCheck(
            final GameSettings gs, final TranspositionTable table, final Color color) {
        return table.isCheckTo(gs, gs.history.getLastBoardState(), color);
    }

    private static boolean isStalemate(
            final GameSettings gs, final TranspositionTable table, final Color color) {
        return gs.endGameDetector.isStalemate(color, table);
    }

    private static List<Move> getLegalMoves(
            final GameSettings gs, final TranspositionTable table, final Color color)
            throws ChessError {
        final BoardState boardState = gs.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);

        final List<Move> allMoves;
        if (entry != null) {
            if (entry.allMoves != null) allMoves = entry.allMoves;
            else // если есть вхождение, значит, скорее всего, есть и следующие
            allMoves = gs.board.getAllPreparedMoves(gs, color, table);
        } else allMoves = gs.board.getAllPreparedMoves(gs, color);

        table.storeAllMoves(boardState, allMoves);

        return allMoves;
    }

    // ------------------------------------------------------------------------//
    // Конкретные реализации всех функций ТТ для определенных типов алгоритмов //

    public static class PositiveAlfaNegaBetaTT extends PositiveAlfaNegaBeta {

        private final TranspositionTable table;
        private final PositiveAlfaNegaBeta alg;

        protected PositiveAlfaNegaBetaTT(
                final TranspositionTable table, final PositiveAlfaNegaBeta alg) {
            super(alg);
            this.table = table;
            this.alg = alg;
        }

        @Override
        public void setSettings(
                final Move mainMove,
                final GameSettings gs,
                final int maxDepth,
                final int moveVersion,
                final SearchFunc<?> searchFunc) {
            super.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
            alg.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
        }

        @Override
        public boolean isCheck(final Color color) {
            return TTFactory.isCheck(gs, table, color);
        }

        @Override
        public boolean isStalemate(final Color color) {
            return TTFactory.isStalemate(gs, table, color);
        }

        @Override
        public List<Move> getLegalMoves(final Color color) throws ChessError {
            return TTFactory.getLegalMoves(gs, table, color);
        }

        @Override
        public int positiveAlfaNegaBeta(final boolean isMyMove, int alfa, int beta, final int depth)
                throws ChessError {
            final BoardState boardState = gs.history.getLastBoardState();
            final TTEntry entry = table.find(boardState);
            if (entry != null && entry.depth >= depth) {
                if (entry.lowerBound >= beta) return entry.lowerBound;
                if (entry.upperBound <= alfa) return entry.upperBound;
                if (entry.lowerBound > alfa) alfa = entry.lowerBound;
                if (entry.upperBound < beta) beta = entry.upperBound;
            }

            final int est = alg.positiveAlfaNegaBeta(isMyMove, alfa, beta, depth);

            table.storeEstimation(boardState, est, alfa, beta, depth);

            return est;
        }

        @Override
        public void run() {
            alg.run();
        }
    }

    public static class NegaAlfaBetaTT extends NegaAlfaBeta {

        private final TranspositionTable table;
        private final NegaAlfaBeta alg;

        protected NegaAlfaBetaTT(final TranspositionTable table, final NegaAlfaBeta alg) {
            super(alg);
            this.table = table;
            this.alg = alg;
        }

        @Override
        public void setSettings(
                final Move mainMove,
                final GameSettings gs,
                final int maxDepth,
                final int moveVersion,
                final SearchFunc<?> searchFunc) {
            super.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
            alg.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
        }

        @Override
        public boolean isCheck(final Color color) {
            return TTFactory.isCheck(gs, table, color);
        }

        @Override
        public boolean isStalemate(final Color color) {
            return TTFactory.isStalemate(gs, table, color);
        }

        @Override
        public List<Move> getLegalMoves(final Color color) throws ChessError {
            return TTFactory.getLegalMoves(gs, table, color);
        }

        @Override
        public int negaSearch(final boolean isMyMove, int alfa, int beta, final int depth)
                throws ChessError {
            final BoardState boardState = gs.history.getLastBoardState();
            final TTEntry entry = table.find(boardState);
            if (entry != null && entry.depth >= depth) {
                if (entry.lowerBound >= beta) return entry.lowerBound;
                if (entry.upperBound <= alfa) return entry.upperBound;
                if (entry.lowerBound > alfa) alfa = entry.lowerBound;
                if (entry.upperBound < beta) beta = entry.upperBound;
            }

            final int est = alg.negaSearch(isMyMove, alfa, beta, depth);

            table.storeEstimation(boardState, est, alfa, beta, depth);

            return est;
        }

        @Override
        public void run() {
            alg.run();
        }
    }

    public static class NegaNullMoveAlfaBetaTT extends NegaNullMoveAlfaBeta {

        private final TranspositionTable table;
        private final NegaNullMoveAlfaBeta alg;

        protected NegaNullMoveAlfaBetaTT(
                final TranspositionTable table, final NegaNullMoveAlfaBeta alg) {
            super(alg);
            this.table = table;
            this.alg = alg;
        }

        @Override
        public void setSettings(
                final Move mainMove,
                final GameSettings gs,
                final int maxDepth,
                final int moveVersion,
                final SearchFunc<?> searchFunc) {
            super.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
            alg.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
        }

        @Override
        public boolean isCheck(final Color color) {
            return TTFactory.isCheck(gs, table, color);
        }

        @Override
        public boolean isStalemate(final Color color) {
            return TTFactory.isStalemate(gs, table, color);
        }

        @Override
        public List<Move> getLegalMoves(final Color color) throws ChessError {
            return TTFactory.getLegalMoves(gs, table, color);
        }

        @Override
        public int negaNullMoveSearch(
                final boolean isMyMove,
                int alfa,
                int beta,
                final int depth,
                final boolean isPrevNullMove)
                throws ChessError {
            final BoardState boardState = gs.history.getLastBoardState();
            final TTEntry entry = table.find(boardState);
            if (entry != null && entry.depth >= depth) {
                if (entry.lowerBound >= beta) return entry.lowerBound;
                if (entry.upperBound <= alfa) return entry.upperBound;
                if (entry.lowerBound > alfa) alfa = entry.lowerBound;
                if (entry.upperBound < beta) beta = entry.upperBound;
            }

            final int est = alg.negaNullMoveSearch(isMyMove, alfa, beta, depth, isPrevNullMove);

            table.storeEstimation(boardState, est, alfa, beta, depth);

            return est;
        }

        @Override
        public void run() {
            alg.run();
        }
    }

    public static class NegaVerifiedNullMoveAlfaBetaTT extends NegaVerifiedNullMoveAlfaBeta {

        private final TranspositionTable table;
        private final NegaVerifiedNullMoveAlfaBeta alg;

        protected NegaVerifiedNullMoveAlfaBetaTT(
                final TranspositionTable table, final NegaVerifiedNullMoveAlfaBeta alg) {
            super(alg);
            this.table = table;
            this.alg = alg;
        }

        @Override
        public void setSettings(
                final Move mainMove,
                final GameSettings gs,
                final int maxDepth,
                final int moveVersion,
                final SearchFunc<?> searchFunc) {
            super.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
            alg.setSettings(mainMove, gs, maxDepth, moveVersion, searchFunc);
        }

        @Override
        public boolean isCheck(final Color color) {
            return TTFactory.isCheck(gs, table, color);
        }

        @Override
        public boolean isStalemate(final Color color) {
            return TTFactory.isStalemate(gs, table, color);
        }

        @Override
        public List<Move> getLegalMoves(final Color color) throws ChessError {
            return TTFactory.getLegalMoves(gs, table, color);
        }

        @Override
        public int negaVNMSearch(
                final boolean isMyMove,
                int alfa,
                int beta,
                final int depth,
                final boolean verify,
                final boolean isPrevNullMove)
                throws ChessError {
            final BoardState boardState = gs.history.getLastBoardState();
            final TTEntry entry = table.find(boardState);
            if (entry != null && entry.depth >= depth) {
                if (entry.lowerBound >= beta) return entry.lowerBound;
                if (entry.upperBound <= alfa) return entry.upperBound;
                if (entry.lowerBound > alfa) alfa = entry.lowerBound;
                if (entry.upperBound < beta) beta = entry.upperBound;
            }

            final int est = alg.negaVNMSearch(isMyMove, alfa, beta, depth, verify, isPrevNullMove);

            table.storeEstimation(boardState, est, alfa, beta, depth);

            return est;
        }

        @Override
        public void run() {
            alg.run();
        }
    }
}
