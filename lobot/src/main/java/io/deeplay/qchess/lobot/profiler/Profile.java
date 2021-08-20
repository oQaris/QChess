package io.deeplay.qchess.lobot.profiler;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Profile {
    private final Map<String, Distribution> repository;
    private final String name;

    public Profile() {
        this("general");
    }

    public Profile(final String name) {
        repository = new HashMap<>();
        this.name = name;
    }

    /**
     * Добавляет запись в профиль. Если такого ключа не было создаётся новая запись<br>
     * Иначе обновляется значение о ключу.
     * @param fen нотация Форсайта-Эдвардса
     * @param move ход который добавится в значение
     */
    public void add(final String fen, final Move move) {
        final Distribution distribution = repository.getOrDefault(fen, defaultBoardState());
        repository.put(fen, distribution.setOrAddMove(move));
    }

    /**
     * Добавляет запись в профиль. Если такого ключа не было создаётся новая запись<br>
     * Иначе обновляется значение о ключу.
     * @param gs из неё мы получим состояние борды, которое используем как ключ
     * @param move ход который добавится в значение
     */
    public void add(final GameSettings gs, final Move move) throws ChessError {
        final String fen = gs.history.getBoardToStringForsythEdwards();
        add(fen, move);
    }

    /**
     * @param gs по которому будет вернётся распределение оценок ходов
     * @return распределение оценок ходов для этого GameSetting
     */
    public Distribution get(final GameSettings gs) throws ChessError {
        final String fen = gs.history.getBoardToStringForsythEdwards();
        return repository.getOrDefault(fen, defaultBoardState());
    }

    /**
     * @return распределение для GameSetting которого нет в профиле
     */
    private Distribution defaultBoardState() {
        return new Distribution();
    }

    public String getName() {
        return name;
    }

    // todo посмотреть на json, tostring для distr
    public void save(final BufferedWriter bw) throws IOException {
        for(final String key : repository.keySet()) {
            bw.write(key);
            bw.write(repository.get(key).toString());
        }
    }
}
