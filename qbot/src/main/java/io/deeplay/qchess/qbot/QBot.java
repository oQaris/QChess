package io.deeplay.qchess.qbot;

import static io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QBot extends RemotePlayer {
    private static final Logger logger = LoggerFactory.getLogger(QBot.class);
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

    public QBot(GameSettings roomSettings, Color color, int searchDepth) {
        super(roomSettings, color, "minimax-bot-" + UUID.randomUUID());
        depth = searchDepth;
    }

    public QBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "minimax-bot-" + UUID.randomUUID());
        depth = 2;
    }

    @Override
    public Move getNextMove() throws ChessError {
        List<Move> topMoves = new ArrayList<>();
        // Move bestMove = null;
        int maxGrade = Integer.MIN_VALUE;
        for (Move move : ms.getAllCorrectMoves(color)) {
            int curGrade = 0;
            try {
                curGrade = ms.virtualMove(move, (from, to) -> minimaxRoot(depth, false));
            } catch (ChessException e) {
                e.printStackTrace();
            }
            if (curGrade > maxGrade) {
                maxGrade = curGrade;
                topMoves.clear();
            }
            if (curGrade >= maxGrade) topMoves.add(move);
            /*if (curGrade > maxGrade) {
                maxGrade = curGrade;
                bestMove = move;
            }*/
        }
        // todo turnIntoInQueen(bestMove);
        return topMoves.get(new Random().nextInt(topMoves.size()));
    }

    public int minimaxRoot(int depth, boolean isMyMoveNext) throws ChessError, ChessException {
        logger.debug("Минимакс стартовал с глубиной поиска: {}", depth);
        int res = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMyMoveNext);
        logger.debug("Оценка хода: {}", res);
        return res;
    }

    private int minimax(int depth, int alpha, int beta, boolean isMaximisingPlayer)
            throws ChessError, ChessException {
        logger.trace("Глубина поиска: {}", depth);
        if (depth == 0) return evaluateBoard();

        int initGrade = isMaximisingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestGrade = initGrade / 2;

        // максимальное берём из наших, минимальное - из противника
        Color curColor = isMaximisingPlayer ? color : color.inverse();

        List<Move> allMoves = ms.getAllCorrectMoves(curColor);
        if (allMoves.isEmpty() && egd.isCheck(curColor)) return initGrade;

        setTurnIntoAll(allMoves);
        for (Move move : allMoves) {
            int finalAlpha = alpha;
            int finalBeta = beta;
            int curGrade =
                    ms.virtualMove(
                            move,
                            (from, to) ->
                                    minimax(depth - 1, finalAlpha, finalBeta, !isMaximisingPlayer));

            if (isMaximisingPlayer) bestGrade = Math.max(bestGrade, curGrade);
            else bestGrade = Math.min(bestGrade, curGrade);

            // Альфа-бетта отсечение
            if (isMaximisingPlayer) alpha = Math.max(alpha, bestGrade);
            else beta = Math.min(beta, bestGrade);
            if (beta <= alpha) return bestGrade;
        }
        logger.trace("Текущая оценка доски: {}", depth);
        return bestGrade;
    }

    /** Функция оценки позиции на доске */
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

    /**
     * Заменяет в списке все ходы типа TURN_INTO на такие же ходы, но с добавленными фигурами для
     * превращения. Исходный порядок не гарантируется.
     *
     * @param moves Исходный список ходов.
     */
    private void setTurnIntoAll(List<Move> moves) {
        int originalSize = moves.size();
        for (int i = 0; i < originalSize; i++) {
            Move move = moves.get(i);
            MoveType type = move.getMoveType();
            if (type == MoveType.TURN_INTO || type == MoveType.TURN_INTO_ATTACK) {
                moves.get(i).setTurnInto(FigureType.QUEEN);
                moves.add(new Move(move, FigureType.KNIGHT));
            }
        }
    }
}