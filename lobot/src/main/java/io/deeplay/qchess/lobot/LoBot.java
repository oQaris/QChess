package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LoBot extends RemotePlayer {
    public static int COUNT = 0;
    public static long TIME = 0;
    public static long MAX = 0;
    private final int MIN_INT = -9999;
    private final int MAX_INT = 9999;

    private EvaluateStrategy evaluateStrategy;
    private int depth;

    public LoBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "lobot-" + UUID.randomUUID());
        this.evaluateStrategy = new SimpleEvaluateStrategy();
        depth = 2;
    }
    public LoBot(GameSettings roomSettings, Color color, EvaluateStrategy evaluateStrategy) {
        super(roomSettings, color, "lobot-" + UUID.randomUUID());
        this.evaluateStrategy = evaluateStrategy;
        depth = 2;
    }
    public LoBot(GameSettings roomSettings, Color color, EvaluateStrategy evaluateStrategy, int depth) {
        super(roomSettings, color, "lobot-" + UUID.randomUUID());
        this.evaluateStrategy = evaluateStrategy;
        this.depth = depth;
    }

    @Override
    public Move getNextMove() throws ChessError {
        try {
            long startTime = System.currentTimeMillis();

            Move move = minimaxRoot(depth, color);

            long time = System.currentTimeMillis() - startTime;
            TIME += time;
            COUNT++;
            if(time > MAX) MAX = time;

            return move;
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Move minimaxRoot(int depth, Color curColor) throws ChessError, ChessException {

        List<Move> moves = ms.getAllCorrectMoves(curColor);
        setTurnIntoAll(moves);
        int bestMove = MIN_INT;
        List<Move> bestMoves = new ArrayList<>();

        for (Move move : moves) {
            int value = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                    minimax(depth - 1, MIN_INT - 1, MAX_INT + 1, curColor.inverse()));
            if (value > bestMove) {
                bestMove = value;
                if (!bestMoves.isEmpty()) {
                    bestMoves.clear();
                }
                bestMoves.add(move);
            } else if (value == bestMove) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get((new Random()).nextInt(bestMoves.size()));
    };

    private int minimax(int depth, int alpha, int beta, Color curColor) throws ChessError, ChessException {
        if (depth == 0) {
            return evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllCorrectMoves(curColor);

        if (moves.isEmpty() && egd.isCheck(curColor)) return color == curColor? MIN_INT : MAX_INT;
        setTurnIntoAll(moves);

        int bestMove = color == curColor? MIN_INT : MAX_INT;
        for (Move move : moves) {
            int alphaForLambda = alpha;
            int betaForLambda = beta;
            int cur = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                minimax(depth - 1, alphaForLambda, betaForLambda, curColor.inverse()));

            if (color == curColor) {
                bestMove = Math.max(bestMove, cur);
                if (bestMove >= beta) {
                    return bestMove;
                }
                alpha = Math.max(alpha, bestMove);
            } else {
                bestMove = Math.min(bestMove, cur);
                if (bestMove <= alpha) {
                    return bestMove;
                }
                beta = Math.min(beta, bestMove);
            }
        }
        return bestMove;

        /*if (color == curColor) {
            int bestMove = MIN_INT;
            for (Move move : moves) {
                int alphaForLambda = alpha;
                int betaForLambda = beta;
                int cur = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                        minimax(depth - 1, alphaForLambda, betaForLambda, curColor.inverse()));
                bestMove = Math.max(bestMove, cur);
                if (bestMove >= beta) {
                    return bestMove;
                }
                alpha = Math.max(alpha, bestMove);
            }
            return bestMove;
        } else {
            int bestMove = MAX_INT;
            for (Move move : moves) {
                int alphaForLambda = alpha;
                int betaForLambda = beta;
                int cur = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                        minimax(depth - 1, alphaForLambda, betaForLambda, curColor.inverse()));
                bestMove = Math.min(bestMove, cur);
                if (bestMove <= alpha) {
                    return bestMove;
                }
                beta = Math.min(beta, bestMove);
            }
            return bestMove;
        }*/
    }

    private void setTurnIntoAll(List<Move> moves) {
        for (Move move : moves) {
            MoveType type = move.getMoveType();
            if (type == MoveType.TURN_INTO || type == MoveType.TURN_INTO_ATTACK) {
                move.setTurnInto(FigureType.QUEEN);
            }
        }
    }
}
