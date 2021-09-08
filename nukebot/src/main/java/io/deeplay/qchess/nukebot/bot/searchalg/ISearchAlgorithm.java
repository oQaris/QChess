package io.deeplay.qchess.nukebot.bot.searchalg;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import java.util.List;

public interface ISearchAlgorithm {

    /**
     * Обновляет текущий лучший ход
     *
     * @param estimation текущая оценка доски
     */
    void updateResult(final int estimation);

    /** @return true, если версия хода НЕ совпадает с текущей */
    boolean isInvalidMoveVersion();

    /** Делает игровой ход */
    void makeMove(Move move) throws ChessError;

    /**
     * Делает игровой ход
     *
     * @param useHistoryRecord записывать ли ход в историю
     * @param changeMoveSideInRecord изменять ли сторону цвета в истории
     */
    void makeMove(Move move, boolean useHistoryRecord, boolean changeMoveSideInRecord)
            throws ChessError;

    /** Отменяет последний игровой ход */
    void undoMove() throws ChessError;

    /**
     * Отменяет последний игровой ход
     *
     * @param useHistoryRecord брать ли последний ход из истории или взять его из кеша
     */
    void undoMove(boolean useHistoryRecord) throws ChessError;

    /** @return все легальные ходы для цвета color */
    List<Move> getLegalMoves(Color color) throws ChessError;

    /** Сортирует ходы на основе какой-либо эвристике */
    default void prioritySort(final List<Move> allMoves) {}

    /** @return true, если текущая нода - терминальная */
    boolean isTerminalNode(List<Move> allMoves);

    /** @return true если цвету color поставлен шах */
    boolean isCheck(Color color);

    /** @return true если цвету color поставлен пат (нет легальных ходов) */
    boolean isStalemate(Color color);

    /** @return true, если текущее состояние - ничья */
    boolean isDraw();
}
