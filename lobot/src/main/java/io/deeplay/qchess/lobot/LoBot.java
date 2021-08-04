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

        List<Move> moves = ms.getAllCorrectMoves(color);
        setTurnIntoAll(moves);
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

    private int expectimax(int depth, Color currentColor) throws ChessError, ChessException {
        if (depth == 0) {
            return evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllCorrectMoves(currentColor);

        if (moves.isEmpty() && egd.isCheck(currentColor)) return color == currentColor? Integer.MIN_VALUE : Integer.MAX_VALUE;
        setTurnIntoAll(moves);

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

        List<Move> moves = ms.getAllCorrectMoves(currentColor);

        if (moves.isEmpty() && egd.isCheck(currentColor)) return color == currentColor? Integer.MIN_VALUE : Integer.MAX_VALUE;
        setTurnIntoAll(moves);

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

    private void setTurnIntoAll(List<Move> moves) {
        for (Move move : moves) {
            MoveType type = move.getMoveType();
            if (type == MoveType.TURN_INTO || type == MoveType.TURN_INTO_ATTACK) {
                move.setTurnInto(FigureType.QUEEN);
            }
        }
    }
}
