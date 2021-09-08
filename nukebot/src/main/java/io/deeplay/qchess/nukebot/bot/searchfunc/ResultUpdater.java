package io.deeplay.qchess.nukebot.bot.searchfunc;

import io.deeplay.qchess.game.model.Move;

/**
 * Используется алгоритмами поиска, чтобы обновлять лучшую оценку хода и сам ход. Этот интерфейс
 * должны реализовывать функции поиска
 */
public interface ResultUpdater {
    /**
     * Обновляет текущий лучший ход
     *
     * @param move лучший найденный ход
     * @param estimation его оценка
     * @param startDepth глубина, с которой стартовал алгоритм поиска (чем больше, тем лучше)
     * @param moveVersion версия хода - используется, чтобы незавершенные потоки с прошлых ходов
     *     случайно не сломали текущий
     */
    void updateResult(Move move, int estimation, int startDepth, int moveVersion);

    /**
     * Чем чаще проверяется валидность версии, тем лучше
     *
     * @param myMoveVersion версия хода в алгоритме поиска
     * @return true, если версия хода НЕ совпадает с текущей
     */
    boolean isInvalidMoveVersion(int myMoveVersion);
}
