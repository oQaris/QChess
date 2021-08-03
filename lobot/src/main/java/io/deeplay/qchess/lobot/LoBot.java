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
import java.util.List;
import java.util.UUID;

public class LoBot extends RemotePlayer {
    public static int COUNT = 0;

    private EvaluateStrategy evaluateStrategy;

    public LoBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "lobot-" + UUID.randomUUID());
        this.evaluateStrategy = new SimpleEvaluateStrategy();
    }
    public LoBot(GameSettings roomSettings, Color color, EvaluateStrategy evaluateStrategy) {
        super(roomSettings, color, "lobot-" + UUID.randomUUID());
        this.evaluateStrategy = evaluateStrategy;
    }

    @Override
    public Move getNextMove() throws ChessError {
        try {
            return minimaxRoot(2, color);
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Move minimaxRoot(int depth, Color curColor) throws ChessError, ChessException {
        //long startTime = System.currentTimeMillis();
        List<Move> moves = ms.getAllCorrectMoves(curColor);
        setTurnIntoAll(moves);
        int bestMove = -9999;
        Move bestMoveFound = null;

        for (Move move : moves) {
            int value = MoveSystem.virtualMove(move, (from, to) ->
                    minimax(depth - 1, -10000, 10000, curColor.inverse()), roomSettings.board,
                roomSettings.history);
            //System.out.println(value);
            if (value >= bestMove) {
                bestMove = value;
                bestMoveFound = move;
            }
        }
        //System.out.println('\n');

        // System.out.println(System.currentTimeMillis() - startTime);
        // System.out.println(roomSettings.board);
        System.out.println(color + " " + bestMoveFound + " " + bestMove);
        return bestMoveFound;
    };

    private int minimax(int depth, int alpha, int beta, Color curColor) throws ChessError, ChessException {
        LoBot.COUNT++;
        if (depth == 0) {
            // int k = evaluateStrategy.evaluateBoard(board, color);
            // System.out.println(k);
            //System.out.println(roomSettings.board);
            return evaluateStrategy.evaluateBoard(roomSettings.board, color);
        }

        List<Move> moves = ms.getAllCorrectMoves(curColor);

        if (moves.isEmpty() && egd.isCheck(curColor)) return color == curColor? -9999 : 9999;
        setTurnIntoAll(moves);

        if (color == curColor) {
            int bestMove = -9999;
            for (Move move : moves) {
                int newAlpha = alpha;
                int newBeta = beta;
                int cur = MoveSystem.virtualMove(move, (from, to) ->
                        minimax(depth - 1, newAlpha, newBeta, curColor.inverse()), roomSettings.board,
                    roomSettings.history);
                bestMove = Math.max(bestMove, cur);
                /*if (bestMove >= beta) {
                    return bestMove;
                }
                alpha = Math.max(alpha, bestMove);*/
            }
            return bestMove;
        } else {
            int bestMove = 9999;
            for (Move move : moves) {
                int newAlpha = alpha;
                int newBeta = beta;
                int cur = MoveSystem.virtualMove(move, (from, to) ->
                        minimax(depth - 1, newAlpha, newBeta, curColor.inverse()), roomSettings.board,
                    roomSettings.history);
                bestMove = Math.min(bestMove, cur);
                /*if (bestMove <= alpha) {
                    return bestMove;
                }
                beta = Math.min(beta, bestMove);*/
            }
            return bestMove;
        }
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
