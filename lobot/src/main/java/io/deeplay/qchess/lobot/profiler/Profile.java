package io.deeplay.qchess.lobot.profiler;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

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
        final Distribution distribution = repository.getOrDefault(fen, defaultFEN());
        if(distribution.isEmpty()) {
            repository.put(fen, distribution);
        }
        distribution.setOrAddMove(move);
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
        return repository.getOrDefault(fen, defaultFEN());
    }

    /**
     * @return распределение для GameSetting которого нет в профиле
     */
    private Distribution defaultFEN() {
        return new Distribution();
    }

    public String getName() {
        return name;
    }

    public void save(final BufferedWriter bw) throws IOException {
        final List<String> list = new ArrayList<>(repository.keySet());
        list.sort((o1, o2) -> {
            final int i1 = FENService.getFiguresCount(o1);
            final int i2 = FENService.getFiguresCount(o2);
            return -Integer.compare(i1, i2);
        });
        for(final String key : list) {
            bw.write(convertProfileRowToString(key));
            bw.write(System.lineSeparator());
        }
    }

    private String convertProfileRowToString(final String fen) {
        final StringBuilder sb = new StringBuilder();
        final Distribution distribution = repository.get(fen);
        sb.append(FENService.getFiguresCount(fen)).append(" | ");
        sb.append(fen).append(" | ");
        sb.append(distribution);
        return sb.toString();
    }
}
