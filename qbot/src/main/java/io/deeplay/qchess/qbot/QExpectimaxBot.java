package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.qbot.profile.ParserKt.fill;
import static io.deeplay.qchess.qbot.profile.ParserKt.getProfilesMap;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector.EndGameType;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.qbot.profile.Profile;
import io.deeplay.qchess.qbot.strategy.PestoStrategy;
import io.deeplay.qchess.qbot.strategy.Strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QExpectimaxBot extends QBot {
    private final Profile enemyProfile;

    protected QExpectimaxBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy) {
        this(roomSettings, color, searchDepth, strategy, null);
    }

    protected QExpectimaxBot(
            final GameSettings roomSettings,
            final Color color,
            final int searchDepth,
            final Strategy strategy,
            final String enemyProfileStr) {
        super(roomSettings, color, searchDepth, strategy, "ExpectiMaxBot");
        if (enemyProfileStr != null) {
            if (getProfilesMap().isEmpty()) fill();
            enemyProfile = getProfilesMap().get(enemyProfileStr);
            Objects.requireNonNull(enemyProfile, "Профиль " + enemyProfileStr + " не найден!");
        } else enemyProfile = new Profile();
    }

    protected QExpectimaxBot(
            final GameSettings roomSettings, final Color color, final int searchDepth) {
        this(roomSettings, color, searchDepth, new PestoStrategy());
    }

    @Override
    public List<Move> getTopMoves() throws ChessError {
        final List<Move> topMoves = new ArrayList<>();
        int maxGrade = color == Color.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        final List<Move> allMoves = ms.getAllPreparedMoves(color);
        for (final Move move : allMoves) {
            final int curGrade = minimaxRootWithVirtualMove(move);
            if (color == Color.WHITE) {
                if (curGrade > maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
                }
                if (curGrade >= maxGrade) {
                    topMoves.add(move);
                }
            } else {
                if (curGrade < maxGrade) {
                    maxGrade = curGrade;
                    topMoves.clear();
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
        ms.move(move);
        final int res = expectimax(depth, color.inverse());
        ms.undoMove();
        return res;
    }

    private int expectimax(final int curDepth, final Color curColor) throws ChessError {
        if (curDepth == 0) return strategy.evaluateBoard(board);

        final List<Move> moves = ms.getAllPreparedMoves(curColor);
        final EndGameType endGameType = egd.updateEndGameStatus(moves, curColor);
        egd.revertEndGameStatus();
        if (endGameType != EndGameType.NOTHING)
            return strategy.gradeIfTerminalNode(endGameType, curDepth);

        if (color == curColor) {
            int bestMoveValue = Integer.MIN_VALUE;
            for (final Move move : moves) {
                ms.move(move);
                final int currentValue = expectimax(curDepth - 1, curColor.inverse());
                ms.undoMove();
                bestMoveValue = Math.max(bestMoveValue, currentValue);
            }
            return bestMoveValue;
        } else {
            double sum = 0;
            for (final Move move : moves) {
                ms.move(move);
                final int currentValue = expectimax(curDepth - 1, curColor.inverse());
                ms.undoMove();
                Double coef;
                final String fen = history.getBoardToStringForsythEdwards();
                final Map<Move, Double> probs = enemyProfile.movesWithProb(fen);
                if (probs.isEmpty()) coef = 1. / moves.size();
                else {
                    coef = probs.get(move);
                    if (coef == null) coef = 0.0;
                }
                sum += currentValue * coef;
            }
            return (int) Math.round(sum);
        }
    }
}
