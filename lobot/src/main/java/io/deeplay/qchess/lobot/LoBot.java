package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem.ChessMoveFunc;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.lobot.strategy.EvaluateStrategy;
import io.deeplay.qchess.lobot.strategy.FiguresCostSumEvaluateStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LoBot extends RemotePlayer {
    public static int STEP_COUNT = 0;
    public static long FULL_TIME = 0;
    public static long MAX_TIME = 0;

    private final EvaluateStrategy evaluateStrategy;
    private final int depth;
    private final ChessMoveFunc<Integer> algorithm;

    public LoBot(GameSettings roomSettings, Color color) {
        this(roomSettings, color, new FiguresCostSumEvaluateStrategy(), 2, TraversalAlgorithm.MINIMAX);
    }
    public LoBot(GameSettings roomSettings, Color color, EvaluateStrategy evaluateStrategy) {
        this(roomSettings, color, evaluateStrategy, 2, TraversalAlgorithm.MINIMAX);
    }
    public LoBot(GameSettings roomSettings, Color color, EvaluateStrategy evaluateStrategy, int depth) {
        this(roomSettings, color, evaluateStrategy, depth, TraversalAlgorithm.MINIMAX);
    }
    public LoBot(GameSettings roomSettings, Color color, EvaluateStrategy evaluateStrategy, int depth, TraversalAlgorithm traversal) {
        super(roomSettings, color, "lobot-" + UUID.randomUUID());
        this.evaluateStrategy = evaluateStrategy;
        this.depth = depth;
        algorithm = getAlgorithm(traversal);
    }

    private ChessMoveFunc<Integer> getAlgorithm(TraversalAlgorithm traversal) {
        if (traversal == TraversalAlgorithm.MINIMAX) {
            return (from, to) ->
                minimax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.inverse());
        } else if(traversal == TraversalAlgorithm.EXPECTIMAX) {
            return (from, to) -> expectimax(depth - 1, color.inverse());
        } else if(traversal == TraversalAlgorithm.NEGASCOUT) {
            return (from, to) -> negascout(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.inverse());
        } else if(traversal == TraversalAlgorithm.NEGAMAX) {
            return (from, to) -> negamax(depth - 1, color.inverse());
        } else if(traversal == TraversalAlgorithm.NEGAMAXALPHABETA) {
            return (from, to) -> negamaxWithAlphaBeta(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.inverse());
        }
        return null;
    }


    @Override
    public Move getNextMove() throws ChessError {
        try {
            long startTime = System.currentTimeMillis();

            Move move = runRoot();

            long time = System.currentTimeMillis() - startTime;
            FULL_TIME += time;
            STEP_COUNT++;
            if(time > MAX_TIME) MAX_TIME = time;

            return move;
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Move runRoot() throws ChessError, ChessException {

        List<Move> moves = ms.getAllPreparedMoves(color);
        int bestMove = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();

        for (Move move : moves) {
            int value = roomSettings.moveSystem.virtualMove(move, algorithm);
            if (value > bestMove) {
                bestMove = value;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (value == bestMove) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get((new Random()).nextInt(bestMoves.size()));
    }

    private int negamax(int depth, Color currentColor) throws ChessError, ChessException {
        if (depth == 0) {
            return -evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllPreparedMoves(currentColor);

        if (moves.isEmpty() && egd.isCheck(currentColor)) return (currentColor == color? Integer.MIN_VALUE : Integer.MAX_VALUE);

        int bestMoveValue = Integer.MIN_VALUE / 2;
        for (Move move : moves) {
            int currentValue = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                negamax(depth - 1, currentColor.inverse()));
            bestMoveValue = Math.max(bestMoveValue, currentValue);
        }
        return -bestMoveValue;
    }

    private int negamaxWithAlphaBeta(int depth, int alpha, int beta, Color currentColor) throws ChessError, ChessException {
        if (depth == 0) {
            return -evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllPreparedMoves(currentColor);

        if (moves.isEmpty() && egd.isCheck(currentColor)) return (currentColor == color? Integer.MIN_VALUE : Integer.MAX_VALUE);

        int bestMoveValue = Integer.MIN_VALUE / 2;
        for (Move move : moves) {
            final int alphaForLambda = alpha;
            final int betaForLambda = beta;
            int currentValue = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                negamaxWithAlphaBeta(depth - 1, -betaForLambda, -alphaForLambda, currentColor.inverse()));
            bestMoveValue = Math.max(bestMoveValue, currentValue);
            alpha = Math.max(alpha, bestMoveValue);
            if (alpha >= beta) {
                break;
            }
        }
        return -bestMoveValue;
    }

    @Deprecated
    private int negascout(int depth, int alpha, int beta, Color currentColor)
        throws ChessError, ChessException {
        int b, t;
        if (depth == 0) {
            return -evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllPreparedMoves(currentColor);

        if (moves.isEmpty() && egd.isCheck(currentColor)) return (currentColor == color? Integer.MIN_VALUE : Integer.MAX_VALUE);

        b = beta;
        for (Move move : moves) {
            final int bb = b;
            final int aa = alpha;
            t = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                -negascout (depth - 1, -bb, -aa, currentColor.inverse()));
            if ((t > alpha) && (t < beta))
                t = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                -negascout (depth - 1, -beta, -aa, currentColor.inverse()));
            alpha = Math.max(alpha, t);
            if (alpha >= beta)
                return alpha;
            b = alpha + 1;
        }
        return alpha;
    }

    private int expectimax(int depth, Color currentColor) throws ChessError, ChessException {
        if (depth == 0) {
            return evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllPreparedMoves(currentColor);

        if (moves.isEmpty() && egd.isCheck(currentColor)) return color == currentColor? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (color == currentColor) {
            int bestMoveValue = Integer.MIN_VALUE;
            for (Move move : moves) {
                int currentValue = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                    expectimax(depth - 1, currentColor.inverse()));
                bestMoveValue = Math.max(bestMoveValue, currentValue);
            }
            return bestMoveValue;
        } else {
            int sum = 0;
            for (Move move : moves) {
                int currentValue = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                    expectimax(depth - 1, currentColor.inverse()));
                sum += currentValue;
            }
            return (int) Math.round((sum * 1.0) / moves.size());
        }
    }

    private int minimax(int depth, int alpha, int beta, Color currentColor) throws ChessError, ChessException {
        if (depth == 0) {
            return evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllPreparedMoves(currentColor);

        if (moves.isEmpty() && egd.isCheck(currentColor)) return color == currentColor? Integer.MIN_VALUE : Integer.MAX_VALUE;

        int bestMoveValue = color == currentColor? Integer.MIN_VALUE / 2 : Integer.MAX_VALUE / 2;
        for (Move move : moves) {
            int alphaForLambda = alpha;
            int betaForLambda = beta;
            int currentValue = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                minimax(depth - 1, alphaForLambda, betaForLambda, currentColor.inverse()));

            if (color == currentColor) {
                bestMoveValue = Math.max(bestMoveValue, currentValue);
                if (bestMoveValue >= beta) {
                    return bestMoveValue;
                }
                alpha = Math.max(alpha, bestMoveValue);
            } else {
                bestMoveValue = Math.min(bestMoveValue, currentValue);
                if (bestMoveValue <= alpha) {
                    return bestMoveValue;
                }
                beta = Math.min(beta, bestMoveValue);
            }
        }
        return bestMoveValue;
    }
}
