package io.deeplay.qchess.nnnbot.bot;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NNNBot extends RemotePlayer {

    public static final int MAX_DEPTH = 2;
    private static final Logger logger = LoggerFactory.getLogger(NNNBot.class);
    private static final EstimatedBoard theWorstEstimation = new EstimatedBoard(0, -100);

    private /*final*/ Map<BoardState, EstimatedBoard> cachedEstimatedStates;

    @Deprecated private int id;
    @Deprecated private int getCacheVirt;
    @Deprecated private int getCache;
    @Deprecated private int NOTgetCacheVirt;
    @Deprecated private int NOTgetCache;

    public NNNBot(GameSettings roomSettings, Color color) {
        super(roomSettings, color, "n-nn-bot-" + UUID.randomUUID());
        // cachedEstimatedStates = new HashMap<>(/*TODO: вычислять размер по MAX_DEPTH*/ );
    }

    /** @deprecated Используется для тестов, чтобы найти оптимальный размер кеша */
    @Deprecated
    public void setCacheSize(int cacheSize) {
        cachedEstimatedStates = new HashMap<>(cacheSize);
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

    @Override
    public Move getNextMove() throws ChessError {
        // TODO: запуск потока для симуляции ходов, если еще не запущен
        return getTheBestMove(roomSettings, color, MAX_DEPTH);
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
            double optEstimation, Move move, GameSettings gs, Color color, int depth) {
        EstimatedBoard estimatedState =
                cachedEstimatedStates.getOrDefault(
                        gs.history.getLastBoardState(), theWorstEstimation);
        if (estimatedState.maxEstimatedDepth >= depth) {
            logger.trace("Взято лучшее состояние из кеша для {}", color);
            ++getCache;
            return estimatedState.estimation;
        }
        logger.trace("Не найдено лучшее состояние в кеше для {}", color);
        ++NOTgetCache;

        if (--depth < 0) return getEvaluation(gs.board, color);

        try {
            gs.moveSystem.move(move);

            estimatedState =
                    cachedEstimatedStates.getOrDefault(
                            gs.history.getLastBoardState(), theWorstEstimation);
            if (estimatedState.maxEstimatedDepth < depth) {
                logger.trace("Не найдено оптимальное состояние в кеше для {}", color);
                ++NOTgetCacheVirt;
                // Если еще нет лучшей оценки
                gs.moveSystem.move(getTheBestMove(gs, color.inverse(), depth));
                double estimation = getTheBestEvaluation(gs, color, depth);
                if (estimation > optEstimation) optEstimation = estimation;
                gs.moveSystem.undoMove();

                cachedEstimatedStates.put(
                        gs.history.getLastBoardState(), new EstimatedBoard(optEstimation, depth));
                // Если уже есть лучшая оценка
            } else if (estimatedState.estimation > optEstimation) {
                optEstimation = estimatedState.estimation;
                logger.trace("Взято оптимальное состояние из кеша для {}", color);
                ++getCacheVirt;
            }

            gs.moveSystem.undoMove();

        } catch (ChessError e) {
            // Только второй ход может кинуть исключение => нет ходов у противника
            optEstimation = 10000;
            cachedEstimatedStates.put(
                    gs.history.getLastBoardState(), new EstimatedBoard(optEstimation, depth));
            try {
                // Отмена первого хода
                gs.moveSystem.undoMove();
            } catch (ChessError ignore) {
                logger.error("Невозможная ошибка при отмене хода");
            }
            logger.debug("Надена безвыходная позиция у оппонента");
        }

        return optEstimation;
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
        EstimatedBoard estimatedState =
                cachedEstimatedStates.getOrDefault(
                        gs.history.getLastBoardState(), theWorstEstimation);
        if (estimatedState.maxEstimatedDepth >= depth) return estimatedState.estimation;

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

        return optEstimation * getEvaluation(gs.board, color);
    }

    private double getEvaluation(Board board, Color color) {
        // Лучшая оценка всех времен и народов
        return new Random().nextDouble();
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
