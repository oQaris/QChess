package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.model.figures.Queen;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinimaxBot extends Player {
    protected static final Logger logger = LoggerFactory.getLogger(MinimaxBot.class);
    protected static final Map<FigureType, Integer> grades = preparedGrades();
    private final int depth;

    public MinimaxBot(GameSettings roomSettings, Color color, int searchDepth) {
        super(roomSettings, color);
        depth = searchDepth;
    }

    private static Map<FigureType, Integer> preparedGrades() {
        Map<FigureType, Integer> res = new EnumMap<>(FigureType.class);
        res.put(FigureType.PAWN, 1);
        res.put(FigureType.KNIGHT, 3);
        res.put(FigureType.BISHOP, 3);
        res.put(FigureType.ROOK, 5);
        res.put(FigureType.QUEEN, 9);
        res.put(FigureType.KING, 90);
        return res;
    }

    @Override
    public Move getNextMove() throws ChessError {
        List<Move> topMoves = new ArrayList<>();
        int maxGrade = Integer.MIN_VALUE;
        for (Move move : ms.getAllCorrectMoves(color)) {
            int curGrade = 0;
            try {
                curGrade = minimax(depth, true);
            } catch (ChessException e) {
                e.printStackTrace();
            }
            if (curGrade > maxGrade) {
                maxGrade = curGrade;
                topMoves.clear();
            }
            if (curGrade >= maxGrade) topMoves.add(move);
        }
        Move move = topMoves.get(new Random().nextInt(topMoves.size()));
        turnIntoInQueen(move);
        return move;
    }

    public int minimax(int depth, boolean isMaximisingPlayer) throws ChessError, ChessException {
        if (depth == 0) return evaluateBoard();
        AtomicInteger bestGrade =
                new AtomicInteger(isMaximisingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        // максимальное берём из наших, минимальное - из противника
        for (Move move : ms.getAllCorrectMoves(isMaximisingPlayer ? color : color.inverse())) {
            bestGrade.set(
                    ms.virtualMove(
                            move,
                            (from, to) -> {
                                // todo что то сделать с turnInto
                                int newGrade = minimax(depth - 1, !isMaximisingPlayer);
                                if (isMaximisingPlayer) return Math.max(bestGrade.get(), newGrade);
                                else return Math.min(bestGrade.get(), newGrade);
                            }));
        }
        return bestGrade.get();
    }

    public int evaluateBoard() {
        int grade = 0;
        for (Figure figure : board.getAllFigures()) {
            int coef = (figure.getColor() == color ? 1 : -1);
            grade += coef * grades.get(figure.getType());
        }
        return grade;
    }

    protected void turnIntoInQueen(Move move) {
        if (move.getMoveType() == MoveType.TURN_INTO)
            move.setTurnInto(new Queen(color, move.getTo()));
    }
}
