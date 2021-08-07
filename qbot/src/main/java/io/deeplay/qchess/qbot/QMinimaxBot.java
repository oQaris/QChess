package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.strategy.MatrixStrategy;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QMinimaxBot extends RemotePlayer {
    public static final int MAX_DEPTH = 100;
    private static final Logger logger = LoggerFactory.getLogger(QMinimaxBot.class);
    private final Strategy strategy;
    private final int depth;

    public QMinimaxBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy) {
        super(roomSettings, color, "minimax-bot-" + UUID.randomUUID());
        this.strategy = strategy;
        this.depth = searchDepth;
        if (depth < 0 || depth > MAX_DEPTH)
            throw new IllegalArgumentException("Некорректная глубина поиска!");
    }

    public QMinimaxBot(GameSettings roomSettings, Color color, int searchDepth) {
        this(roomSettings, color, searchDepth, new MatrixStrategy());
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = getTopMoves();
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    // todo Refactor this method to reduce its Cognitive Complexity
    public List<Move> getTopMoves() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = color == Color.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        final List<Move> allMoves = ms.getAllPreparedMoves(color);
        sortMoves(allMoves);
        for (Move move : allMoves) {
            final int curGrade = minimaxRootWithVirtualMove(move);
            if (color == Color.WHITE) {
                if (curGrade > maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                    logger.info("{} {}", move, curGrade);
                }
                if (curGrade >= maxGrade) topMoves.add(move);
            } else {
                if (curGrade < maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                    logger.info("{} {}", move, curGrade);
                }
                if (curGrade <= maxGrade) topMoves.add(move);
            }
        }
        return topMoves;
    }

    /** Точка входа в минимакс после выполнения виртуального хода */
    public int minimaxRootWithVirtualMove(final Move move) throws ChessError {
        logger.debug("Минимакс с виртуальным {} ходом стартовал", move);
        ms.move(move);
        final int res =
                minimax(
                        roomSettings,
                        depth,
                        Integer.MIN_VALUE,
                        Integer.MAX_VALUE,
                        // т.к. следующий ход противника, то максимизируем его,
                        // если сами играем за чёрных (а он за белых)
                        color == Color.BLACK);
        ms.undoMove();
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    /** Точка входа в минимакс */
    public int minimaxRoot(final int depth, boolean isMaximisingPlayer) throws ChessError {
        logger.debug(
                "Минимакс стартовал с глубиной поиска: {}. Максимизирует? {}",
                depth,
                isMaximisingPlayer);
        final int res =
                minimax(
                        roomSettings,
                        depth,
                        Integer.MIN_VALUE,
                        Integer.MAX_VALUE,
                        isMaximisingPlayer);
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    /**
     * Классический минимакс с альфа-бетта отсечением
     *
     * @param depth Глубина поиска
     * @param alpha Лучшая оценка максимизирующего игрока
     * @param beta Лучшая оценка минимизирующего игрока
     * @param isMaximisingPlayer true, если сейчас ходит белый игрок (максимизирует оценку)
     * @return Оценку позиции на доске
     * @throws ChessError При выполнении некорректного хода (при нормальной работе невозможно)
     */
    private int minimax(
            final GameSettings node,
            final int depth,
            int alpha,
            int beta,
            final boolean isMaximisingPlayer)
            throws ChessError {
        logger.trace("Глубина поиска: {}", depth);
        if (depth == 0) return strategy.evaluateBoard(node.board);

        final Color curColor = isMaximisingPlayer ? Color.WHITE : Color.BLACK;
        final List<Move> allMoves = node.moveSystem.getAllPreparedMoves(curColor);
        // Если терминальный узел
        // todo Оптимизировать, чтоб два раза не вызывать ms.getAllMoves
        final EndGameType gameResult = node.endGameDetector.updateEndGameStatus();
        node.endGameDetector.revertEndGameStatus();
        if (gameResult != EndGameType.NOTHING) return strategy.gradeIfTerminalNode(gameResult);

        sortMoves(allMoves);
        int value;
        // todo Refactor this method to reduce its Cognitive Complexity
        if (isMaximisingPlayer) {
            value = Integer.MIN_VALUE;
            for (Move move : allMoves) {
                // GameSettings newNode = new GameSettings(node);
                roomSettings.moveSystem.move(move);
                value = Math.max(value, minimax(roomSettings, depth - 1, alpha, beta, false));
                roomSettings.moveSystem.undoMove();
                alpha = Math.max(alpha, value);
                if (value >= beta) break;
            }
        } else {
            value = Integer.MAX_VALUE;
            for (Move move : allMoves) {
                // GameSettings newNode = new GameSettings(node);
                roomSettings.moveSystem.move(move);
                value = Math.min(value, minimax(roomSettings, depth - 1, alpha, beta, true));
                roomSettings.moveSystem.undoMove();
                beta = Math.min(beta, value);
                if (value <= alpha) break;
            }
        }
        // отнимаем/прибавляем грубину, чтоб из ходов с одинаковыми оценками выбирался тот,
        // который достигается за меньшее число ходов
        return value + (isMaximisingPlayer ? 1 : -1) * depth;
    }

    /**
     * Сортирует переданный список ходов по убыванию уровня важности хода
     *
     * @param moves Исходный список ходов.
     */
    private void sortMoves(List<Move> moves) {
        moves.sort((m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel);
    }
}
