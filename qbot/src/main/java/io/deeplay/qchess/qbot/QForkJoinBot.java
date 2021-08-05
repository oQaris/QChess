package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.qbot.QMinimaxBot.MAX_DEPTH;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class QForkJoinBot extends RemotePlayer {
    private final Strategy strategy;
    private final int depth;

    public QForkJoinBot(
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

    @Override
    public Move getNextMove() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = color == Color.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        final List<Move> allMoves = ms.getAllPreparedMoves(color);
        sortMoves(allMoves);
        for (Move move : allMoves) {
            final int curGrade = minimaxRoot(move);
            if (color == Color.WHITE) {
                if (curGrade > maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                }
                if (curGrade >= maxGrade) topMoves.add(move);
            } else {
                if (curGrade < maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                }
                if (curGrade <= maxGrade) topMoves.add(move);
            }
        }
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    public int minimaxRoot(final Move move) throws ChessError {
        ms.move(move);
        final int res =
                minimax(new GameSettings(roomSettings), depth, Integer.MIN_VALUE, Integer.MAX_VALUE, color, null);
        ms.undoMove();
        return res;
    }

    private int minimax(
            final GameSettings curNode,
            final int depth,
            int alpha,
            int beta,
            final Color curColor,
            final MoveWrapper bestMove)
            throws ChessError {
        if (depth == 0) return strategy.evaluateBoard(curNode.board);

        final boolean isMaximisingPlayer = curColor == Color.WHITE;
        final List<Move> allMoves = curNode.moveSystem.getAllPreparedMoves(curColor);
        // Если терминальный узел
        final EndGameType gameResult = curNode.endGameDetector.updateEndGameStatus();
        curNode.endGameDetector.revertEndGameStatus();
        if (gameResult != EndGameType.NOTHING) return strategy.gradeIfTerminalNode(gameResult);

        int value = isMaximisingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        sortMoves(allMoves);
        for (Move move : allMoves) {
            GameSettings newNode = new GameSettings(curNode);
            newNode.moveSystem.move(move);
            int curGrade = minimax(newNode, depth - 1, alpha, beta, curColor.inverse(), bestMove);

            if (isMaximisingPlayer) {
                value = Math.max(value, curGrade);
                if (alpha < value) {
                    alpha = value;
                    // if (color == Color.WHITE) bestMove.move = move;
                }
                if (value >= beta) break;
            } else {
                value = Math.min(value, curGrade);
                if (beta < value) {
                    beta = value;
                    // if (color == Color.BLACK) bestMove.move = move;
                }
                if (value <= alpha) break;
            }
        }
        return value;
    }

    private void sortMoves(List<Move> moves) {
        moves.sort((m1, m2) -> m2.getMoveType().importantLevel - m1.getMoveType().importantLevel);
    }

    private static class MoveWrapper {
        public Move move;
    }
}
