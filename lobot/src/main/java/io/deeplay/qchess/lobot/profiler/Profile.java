package io.deeplay.qchess.lobot.profiler;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.model.BoardState;
import io.deeplay.qchess.game.model.Move;
import java.util.HashMap;
import java.util.Map;

public class Profile {
    private final Map<BoardState, Distribution> repository;

    public Profile() {
        repository = new HashMap<>();
    }

    /**
     * Добавляет запись в профиль. Если такого ключа не было создаётся новая запись<br>
     * Иначе обновляется значение о ключу.
     * @param gs из неё мы получим состояние борды, которое используем как ключ
     * @param move ход который добавится в значение
     */
    public void add(final GameSettings gs, final Move move) {
        final BoardState bs = gs.history.getLastBoardState();
        final Distribution distribution = repository.getOrDefault(bs, defaultBoardState());
        repository.put(bs, distribution.setOrAddMove(move));
    }

    /**
     * @param gs по которому будет вернётся распределение оценок ходов
     * @return распределение оценок ходов для этого GameSetting
     */
    public Distribution get(final GameSettings gs) {
        return repository.getOrDefault(gs.history.getLastBoardState(), defaultBoardState());
    }

    /**
     * @return распределение для GameSetting которого нет в профиле
     */
    private Distribution defaultBoardState() {
        return new Distribution();
    }
}
