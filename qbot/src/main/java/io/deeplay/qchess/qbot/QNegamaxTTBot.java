package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag.EXACT;
import static io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag.LOWERBOUND;
import static io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag.UPPERBOUND;
import static java.lang.Math.max;
import static java.lang.Math.min;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.qbot.TranspositionTable.TTEntry;
import io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QNegamaxTTBot extends QBot {
    private static final Logger logger = LoggerFactory.getLogger(QNegamaxTTBot.class);
    public final boolean ttEnable;
    private final TranspositionTable table = new TranspositionTable();
    private final Comparator<Move> order =
            Comparator.comparing(m -> m.getMoveType().importantLevel);
    private int countFindingTT = 0;

    public QNegamaxTTBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy,
            final boolean ttEnable) {
        super(roomSettings, color, searchDepth, strategy, "NegaMaxBot");
        this.ttEnable = ttEnable;
        history.setMinBoardStateToSave(MAX_DEPTH);
    }

    public QNegamaxTTBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final boolean ttEnable) {
        this(roomSettings, color, searchDepth, new PestoStrategy(), ttEnable);
    }

    public QNegamaxTTBot(
            final GameSettings roomSettings, final Color color, final int searchDepth) {
        this(roomSettings, color, searchDepth, new PestoStrategy(), true);
    }

    public QNegamaxTTBot(final GameSettings roomSettings, final Color color) {
        this(roomSettings, color, 3);
    }

    /**
     * Сортирует переданный список ходов по убыванию уровня важности хода
     *
     * @param moves Исходный список ходов.
     */
    private void orderMoves(final List<Move> moves, final GameSettings gs, final int coef) {
        moves.sort(
                Comparator.comparingInt(
                        m -> {
                            return m.getMoveType().importantLevel;
                            /*int res = 0;
                            try {
                                gs.moveSystem.move(m);
                                res = coef * strategy.evaluateBoard(gs.board);
                                gs.moveSystem.undoMove();
                                return res;
                            } catch (final ChessError e) {
                                e.printStackTrace();
                            }
                            return res;*/
                        }));
    }

    public int getCountFindingTT() {
        return countFindingTT;
    }

    @Override
    public List<Move> getTopMoves() throws ChessError {
        /*final List<Move> topMoves = new ArrayList<>();
        int maxGrade = Integer.MIN_VALUE;
        final List<Move> allMoves = ms.getAllPreparedMoves(color);
        orderMoves(allMoves, roomSettings, color == Color.WHITE ? 1 : -1);
        for (final Move move : allMoves) {
            final int curGrade = root(move);
            if (curGrade > maxGrade) {
                maxGrade = curGrade;
                topMoves.clear();
                logger.info("{} {}", move, curGrade);
            }
            if (curGrade >= maxGrade) {
                topMoves.add(move);
            }
        }
        return topMoves;*/
        final MoveWrapper bestMove = new MoveWrapper();
        negamax(new GameSettings(roomSettings, MAX_DEPTH), depth+1, Integer.MIN_VALUE, Strategy.MAX_EST, color, bestMove);
        return Collections.singletonList(bestMove.move);
    }

    private int negamax(
            final GameSettings gs,
            final int curDepth,
            int alpha,
            final int beta,
            final Color curColor,
            final MoveWrapper bestMove)
            throws ChessError {
        /*final int alphaOrig = alpha;
        final BoardState boardState = gs.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);*/

        /*// todo сделать что то с entry.depth == curDepth
        if (ttEnable && entry != null && entry.depth == curDepth && curDepth != depth+1) {
            countFindingTT++;
            if (entry.flag == EXACT) return entry.value;
            else if (entry.flag == LOWERBOUND) alpha = max(alpha, entry.value);
            else if (entry.flag == UPPERBOUND) beta = min(beta, entry.value);
            if (alpha >= beta) return entry.value;
        }*/

        final int coef = curColor == Color.WHITE ? 1 : -1;
        if (curDepth == 0) {
            return coef * strategy.evaluateBoard(gs.board);
        }

        final List<Move> allMoves = gs.moveSystem.getAllPreparedMoves(curColor);
        // Если терминальный узел
        final EndGameType gameResult = gs.endGameDetector.updateEndGameStatus(allMoves, curColor);
        if (gameResult != EndGameType.NOTHING) {
            return coef * strategy.gradeIfTerminalNode(gameResult, curDepth);
        }

        orderMoves(allMoves, gs, coef);
        int value = Integer.MIN_VALUE;

        for (final Move move : allMoves) {
            gs.moveSystem.move(move);
            value =
                    max(
                            value,
                            -negamax(
                                    gs, curDepth - 1, -beta, -alpha, curColor.inverse(), bestMove));
            gs.moveSystem.undoMove();
            if (value > alpha) {
                alpha = value;
                if (curDepth == depth+1) bestMove.move = move;
            }
            if (alpha >= beta) break;
        }

        /*final Flag flag;
        if (value <= alphaOrig) flag = UPPERBOUND;
        else if (value >= beta) flag = LOWERBOUND;
        else flag = EXACT;

        table.store(new TTEntry(value, curDepth, flag), boardState);*/

        return value;
    }

    /** Точка входа в негамакс после выполнения виртуального хода */
    public int root(final Move move) throws ChessError {
        logger.debug("Негамакс с виртуальным {} ходом стартовал", move);
        final GameSettings newGs = new GameSettings(roomSettings, MAX_DEPTH);
        newGs.moveSystem.move(move);
        final int res = -negamax(newGs, depth, Strategy.MIN_EST, Strategy.MAX_EST, color.inverse());
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    /**
     * Классический минимакс с альфа-бетта отсечением
     *
     * @param curDepth Глубина поиска
     * @param alpha Лучшая оценка максимизирующего игрока
     * @param beta Лучшая оценка минимизирующего игрока
     * @param curColor Цвет максимизирующего игрока
     * @return Оценку позиции на доске
     * @throws ChessError При выполнении некорректного хода (при нормальной работе невозможно)
     */
    private int negamax(
            final GameSettings gs, final int curDepth, int alpha, int beta, final Color curColor)
            throws ChessError {
        final int alphaOrig = alpha;
        final BoardState boardState = gs.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);

        // todo сделать что то с entry.depth == curDepth
        if (ttEnable && entry != null && entry.depth == curDepth) {
            countFindingTT++;
            if (entry.flag == EXACT) return entry.value;
            else if (entry.flag == LOWERBOUND) alpha = max(alpha, entry.value);
            else if (entry.flag == UPPERBOUND) beta = min(beta, entry.value);
            if (alpha >= beta) return entry.value;
        }

        final int coef = curColor == Color.WHITE ? 1 : -1;
        if (curDepth == 0) {
            return coef * strategy.evaluateBoard(gs.board);
        }

        final List<Move> allMoves = gs.moveSystem.getAllPreparedMoves(curColor);
        // Если терминальный узел
        final EndGameType gameResult = gs.endGameDetector.updateEndGameStatus(allMoves, curColor);
        if (gameResult != EndGameType.NOTHING) {
            return coef * strategy.gradeIfTerminalNode(gameResult, curDepth);
        }

        orderMoves(allMoves, gs, coef);
        int value = Integer.MIN_VALUE;

        for (final Move move : allMoves) {
            gs.moveSystem.move(move);
            value = max(value, -negamax(gs, curDepth - 1, -beta, -alpha, curColor.inverse()));
            gs.moveSystem.undoMove();
            alpha = max(alpha, value);
            if (alpha >= beta) break;
        }

        final Flag flag;
        if (value <= alphaOrig) flag = UPPERBOUND;
        else if (value >= beta) flag = LOWERBOUND;
        else flag = EXACT;

        table.store(new TTEntry(value, curDepth, flag), boardState);

        return value;
    }

    static class MoveWrapper {
        public Move move;
    }

    public static class Builder extends QBot.Builder {
        private final GameSettings gameSettings;
        private final Color color;
        private int depth = 3;
        private Strategy strategy = new PestoStrategy();
        private boolean ttEnable = false;

        public Builder(final GameSettings gameSettings, final Color color) {
            this.gameSettings = gameSettings;
            this.color = color;
        }

        @Override
        public Builder setDepth(final int depth) {
            this.depth = depth;
            return this;
        }

        @Override
        public Builder setStrategy(final Strategy strategy) {
            this.strategy = strategy;
            return this;
        }

        @Override
        public Builder withTT() {
            ttEnable = true;
            return this;
        }

        @Override
        public QNegamaxTTBot build() {
            return new QNegamaxTTBot(gameSettings, color, depth, strategy, ttEnable);
        }
    }
}
