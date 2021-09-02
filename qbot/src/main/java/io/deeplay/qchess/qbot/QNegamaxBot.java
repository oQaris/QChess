package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.game.model.MoveType.ATTACK;
import static io.deeplay.qchess.game.model.MoveType.EN_PASSANT;
import static io.deeplay.qchess.game.model.MoveType.TURN_INTO;
import static io.deeplay.qchess.game.model.MoveType.TURN_INTO_ATTACK;
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
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.qbot.TranspositionTable.TTEntry;
import io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QNegamaxBot extends QBot {
    private static final Logger logger = LoggerFactory.getLogger(QNegamaxBot.class);
    public final boolean ttEnable;
    private final TranspositionTable table = new TranspositionTable();
    private int countFindingTT = 0;

    public QNegamaxBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy,
            final boolean ttEnable) {
        super(roomSettings, color, searchDepth, strategy, "NegaMaxBot");
        this.ttEnable = ttEnable;
        history.setMinBoardStateToSave(MAX_DEPTH);
    }

    public QNegamaxBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final boolean ttEnable) {
        this(roomSettings, color, searchDepth, new PestoStrategy(), ttEnable);
    }

    public QNegamaxBot(final GameSettings roomSettings, final Color color, final int searchDepth) {
        this(roomSettings, color, searchDepth, new PestoStrategy(), true);
    }

    public QNegamaxBot(final GameSettings roomSettings, final Color color) {
        this(roomSettings, color, 6);
    }

    /**
     * Сортирует переданный список ходов по убыванию уровня важности хода
     *
     * @param moves Исходный список ходов.
     */
    private void orderMoves(final List<Move> moves, final GameSettings gs) {
        moves.sort(
                Comparator.comparingInt(
                                (Move m) -> {
                                    final MoveType mt = m.getMoveType();
                                    int level = mt.importantLevel;
                                    // сортировка MVV-LVA
                                    if (mt == ATTACK || mt == TURN_INTO_ATTACK)
                                        level +=
                                                gs.board.getFigureUgly(m.getTo()).figureType.type
                                                        - gs.board.getFigureUgly(m.getFrom())
                                                                .figureType
                                                                .type;
                                    return level;
                                })
                        .reversed());
    }

    public int getCountFindingTT() {
        return countFindingTT;
    }

    @Override
    public List<Move> getTopMoves() throws ChessError {
        final MoveWrapper bestMove = new MoveWrapper();
        negamax(roomSettings, depth + 1, Strategy.MIN_VAL, Strategy.MAX_VAL, color, bestMove);
        clearTT(bestMove.move);
        return Collections.singletonList(bestMove.move);
    }

    private void clearTT(final Move nextMove) {
        final MoveType mt = nextMove.getMoveType();
        if (mt == ATTACK
                || mt == TURN_INTO
                || mt == TURN_INTO_ATTACK
                || mt == EN_PASSANT
                || board.getFigureUgly(nextMove.getFrom()).figureType == FigureType.PAWN)
            table.clear();
    }

    private int negamax(
            final GameSettings gs,
            final int curDepth,
            int alpha,
            int beta,
            final Color curColor,
            final MoveWrapper bestMove)
            throws ChessError {
        final int alphaOrig = alpha;
        final BoardState boardState = gs.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);

        // todo сделать что то с entry.depth == curDepth
        if (ttEnable && entry != null && entry.depth == curDepth && curDepth != depth + 1) {
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

        orderMoves(allMoves, gs);
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
                if (curDepth == depth + 1) bestMove.move = move;
            }
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
        public QNegamaxBot build() {
            return new QNegamaxBot(gameSettings, color, depth, strategy, ttEnable);
        }
    }
}
