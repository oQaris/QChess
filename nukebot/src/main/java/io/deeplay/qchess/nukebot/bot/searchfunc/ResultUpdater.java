package io.deeplay.qchess.nukebot.bot.searchfunc;

import io.deeplay.qchess.game.model.Move;

/**
 * Используется алгоритмами поиска, чтобы обновлять лучшую оценку хода и сам ход. Этот интерфейс
 * должны реализовывать функции поиска
 */
@FunctionalInterface
public interface ResultUpdater {
    /**
     * @param move лучший найденный ход
     * @param estimation его оценка
     * @param maxDepth глубина, на которой была основана оценка (чем больше, тем лучше)
     */
    void updateResult(Move move, int estimation, int maxDepth);
}
