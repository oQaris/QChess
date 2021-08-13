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
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.TranspositionTable.TTEntry;
import io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QNegamaxTTBot extends RemotePlayer {
    public static final int MAX_DEPTH = 100;
    private static final Logger logger = LoggerFactory.getLogger(QNegamaxTTBot.class);
    private final Strategy strategy;
    private final TranspositionTable table = new TranspositionTable();
    private final int depth;
    public int countFindingTT = 0;

    public QNegamaxTTBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy) {
        super(roomSettings, color, "negamax-bot-" + UUID.randomUUID(), "Самый НигаЖёский");
        this.strategy = strategy;
        this.depth = searchDepth;
        if (this.depth < 0 || this.depth > MAX_DEPTH) {
            throw new IllegalArgumentException("Некорректная глубина поиска!");
        }
    }

    public QNegamaxTTBot(
            final GameSettings roomSettings, final Color color, final int searchDepth) {
        this(roomSettings, color, searchDepth, new PestoStrategy());
    }

    public QNegamaxTTBot(final GameSettings roomSettings, final Color color) {
        this(roomSettings, color, 3);
    }

    /**
     * Сортирует переданный список ходов по убыванию уровня важности хода
     *
     * @param moves Исходный список ходов.
     */
    private static void orderMoves(final List<Move> moves) {
        moves.sort((m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel);
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = this.getTopMoves();
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    public List<Move> getTopMoves() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = Integer.MIN_VALUE;
        final List<Move> allMoves = this.ms.getAllPreparedMoves(this.color);
        QNegamaxTTBot.orderMoves(allMoves);
        for (final Move move : allMoves) {
            final int curGrade = this.root(move);
            if (curGrade > maxGrade) {
                maxGrade = curGrade;
                topMoves.clear();
                logger.info("{} {}", move, curGrade);
            }
            if (curGrade >= maxGrade) {
                topMoves.add(move);
            }
        }
        return topMoves;
    }

    /** Точка входа в негамакс после выполнения виртуального хода */
    public int root(final Move move) throws ChessError {
        logger.debug("Негамакс с виртуальным {} ходом стартовал", move);
        this.ms.move(move);
        final int res = -this.negamax(this.depth, Integer.MIN_VALUE, Integer.MAX_VALUE, this.color.inverse());
        this.ms.undoMove();
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
    private int negamax(final int curDepth, int alpha, int beta, final Color curColor)
            throws ChessError {

        final int alphaOrig = alpha;

        final BoardState boardState = roomSettings.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);

        if (entry != null && entry.depth >= curDepth) {
            countFindingTT++;
            if (entry.flag == EXACT) return entry.value;
            else if (entry.flag == LOWERBOUND) alpha = max(alpha, entry.value);
            else if (entry.flag == UPPERBOUND) beta = min(beta, entry.value);

            if (alpha >= beta) return entry.value;
        }

        final int coef = curColor == Color.WHITE ? 1 : -1;
        if (curDepth == 0) {
            return coef * this.strategy.evaluateBoard(this.board);
        }

        final List<Move> allMoves = this.ms.getAllPreparedMoves(curColor);
        // Если терминальный узел
        final EndGameType gameResult = this.egd.updateEndGameStatus();
        this.egd.revertEndGameStatus();
        if (gameResult != EndGameType.NOTHING) {
            return coef * this.strategy.gradeIfTerminalNode(gameResult, curDepth);
        }

        QNegamaxTTBot.orderMoves(allMoves);
        int value = Integer.MIN_VALUE;

        for (final Move move : allMoves) {
            /*if (curDepth == depth)
                System.err.println(curDepth + " - " + child + " - " + curColor);*/
            this.ms.move(move);
            value = max(value, -this.negamax(curDepth - 1, -beta, -alpha, curColor.inverse()));
            this.ms.undoMove();
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
}
