package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NNNBot extends RemotePlayer {

    public static final int MAX_DEPTH = 2;

    private static final Logger logger = LoggerFactory.getLogger(NNNBot.class);

    private static final EstimatedBoard theWorstEstimation = new EstimatedBoard(0, -100);

    private int maxCacheSize;
    private /*final*/ Map<Integer, EstimatedBoard> cachedEstimatedStates;

    @Deprecated private int id;

    @Deprecated private boolean includeCache;
    @Deprecated private int getCacheVirt;
    @Deprecated private int getCache;
    @Deprecated private int NOTgetCacheVirt;
    @Deprecated private int NOTgetCache;
    @Deprecated private int putCache;

    @Deprecated private int moveCount;
    @Deprecated private double timeToThink;
    @Deprecated private double maxTimeToThink = Double.MIN_VALUE;
    @Deprecated private double minTimeToThink = Double.MAX_VALUE;

    public NNNBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "n-nn-bot-" + UUID.randomUUID());
        // cachedEstimatedStates = new HashMap<>(/*TODO: вычислять размер по MAX_DEPTH*/ );
    }

    /** @deprecated Используется для тестов, чтобы найти оптимальный размер кеша */
    @Deprecated
    public void setCacheSize(int cacheSize) {
        this.maxCacheSize = cacheSize * 10;
        cachedEstimatedStates = new HashMap<>(cacheSize);
    }

    @Deprecated
    public void includeCache() {
        includeCache = true;
    }

    @Deprecated
    public int getId() {
        return id;
    }

    @Deprecated
    public void setId(int id) {
        this.id = id;
    }

    @Deprecated
    public int getGetCache() {
        return getCache;
    }

    @Deprecated
    public int getGetCacheVirt() {
        return getCacheVirt;
    }

    @Deprecated
    public int getNOTgetCache() {
        return NOTgetCache;
    }

    @Deprecated
    public int getNOTgetCacheVirt() {
        return NOTgetCacheVirt;
    }

    @Deprecated
    public double getAverageTimeToThink() {
        return timeToThink / moveCount;
    }

    @Deprecated
    public int getMoveCount() {
        return moveCount;
    }

    @Deprecated
    public double getMaxTimeToThink() {
        return maxTimeToThink;
    }

    @Deprecated
    public double getMinTimeToThink() {
        return minTimeToThink;
    }

    @Deprecated
    public int getPutCache() {
        return putCache;
    }

    @Override
    public Move getNextMove() throws ChessError {
        // TODO: запуск потока для симуляции ходов, если еще не запущен
        ++moveCount;
        long startTime = System.currentTimeMillis();
        Move move = getTheBestMove(roomSettings, color, MAX_DEPTH);
        double time = (System.currentTimeMillis() - startTime) / 1000.;
        timeToThink += time;
        if (time < minTimeToThink) minTimeToThink = time;
        if (time > maxTimeToThink) maxTimeToThink = time;
        return move;
    }

    private Move getTheBestMove(GameSettings gs, Color color, int depth) throws ChessError {
        Move theBestMove = null;
        double optEstimation = -1;
        double estimation = 0;

        // TODO: запуск нескольких потоков для начальной глубины
        for (Move move : gs.moveSystem.getAllCorrectMoves(color)) {
            if (move.getMoveType() == MoveType.TURN_INTO
                    || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
                move.setTurnInto(FigureType.QUEEN);
                estimation =
                        getTheBestEvaluationAfterVirtualMove(estimation, move, gs, color, depth);
                if (estimation > optEstimation) {
                    theBestMove = move;
                    optEstimation = estimation;
                }

                move.setTurnInto(FigureType.KNIGHT);
            }
            estimation = getTheBestEvaluationAfterVirtualMove(estimation, move, gs, color, depth);
            if (estimation > optEstimation) {
                theBestMove = move;
                optEstimation = estimation;
            }
        }

        return theBestMove;
    }

    private double getTheBestEvaluationAfterVirtualMove(
            double optEstimation, Move move, GameSettings gs, Color color, int depth)
            throws ChessError {
        EstimatedBoard estimatedState = null;
        if (includeCache) {
            estimatedState =
                    cachedEstimatedStates.getOrDefault(
                            gs.history.getLastRecord(), theWorstEstimation);
            if (estimatedState.maxEstimatedDepth + 1 >= depth) {
                ++getCache;
                return estimatedState.estimation;
            }
            if (cachedEstimatedStates.remove(gs.history.getLastRecord()) != null) ++NOTgetCache;
        }

        if (--depth < 0) return getEvaluation(gs, color);

        try {
            // Мой ход
            gs.moveSystem.move(move);
        } catch (ChessError e) {
            logger.error("Непредвиденная ошибка при выполнении хода: {}", e.getMessage());
            throw e;
        }

        try {
            if (includeCache)
                estimatedState =
                        cachedEstimatedStates.getOrDefault(
                                gs.history.getLastRecord(), theWorstEstimation);
            if (!includeCache || estimatedState.maxEstimatedDepth + 1 < depth) {
                if (includeCache
                        && cachedEstimatedStates.remove(gs.history.getLastRecord()) != null)
                    ++NOTgetCacheVirt;
                // Если еще нет лучшей оценки
                gs.moveSystem.move(getTheBestMoveForOpponent(gs, color.inverse(), depth));
                double estimation = getTheBestEvaluation(gs, color, depth);
                if (estimation > optEstimation) optEstimation = estimation;
                gs.moveSystem.undoMove();

                if (includeCache && cachedEstimatedStates.size() < maxCacheSize) {
                    cachedEstimatedStates.put(
                            gs.history.getLastRecord(), new EstimatedBoard(optEstimation, depth));
                    ++putCache;
                }
                // Если уже есть лучшая оценка
            } else if (estimatedState.estimation > optEstimation) {
                optEstimation = estimatedState.estimation;
                ++getCacheVirt;
            }
        } catch (ChessError e) {
            // Только второй ход может кинуть исключение => нет ходов у противника
            optEstimation = 10000;
            if (includeCache && cachedEstimatedStates.size() < maxCacheSize) {
                cachedEstimatedStates.put(
                        gs.history.getLastRecord(), new EstimatedBoard(optEstimation, depth));
                ++putCache;
            }
            logger.debug("Надена безвыходная позиция у оппонента");
        }

        try {
            // Отмена первого (моего) хода
            gs.moveSystem.undoMove();
        } catch (ChessError e) {
            logger.error("Непредвиденная ошибка при отмене хода: {}", e.getMessage());
            throw e;
        }

        return optEstimation;
    }

    private Move getTheBestMoveForOpponent(GameSettings gs, Color color, int depth)
            throws ChessError {
        // return getTheBestMove(gs, color.inverse(), depth);
        return getTheBestMoveGreedy(gs, color);
    }

    private Move getTheBestMoveGreedy(GameSettings gs, Color color) throws ChessError {
        return getTheBestMove(gs, color, 0);
    }

    /**
     * Вычисление лучшей стоимости доски:
     *
     * <p>1. Пройти по всем возможным ходам и виртуально походить
     *
     * <p>2. Для каждого моего хода походить лучшим ходом противника
     *
     * <p>3. Вычислить лучшую стоимость доски для глубины (depth - 1) и уточнить оценку стоимостью
     * getEvaluation(текущей доски)
     */
    private double getTheBestEvaluation(GameSettings gs, Color color, int depth) throws ChessError {
        if (includeCache) {
            EstimatedBoard estimatedState =
                    cachedEstimatedStates.getOrDefault(
                            gs.history.getLastRecord(), theWorstEstimation);
            if (estimatedState.maxEstimatedDepth + 1 >= depth) {
                ++getCache;
                return estimatedState.estimation;
            }
            if (cachedEstimatedStates.remove(gs.history.getLastRecord()) != null) ++NOTgetCache;
        }

        double optEstimation = 0;

        for (Move move : gs.moveSystem.getAllCorrectMoves(color)) {
            if (move.getMoveType() == MoveType.TURN_INTO
                    || move.getMoveType() == MoveType.TURN_INTO_ATTACK) {
                move.setTurnInto(FigureType.QUEEN);
                optEstimation =
                        getTheBestEvaluationAfterVirtualMove(optEstimation, move, gs, color, depth);

                move.setTurnInto(FigureType.KNIGHT);
            }
            optEstimation =
                    getTheBestEvaluationAfterVirtualMove(optEstimation, move, gs, color, depth);
        }

        return optEstimation * getEvaluation(gs, color);
    }

    private double getEvaluation(GameSettings gs, Color color) throws ChessError {
        // Лучшая оценка всех времен и народов
        return gs.moveSystem.getAllCorrectMoves(color).size() / 432.;
    }

    private static class EstimatedBoard {

        public double estimation;
        public double maxEstimatedDepth;

        public EstimatedBoard(double estimation, double maxEstimatedDepth) {
            this.estimation = estimation;
            this.maxEstimatedDepth = maxEstimatedDepth;
        }
    }
}
