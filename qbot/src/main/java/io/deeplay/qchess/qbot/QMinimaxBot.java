package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
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
        super(roomSettings, color, "minimax-bot-" + UUID.randomUUID(), "Самый жёский");
        this.strategy = strategy;
        this.depth = searchDepth;
        if (this.depth < 0 || this.depth > MAX_DEPTH) {
            throw new IllegalArgumentException("Некорректная глубина поиска!");
        }
    }

    public QMinimaxBot(final GameSettings roomSettings, final Color color, final int searchDepth) {
        this(roomSettings, color, searchDepth, new PestoStrategy());
    }

    public QMinimaxBot(final GameSettings roomSettings, final Color color) {
        this(roomSettings, color, 3);
    }

    /**
     * Сортирует переданный список ходов по убыванию уровня важности хода
     *
     * @param moves Исходный список ходов.
     */
    private static void sortMoves(final List<Move> moves) {
        moves.sort((m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel);
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = this.getTopMoves();
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    // todo Refactor this method to reduce its Cognitive Complexity
    public List<Move> getTopMoves() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = this.color == Color.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        final List<Move> allMoves = this.ms.getAllPreparedMoves(this.color);
        QMinimaxBot.sortMoves(allMoves);
        for (final Move move : allMoves) {
            final int curGrade = this.minimaxRootWithVirtualMove(move);
            if (this.color == Color.WHITE) {
                if (curGrade > maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                    logger.info("{} {}", move, curGrade);
                }
                if (curGrade >= maxGrade) {
                    topMoves.add(move);
                }
            } else {
                if (curGrade < maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                    logger.info("{} {}", move, curGrade);
                }
                if (curGrade <= maxGrade) {
                    topMoves.add(move);
                }
            }
        }
        return topMoves;
    }

    /** Точка входа в минимакс после выполнения виртуального хода */
    public int minimaxRootWithVirtualMove(final Move move) throws ChessError {
        logger.debug("Минимакс с виртуальным {} ходом стартовал", move);
        this.ms.move(move);
        final int res =
                this.minimax(
                        this.depth,
                        Integer.MIN_VALUE,
                        Integer.MAX_VALUE,
                        // т.к. следующий ход противника, то максимизируем его,
                        // если сами играем за чёрных (а он за белых)
                        this.color == Color.BLACK);
        this.ms.undoMove();
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    /** Точка входа в минимакс */
    public int minimaxRoot(final int depth, final boolean isMaximisingPlayer) throws ChessError {
        logger.debug(
                "Минимакс стартовал с глубиной поиска: {}. Максимизирует? {}",
                depth,
                isMaximisingPlayer);
        final int res =
                this.minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximisingPlayer);
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    /**
     * Классический минимакс с альфа-бетта отсечением
     *
     * @param curDepth Глубина поиска
     * @param alpha Лучшая оценка максимизирующего игрока
     * @param beta Лучшая оценка минимизирующего игрока
     * @param isMaximisingPlayer true, если сейчас ходит белый игрок (максимизирует оценку)
     * @return Оценку позиции на доске
     * @throws ChessError При выполнении некорректного хода (при нормальной работе невозможно)
     */
    private int minimax(final int curDepth, int alpha, int beta, final boolean isMaximisingPlayer)
            throws ChessError {
        logger.trace("Глубина поиска: {}", curDepth);
        if (curDepth == 0) {
            return this.strategy.evaluateBoard(this.board);
        }

        final Color curColor = isMaximisingPlayer ? Color.WHITE : Color.BLACK;
        final List<Move> allMoves = this.ms.getAllPreparedMoves(curColor);
        // Если терминальный узел
        // todo Оптимизировать, чтоб два раза не вызывать ms.getAllMoves
        final EndGameType gameResult = this.egd.updateEndGameStatus();
        this.egd.revertEndGameStatus();
        if (gameResult != EndGameType.NOTHING) {
            return this.strategy.gradeIfTerminalNode(gameResult, curDepth);
        }

        QMinimaxBot.sortMoves(allMoves);
        int value;
        // todo Refactor this method to reduce its Cognitive Complexity
        if (isMaximisingPlayer) {
            value = Integer.MIN_VALUE;
            for (final Move move : allMoves) {
                // GameSettings newNode = new GameSettings(node);
                this.ms.move(move);
                value = Math.max(value, this.minimax(curDepth - 1, alpha, beta, false));
                this.ms.undoMove();
                alpha = Math.max(alpha, value);
                if (value >= beta) {
                    break;
                }
            }
        } else {
            value = Integer.MAX_VALUE;
            for (final Move move : allMoves) {
                // GameSettings newNode = new GameSettings(node);
                this.ms.move(move);
                value = Math.min(value, this.minimax(curDepth - 1, alpha, beta, true));
                this.ms.undoMove();
                beta = Math.min(beta, value);
                if (value <= alpha) {
                    break;
                }
            }
        }
        // отнимаем/прибавляем грубину, чтоб из ходов с одинаковыми оценками выбирался тот,
        // который достигается за меньшее число ходов
        return value + (isMaximisingPlayer ? 1 : -1) * curDepth;
    }
}
