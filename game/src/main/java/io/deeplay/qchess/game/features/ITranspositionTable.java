package io.deeplay.qchess.game.features;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Color;

/** Таблица для кеширования значений */
public interface ITranspositionTable {
    /** @return true, если цвету color поставлен шах */
    boolean isCheckTo(GameSettings gs, BoardState boardState, Color color);
}
