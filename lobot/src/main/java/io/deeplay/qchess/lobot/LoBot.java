package io.deeplay.qchess.lobot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.logics.MoveSystem.ChessMoveFunc;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.lobot.evaluation.Evaluation;
import io.deeplay.qchess.lobot.evaluation.MonteCarloEvaluation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class LoBot extends RemotePlayer {

    private final Evaluation evaluation;
    private final int depth;
    private final ChessMoveFunc<Integer> algorithm;
    private final boolean onMonteCarlo;

    public LoBot(final GameSettings roomSettings, final Color color) {
        this(roomSettings, color, new Strategy());
    }

    public LoBot(final GameSettings roomSettings, final Color color, final Strategy strategy) {
        super(roomSettings, color, "lobot-" + UUID.randomUUID(), "lobot");
        evaluation = strategy.getEvaluation();
        depth = strategy.getDepth();
        algorithm = getAlgorithm(strategy.getAlgorithm());
        history.setMinBoardStateToSave(100);
        onMonteCarlo = strategy.getOnMonteCarlo();
    }

    private ChessMoveFunc<Integer> getAlgorithm(final TraversalAlgorithm traversal) {
        return switch (traversal) {
            case MINIMAX -> (from, to) ->
                    minimax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.inverse());
            case EXPECTIMAX -> (from, to) -> expectimax(depth - 1, color.inverse());
            case NEGAMAX -> (from, to) -> negamax(depth - 1, color.inverse());
            case NEGASCOUT -> (from, to) ->
                    negascout(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.inverse());
            case NEGAMAXALPHABETA -> (from, to) ->
                    negamaxWithAlphaBeta(
                            depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.inverse());
            default -> null;
        };
    }

    @Override
    public Move getNextMove() throws ChessError {
        try {
            return runRoot();
        } catch (final ChessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Move runRoot() throws ChessError, ChessException {
        final List<Move> moves = ms.getAllPreparedMoves(color);
        int bestMove = Integer.MIN_VALUE;
        final List<Move> bestMoves = new ArrayList<>();
        roomSettings = new GameSettings(roomSettings, 100);

        for (final Move move : moves) {
            final int value = roomSettings.moveSystem.virtualMove(move, algorithm);
            if (value > bestMove) {
                bestMove = value;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (value == bestMove) {
                bestMoves.add(move);
            }
        }

        if (bestMoves.size() == 1) {
            return bestMoves.get(0);
        }

        if (!onMonteCarlo || Math.abs(bestMove) > Integer.MAX_VALUE - 500) {
            return bestMoves.get((new Random()).nextInt(bestMoves.size()));
        }
        return getBestMonteCarloTopMove(bestMoves);
    }

    private Move getBestMonteCarloTopMove(final List<Move> bestMoves) throws ChessError {
        final Evaluation monteCarlo = new MonteCarloEvaluation(20);
        int maxEvaluation = 0;
        Move bestMove = bestMoves.get(0);
        for (final Move move : bestMoves) {
            roomSettings.moveSystem.move(move);
            final int evaluation = monteCarlo.evaluateBoard(roomSettings, color.inverse());
            roomSettings.moveSystem.undoMove();

            if (evaluation > maxEvaluation) {
                maxEvaluation = evaluation;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int negamax(final int depth, final Color currentColor)
            throws ChessError, ChessException {
        if (depth == 0) {
            return -evaluation.evaluateBoard(roomSettings, color);
        }

        final List<Move> moves = ms.getAllPreparedMoves(currentColor);

        final EndGameType endGameType = egd.updateEndGameStatus();
        egd.revertEndGameStatus();
        if (endGameType != EndGameType.NOTHING) {
            return Strategy.getTerminalEvaluation(currentColor, endGameType);
        }

        int bestMoveValue = Integer.MIN_VALUE;
        for (final Move move : moves) {
            final int currentValue =
                    roomSettings.moveSystem.virtualMove(
                            move, (from, to) -> negamax(depth - 1, currentColor.inverse()));
            bestMoveValue = Math.max(bestMoveValue, currentValue);
        }
        return -bestMoveValue;
    }

    private int negamaxWithAlphaBeta(
            final int depth, int alpha, final int beta, final Color currentColor)
            throws ChessError, ChessException {
        if (depth == 0) {
            return -evaluation.evaluateBoard(roomSettings, color);
        }

        final List<Move> moves = ms.getAllPreparedMoves(currentColor);

        final EndGameType endGameType = egd.updateEndGameStatus();
        egd.revertEndGameStatus();
        if (endGameType != EndGameType.NOTHING) {
            return Strategy.getTerminalEvaluation(currentColor, endGameType);
        }

        int bestMoveValue = Integer.MIN_VALUE;
        for (final Move move : moves) {
            final int alphaForLambda = alpha;
            final int betaForLambda = beta;
            final int currentValue =
                    roomSettings.moveSystem.virtualMove(
                            move,
                            (from, to) ->
                                    negamaxWithAlphaBeta(
                                            depth - 1,
                                            -betaForLambda,
                                            -alphaForLambda,
                                            currentColor.inverse()));
            bestMoveValue = Math.max(bestMoveValue, currentValue);
            alpha = Math.max(alpha, bestMoveValue);
            if (alpha >= beta) {
                break;
            }
        }
        return -bestMoveValue;
    }

    private int negascout(final int depth, int alpha, final int beta, final Color currentColor)
            throws ChessError, ChessException {
        if (depth == 0) {
            return -evaluation.evaluateBoard(roomSettings, color);
        }

        final List<Move> moves = ms.getAllPreparedMoves(currentColor);

        final EndGameType endGameType = egd.updateEndGameStatus();
        egd.revertEndGameStatus();
        if (endGameType != EndGameType.NOTHING) {
            return Strategy.getTerminalEvaluation(currentColor, endGameType);
        }

        for (int i = 0; i < moves.size(); i++) {
            final int alphaForLambda = alpha;
            final int betaForLambda = beta;
            int currentValue;
            if (i == 0) {
                currentValue =
                        roomSettings.moveSystem.virtualMove(
                                moves.get(i),
                                (from, to) ->
                                        negamaxWithAlphaBeta(
                                                depth - 1,
                                                -betaForLambda,
                                                -alphaForLambda,
                                                currentColor.inverse()));
            } else {
                currentValue =
                        roomSettings.moveSystem.virtualMove(
                                moves.get(i),
                                (from, to) ->
                                        negamaxWithAlphaBeta(
                                                depth - 1,
                                                -alphaForLambda - 1,
                                                -alphaForLambda,
                                                currentColor.inverse()));
            }
            if (alpha < currentValue && currentValue < beta) {
                final int currentValueForLambda = currentValue;
                currentValue =
                        roomSettings.moveSystem.virtualMove(
                                moves.get(i),
                                (from, to) ->
                                        negamaxWithAlphaBeta(
                                                depth - 1,
                                                -betaForLambda,
                                                -currentValueForLambda,
                                                currentColor.inverse()));
            }
            alpha = Math.max(alpha, currentValue);
            if (alpha >= beta) {
                break;
            }
        }
        return -alpha;
    }

    private int expectimax(final int depth, final Color currentColor)
            throws ChessError, ChessException {
        if (depth == 0) {
            return evaluation.evaluateBoard(roomSettings, color);
        }

        final List<Move> moves = ms.getAllPreparedMoves(currentColor);

        final EndGameType endGameType = egd.updateEndGameStatus();
        egd.revertEndGameStatus();
        if (endGameType != EndGameType.NOTHING) {
            return Strategy.getTerminalEvaluation(currentColor, endGameType);
        }

        if (color == currentColor) {
            int bestMoveValue = Integer.MIN_VALUE;
            for (final Move move : moves) {
                final int currentValue =
                        roomSettings.moveSystem.virtualMove(
                                move, (from, to) -> expectimax(depth - 1, currentColor.inverse()));
                bestMoveValue = Math.max(bestMoveValue, currentValue);
            }
            return bestMoveValue;
        } else {
            int sum = 0;
            for (final Move move : moves) {
                final int currentValue =
                        roomSettings.moveSystem.virtualMove(
                                move, (from, to) -> expectimax(depth - 1, currentColor.inverse()));
                sum += currentValue;
            }
            return (int) Math.round((sum * 1.0) / moves.size());
        }
    }

    private int minimax(final int depth, int alpha, int beta, final Color currentColor)
            throws ChessError, ChessException {

        if (depth == 0) {
            return evaluation.evaluateBoard(roomSettings, color);
        }

        final EndGameType endGameType = egd.updateEndGameStatus();
        egd.revertEndGameStatus();
        if (endGameType != EndGameType.NOTHING) {
            return Strategy.getTerminalEvaluation(currentColor, endGameType);
        }

        final List<Move> moves = ms.getAllPreparedMoves(currentColor);
        int bestMoveValue = color == currentColor ? Integer.MIN_VALUE / 2 : Integer.MAX_VALUE / 2;
        for (final Move move : moves) {
            final int alphaForLambda = alpha;
            final int betaForLambda = beta;
            final int currentValue =
                    roomSettings.moveSystem.virtualMove(
                            move,
                            (from, to) ->
                                    minimax(
                                            depth - 1,
                                            alphaForLambda,
                                            betaForLambda,
                                            currentColor.inverse()));

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

    private int clusterMinimax(final int depth, int alpha, int beta, final Color currentColor)
            throws ChessError, ChessException {

        if (depth == 0) {
            return evaluation.evaluateBoard(roomSettings, color);
        }

        final EndGameType endGameType = egd.updateEndGameStatus();
        egd.revertEndGameStatus();
        if (endGameType != EndGameType.NOTHING) {
            return Strategy.getTerminalEvaluation(currentColor, endGameType);
        }

        final List<Move> moves = ms.getAllPreparedMoves(currentColor);

        final List<Integer> clusterMoves =
                Strategy.getClusters(
                        moves.stream()
                                .map(
                                        move -> {
                                            int evaluate = 0;
                                            try {
                                                roomSettings.moveSystem.move(move);
                                                evaluate =
                                                        evaluation.evaluateBoard(
                                                                roomSettings, color);
                                                roomSettings.moveSystem.undoMove();
                                            } catch (final ChessError chessError) {
                                                chessError.printStackTrace();
                                            }
                                            return evaluate;
                                        })
                                .collect(Collectors.toSet()));

        int bestMoveValue = color == currentColor ? Integer.MIN_VALUE / 2 : Integer.MAX_VALUE / 2;
        for (final Move move : moves) {
            final int alphaForLambda = alpha;
            final int betaForLambda = beta;
            final int currentValue =
                    roomSettings.moveSystem.virtualMove(
                            move,
                            (from, to) ->
                                    clusterMinimax(
                                            depth - 1,
                                            alphaForLambda,
                                            betaForLambda,
                                            currentColor.inverse()));

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
