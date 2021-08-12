package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag.EXACT;
import static io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag.LOWERBOUND;
import static io.deeplay.qchess.qbot.TranspositionTable.TTEntry.Flag.UPPERBOUND;
import static java.lang.Math.max;
import static java.lang.Math.min;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Board;
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

    public QNegamaxTTBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy) {
        super(roomSettings, color, "negamax-bot-" + UUID.randomUUID(), "Самый НигаЖёский");
        this.strategy = strategy;
        this.depth = searchDepth;
        if (depth < 0 || depth > MAX_DEPTH)
            throw new IllegalArgumentException("Некорректная глубина поиска!");
    }

    public QNegamaxTTBot(GameSettings roomSettings, Color color, int searchDepth) {
        this(roomSettings, color, searchDepth, new PestoStrategy());
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = getTopMoves();
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    public List<Move> getTopMoves() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = Integer.MIN_VALUE;
        final List<Move> allMoves = ms.getAllPreparedMoves(color);
        orderMoves(allMoves);
        for (Move move : allMoves) {
            final int curGrade = root(move);
            if (curGrade > maxGrade) {
                maxGrade = curGrade;
                topMoves.clear();
                logger.info("{} {}", move, curGrade);
            }
            if (curGrade >= maxGrade) topMoves.add(move);
        }
        return topMoves;
    }

    /** Точка входа в негамакс после выполнения виртуального хода */
    public int root(final Move move) throws ChessError {
        logger.debug("Негамакс с виртуальным {} ходом стартовал", move);
        ms.move(move);
        final int res = negamax(roomSettings, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, color);
        ms.undoMove();
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    /**
     * Классический минимакс с альфа-бетта отсечением
     *
     * @param depth Глубина поиска
     * @param alpha Лучшая оценка максимизирующего игрока
     * @param beta Лучшая оценка минимизирующего игрока
     * @param curColor Цвет максимизирующего игрока
     * @return Оценку позиции на доске
     * @throws ChessError При выполнении некорректного хода (при нормальной работе невозможно)
     */
    private int negamax(
            final GameSettings node, final int depth, int alpha, int beta, final Color curColor)
            throws ChessError {

        final int alphaOrig = alpha;

        final BoardState boardState = roomSettings.history.getLastBoardState();
        final TTEntry entry = table.find(boardState);

        if (entry != null && entry.depth >= depth) {
            if (entry.flag == EXACT) return entry.value;
            else if (entry.flag == LOWERBOUND) alpha = max(alpha, entry.value);
            else if (entry.flag == UPPERBOUND) beta = min(beta, entry.value);

            if (alpha >= beta) return entry.value;
        }

        int coef = curColor == Color.WHITE ? 1 : -1;
        if (depth == 0) return coef * evaluateBoard(node.board, curColor);
        // Если терминальный узел
        final EndGameType gameResult = node.endGameDetector.updateEndGameStatus();
        node.endGameDetector.revertEndGameStatus();
        if (gameResult != EndGameType.NOTHING)
            return coef * gradeIfTerminalNode(gameResult, curColor);

        final List<Move> childNodes = node.moveSystem.getAllPreparedMoves(curColor);
        orderMoves(childNodes);
        int value = Integer.MIN_VALUE;

        for (Move child : childNodes) {
            roomSettings.moveSystem.move(child);
            value =
                    max(
                            value,
                            -negamax(roomSettings, depth - 1, -beta, -alpha, curColor.inverse()));
            roomSettings.moveSystem.undoMove();
            alpha = max(alpha, value);
            if (alpha >= beta) break;
        }

        Flag flag;
        if (value <= alphaOrig) flag = UPPERBOUND;
        else if (value >= beta) flag = LOWERBOUND;
        else flag = EXACT;

        table.store(new TTEntry(value, depth, flag), boardState);

        return value;
    }

    /**
     * Сортирует переданный список ходов по убыванию уровня важности хода
     *
     * @param moves Исходный список ходов.
     */
    private void orderMoves(List<Move> moves) {
        moves.sort((m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel);
    }

    public int evaluateBoard(Board board, Color color) {
        return board.getFigureCount(color) - board.getFigureCount(color.inverse());
    }

    public int gradeIfTerminalNode(EndGameType endGameStatus, Color color) {
        final int MAX = Integer.MAX_VALUE - QMinimaxBot.MAX_DEPTH;
        final int MIN = Integer.MIN_VALUE + QMinimaxBot.MAX_DEPTH+1;
        return switch (endGameStatus) {
            case CHECKMATE_TO_BLACK -> color==Color.WHITE?MAX:MIN;
            case CHECKMATE_TO_WHITE -> color==Color.WHITE?MIN:MAX;
            case STALEMATE_TO_BLACK -> MAX / 2;
            case STALEMATE_TO_WHITE -> MIN / 2;
            case NOTHING -> throw new IllegalArgumentException(
                "Состояние не является терминальным!");
            default -> 0; // Ничьи
        };
    }
}
