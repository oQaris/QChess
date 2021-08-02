package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.strategy.IStrategy;
import io.deeplay.qchess.qbot.strategy.MatrixStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QBot extends RemotePlayer {
    private static final Logger logger = LoggerFactory.getLogger(QBot.class);
    private final IStrategy strategy;
    private final int depth;
    private int countNode = 0;
    private int countAB = 0;

    public QBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final IStrategy strategy) {
        super(roomSettings, color, "minimax-bot-" + UUID.randomUUID());
        this.strategy = strategy;
        this.depth = searchDepth;
        if (depth < 0) throw new IllegalArgumentException("Глубина не должна быть отрицательная!");
    }

    public QBot(final GameSettings roomSettings, final Color color) {
        this(roomSettings, color, 1, new MatrixStrategy());
    }

    public QBot(GameSettings roomSettings, Color color, int searchDepth) {
        this(roomSettings, color, searchDepth, new MatrixStrategy());
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = getTopMoves();
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    public List<Move> getTopMoves() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = color == Color.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        final List<Move> allMoves = ms.getAllCorrectMoves(color);
        setTurnIntoAllAndSort(allMoves);
        for (Move move : allMoves) {
            int curGrade = minimaxRootWithVirtualMove(move);
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
        logger.info("Всего нодов дерева: {}", countNode);
        logger.info("a-b отсечений: {}", countAB);
        return topMoves;
    }

    public int minimaxRootWithVirtualMove(final Move move) throws ChessError {
        logger.debug("Минимакс с виртуальным {} ходом стартовал", move);
        ms.move(move);
        int res =
                minimax(
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

    public int minimaxRoot(final int depth, boolean isMaximisingPlayer) throws ChessError {
        logger.debug(
                "Минимакс стартовал с глубиной поиска: {}. Максимизирует? {}",
                depth,
                isMaximisingPlayer);
        int res = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximisingPlayer);
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    private int minimax(final int depth, int alpha, int beta, final boolean isMaximisingPlayer)
            throws ChessError {
        logger.trace("Глубина поиска: {}", depth);
        countNode++;
        if (depth == 0) return strategy.evaluateBoard(board);

        final int initGrade = isMaximisingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestGrade = initGrade / 2;

        Color curColor = isMaximisingPlayer ? Color.WHITE : Color.BLACK;

        final List<Move> allMoves = ms.getAllCorrectMoves(curColor);
        // Если терминальный узел
        if (allMoves.isEmpty() && egd.isCheck(curColor)) return initGrade;

        setTurnIntoAllAndSort(allMoves);
        for (Move move : allMoves) {
            ms.move(move);
            int curGrade = minimax(depth - 1, alpha, beta, !isMaximisingPlayer);
            ms.undoMove();

            if (isMaximisingPlayer) {
                bestGrade = Math.max(bestGrade, curGrade);
                alpha = Math.max(alpha, bestGrade);
            } else {
                bestGrade = Math.min(bestGrade, curGrade);
                beta = Math.min(beta, bestGrade);
            }
            // Альфа-бетта отсечение
            if (beta <= alpha) {
                countAB++;
                return bestGrade;
            }
        }
        logger.trace("Текущая оценка доски: {}", depth);
        return bestGrade;
    }

    /**
     * Заменяет в списке все ходы типа TURN_INTO на такие же ходы, но с добавленными фигурами для
     * превращения. Исходный порядок не гарантируется.
     *
     * @param moves Исходный список ходов.
     */
    private void setTurnIntoAllAndSort(List<Move> moves) {
        final int originalSize = moves.size();
        for (int i = 0; i < originalSize; i++) {
            final Move move = moves.get(i);
            final MoveType type = move.getMoveType();
            if (type == MoveType.TURN_INTO || type == MoveType.TURN_INTO_ATTACK) {
                moves.get(i).setTurnInto(FigureType.QUEEN);
                moves.add(new Move(move, FigureType.KNIGHT));
            }
        }
        moves.sort((m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel);
    }
}
