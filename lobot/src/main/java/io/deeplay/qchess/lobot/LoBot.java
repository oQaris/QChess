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
    public static int COUNT = 0;
    public static long TIME = 0;
    public static long MAX = 0;
    private final int MIN_INT = -9999;
    private final int MAX_INT = 9999;

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
                minimax(depth - 1, MIN_INT - 1, MAX_INT + 1, color.inverse());
        } else if(traversal == TraversalAlgorithm.EXPECTIMAX) {
            return (from, to) -> expectimax(depth - 1, color.inverse());
        }
        return null;
    }


    @Override
    public Move getNextMove() throws ChessError {
        try {
            long startTime = System.currentTimeMillis();

            Move move = runRoot(algorithm);

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

    private Move runRoot(ChessMoveFunc<Integer> algorithm) throws ChessError, ChessException {

        List<Move> moves = ms.getAllCorrectMoves(color);
        setTurnIntoAll(moves);
        int bestMove = MIN_INT;
        List<Move> bestMoves = new ArrayList<>();

        for (Move move : moves) {
            int value = roomSettings.moveSystem.virtualMove(move, algorithm);
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
    }

    private int expectimax(int depth, Color curColor) throws ChessError, ChessException {
        if (depth == 0) {
            return evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllCorrectMoves(curColor);

        if (moves.isEmpty() && egd.isCheck(curColor)) return color == curColor? MIN_INT : MAX_INT;
        setTurnIntoAll(moves);

        if (color == curColor) {
            int bestMove = MIN_INT;
            for (Move move : moves) {
                int cur = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                    expectimax(depth - 1, curColor.inverse()));
                bestMove = Math.max(bestMove, cur);
            }
            return bestMove;
        } else {
            int sum = 0;
            for (Move move : moves) {
                int cur = roomSettings.moveSystem.virtualMove(move, (from, to) ->
                    expectimax(depth - 1, curColor.inverse()));
                sum += cur;
            }
            return (int) Math.round((sum * 1.0) / moves.size());
        }
    }

    private int minimax(int depth, int alpha, int beta, Color curColor) throws ChessError, ChessException {
        if (depth == 0) {
            return evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllCorrectMoves(curColor);

        if (moves.isEmpty() && egd.isCheck(curColor)) return color == curColor? MIN_INT : MAX_INT;
        setTurnIntoAll(moves);

        int bestMove = color == curColor? MIN_INT / 2 : MAX_INT / 2;
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
