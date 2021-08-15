package io.deeplay.qchess.nukebot.bot.searchfunc;

import io.deeplay.qchess.game.model.Move;

/**
 * Используется алгоритмами поиска, чтобы обновлять лучшую оценку хода и сам ход. Этот интерфейс
 * должны реализовывать функции поиска
 */
public interface ResultUpdater {
    /**
     * @param move лучший найденный ход
     * @param estimation его оценка
     * @param maxDepth глубина, на которой была основана оценка (чем больше, тем лучше)
     * @param moveVersion версия хода - используется, чтобы незавершенные потоки с прошлых ходов
     *     случайно не сломали текущий
     */
    void updateResult(Move move, int estimation, int maxDepth, int moveVersion);

    /**
     * @param myMoveVersion версия хода в алгоритме поиска
     * @return true, если версия хода совпадает с текущей
     */
    boolean isValidMoveVersion(int myMoveVersion);
}
