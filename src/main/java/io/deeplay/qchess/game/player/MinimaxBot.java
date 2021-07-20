package io.deeplay.qchess.game.player;

import static io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinimaxBot extends Player {
    private static final Logger logger = LoggerFactory.getLogger(MinimaxBot.class);
    private static final Map<FigureType, Integer[][]> grades;
    private static final Integer[][] pawnEval = {
        {20, 20, 20, 20, 20, 20, 20, 20},
        {30, 30, 30, 30, 30, 30, 30, 30},
        {22, 22, 24, 26, 26, 24, 22, 22},
        {21, 21, 22, 25, 25, 22, 21, 21},
        {20, 20, 20, 24, 24, 20, 20, 20},
        {21, 19, 18, 20, 20, 18, 19, 21},
        {21, 22, 22, 16, 16, 22, 22, 21},
        {20, 20, 20, 20, 20, 20, 20, 20},
    };
    private static final Integer[][] knightEval = {
        {50, 52, 54, 54, 54, 54, 52, 50},
        {52, 56, 60, 60, 60, 60, 56, 52},
        {54, 60, 62, 63, 63, 62, 60, 54},
        {54, 61, 63, 64, 64, 63, 61, 54},
        {54, 60, 63, 64, 64, 63, 60, 54},
        {54, 61, 62, 63, 63, 62, 61, 54},
        {52, 56, 60, 61, 61, 60, 56, 52},
        {50, 52, 54, 54, 54, 54, 52, 50},
    };
    private static final Integer[][] bishopEval = {
        {56, 58, 58, 58, 58, 58, 58, 56},
        {58, 60, 60, 60, 60, 60, 60, 58},
        {58, 60, 61, 62, 62, 61, 60, 58},
        {58, 61, 61, 62, 62, 61, 61, 58},
        {58, 60, 62, 62, 62, 62, 60, 58},
        {58, 62, 62, 62, 62, 62, 62, 58},
        {58, 61, 60, 60, 60, 60, 61, 58},
        {56, 58, 58, 58, 58, 58, 58, 56},
    };
    private static final Integer[][] rookEval = {
        {100, 100, 100, 100, 100, 100, 100, 100},
        {101, 102, 102, 102, 102, 102, 102, 101},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {99, 100, 100, 100, 100, 100, 100, 99},
        {100, 100, 100, 101, 101, 100, 100, 100},
    };
    private static final Integer[][] queenEval = {
        {176, 178, 178, 179, 179, 178, 178, 176},
        {178, 180, 180, 180, 180, 180, 180, 178},
        {178, 180, 181, 181, 181, 181, 180, 178},
        {179, 180, 181, 181, 181, 181, 180, 179},
        {179, 180, 181, 181, 181, 181, 180, 179},
        {178, 181, 181, 181, 181, 181, 180, 178},
        {178, 180, 181, 180, 180, 180, 180, 178},
        {176, 178, 178, 179, 179, 178, 178, 176},
    };
    private static final Integer[][] kingEval = {
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1794, 1792, 1792, 1790, 1790, 1792, 1792, 1794},
        {1796, 1794, 1794, 1792, 1792, 1794, 1794, 1796},
        {1798, 1796, 1796, 1796, 1796, 1796, 1796, 1798},
        {1804, 1804, 1800, 1800, 1800, 1800, 1804, 1804},
        {1804, 1806, 1802, 1800, 1800, 1802, 1806, 1804},
    };

    static {
        Map<FigureType, Integer[][]> res = new EnumMap<>(FigureType.class);
        res.put(FigureType.PAWN, pawnEval);
        res.put(FigureType.KNIGHT, knightEval);
        res.put(FigureType.BISHOP, bishopEval);
        res.put(FigureType.ROOK, rookEval);
        res.put(FigureType.QUEEN, queenEval);
        res.put(FigureType.KING, kingEval);
        grades = Collections.unmodifiableMap(res);
    }

    private final int depth;

    public MinimaxBot(GameSettings roomSettings, Color color, int searchDepth) {
        super(roomSettings, color);
        depth = searchDepth;
    }

    @Override
    public Move getNextMove() throws ChessError {
        //List<Move> topMoves = new ArrayList<>();
        Move bestMove = null;
        int maxGrade = Integer.MIN_VALUE;
        for (Move move : ms.getAllCorrectMoves(color)) {
            AtomicInteger curGrade = new AtomicInteger();
            try {
                curGrade.set(ms.virtualMove(move, (from, to) -> minimaxRoot(depth, false)));
            } catch (ChessException e) {
                e.printStackTrace();
            }
            /*if (curGrade.get() > maxGrade) {
                maxGrade = curGrade.get();
                topMoves.clear();
            }
            if (curGrade.get() >= maxGrade) topMoves.add(move);*/
            if (curGrade.get() > maxGrade) {
                maxGrade = curGrade.get();
                bestMove = move;
            }
        }
        //Move move = topMoves.get(new Random().nextInt(topMoves.size()));
        turnIntoInQueen(bestMove);
        return bestMove;
    }

    public int minimaxRoot(int depth, boolean isMaximisingPlayer)
            throws ChessError, ChessException {
        logger.debug("Минимакс стартовал с глубиной поиска: {}", depth);
        int res = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximisingPlayer);
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    private int minimax(int depth, int alpha, int beta, boolean isMaximisingPlayer)
            throws ChessError, ChessException {
        logger.trace("Глубина поиска: {}", depth);
        if (depth == 0) return evaluateBoard();
        AtomicInteger bestGrade =
                new AtomicInteger(isMaximisingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        // максимальное берём из наших, минимальное - из противника
        for (Move move : ms.getAllCorrectMoves(isMaximisingPlayer ? color : color.inverse())) {
            turnIntoInQueen(move);
            bestGrade.set(
                    ms.virtualMove(
                            move,
                            (from, to) -> {
                                int newGrade = minimax(depth - 1, alpha, beta, !isMaximisingPlayer);
                                if (isMaximisingPlayer) return Math.max(bestGrade.get(), newGrade);
                                else return Math.min(bestGrade.get(), newGrade);
                            }));
            int finalAlpha = alpha;
            int finalBeta = beta;

            if (isMaximisingPlayer) finalAlpha = Math.max(alpha, bestGrade.get());
            else finalBeta = Math.min(beta, bestGrade.get());

            if (finalBeta <= finalAlpha) return bestGrade.get();
        }
        logger.trace("Текущая оценка хода: {}", depth);
        return bestGrade.get();
    }

    public int evaluateBoard() {
        int grade = 0;
        for (Figure figure : board.getAllFigures()) {
            int absX = figure.getCurrentPosition().getColumn();
            int y = figure.getCurrentPosition().getRow();
            // разворачиваем массив ценностей для чёрных
            int absY = figure.getColor() == Color.BLACK ? STD_BOARD_SIZE - 1 - y : y;
            int coef = figure.getColor() == color ? 1 : -1;

            grade += coef * grades.get(figure.getType())[absY][absX];
        }
        logger.trace("Оценка доски: {}", grade);
        return grade;
    }

    protected void turnIntoInQueen(Move move) {
        if (move.getMoveType() == MoveType.TURN_INTO) move.setTurnInto(FigureType.QUEEN);
    }
}
