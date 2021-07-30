package io.deeplay.qchess.qbot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
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

    public QBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final IStrategy strategy) {
        super(roomSettings, color, "minimax-bot-" + UUID.randomUUID());
        this.strategy = strategy;
        this.depth = searchDepth;
    }

    public QBot(final GameSettings roomSettings, final Color color) {
        this(roomSettings, color, 1, new MatrixStrategy());
    }

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = Integer.MIN_VALUE;
        final List<Move> allMoves = ms.getAllCorrectMoves(color);
        setTurnIntoAll(allMoves);
        for (Move move : allMoves) {
            int curGrade = minimaxRoot(move);
            if (curGrade > maxGrade) {
                maxGrade = curGrade;
                topMoves.clear();
            }
            if (curGrade >= maxGrade) topMoves.add(move);
        }
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    public int minimaxRoot(final Move move) throws ChessError {
        logger.debug("Минимакс стартовал с глубиной поиска: {}", depth);
        int res = 0;
        try {
            res =
                    ms.virtualMove(
                            move,
                            (from, to) ->
                                    minimax(
                                            depth,
                                            Integer.MIN_VALUE,
                                            Integer.MAX_VALUE,
                                            // т.к. следующий ход противника, то максимизируем его,
                                            // если сами играем за чёрных
                                            color != Color.WHITE));
        } catch (ChessException e) {
            logger.error("Ошибка в минимаксе: {}", e.getLocalizedMessage());
        }
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    private int minimax(final int depth, int alpha, int beta, final boolean isMaximisingPlayer)
            throws ChessError, ChessException {
        logger.trace("Глубина поиска: {}", depth);
        if (depth == 0) return strategy.evaluateBoard(board);

        final int initGrade = isMaximisingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestGrade = initGrade / 2;

        // максимальное берём из наших, минимальное - из противника
        Color curColor = isMaximisingPlayer ? color : color.inverse();

        final List<Move> allMoves = ms.getAllCorrectMoves(curColor);
        // todo вынести в функцию оценки
        if (allMoves.isEmpty() && egd.isCheck(curColor)) return initGrade;

        setTurnIntoAll(allMoves);
        for (Move move : allMoves) {
            final int finalAlpha = alpha;
            final int finalBeta = beta;
            int curGrade =
                    ms.virtualMove(
                            move,
                            (from, to) ->
                                    minimax(depth - 1, finalAlpha, finalBeta, !isMaximisingPlayer));

            if (isMaximisingPlayer) bestGrade = Math.max(bestGrade, curGrade);
            else bestGrade = Math.min(bestGrade, curGrade);

            // Альфа-бетта отсечение
            if (isMaximisingPlayer) alpha = Math.max(alpha, bestGrade);
            else beta = Math.min(beta, bestGrade);
            if (beta <= alpha) return bestGrade;
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
    private void setTurnIntoAll(List<Move> moves) {
        final int originalSize = moves.size();
        for (int i = 0; i < originalSize; i++) {
            final Move move = moves.get(i);
            final MoveType type = move.getMoveType();
            if (type == MoveType.TURN_INTO || type == MoveType.TURN_INTO_ATTACK) {
                moves.get(i).setTurnInto(FigureType.QUEEN);
                moves.add(new Move(move, FigureType.KNIGHT));
            }
        }
    }
}
