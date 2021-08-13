package io.deeplay.qchess.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RatingELO {
    private final Path toReplace = Paths.get("elo_rating.txt");
    private final Path newContents = toReplace.resolveSibling("temp_elo.json");
    private Map<String, Long> elo = new ConcurrentHashMap<>();

    public void pullELO() throws IOException {
        try (final BufferedReader reader =
                Files.newBufferedReader(toReplace, StandardCharsets.UTF_8)) {
            elo = new Gson().fromJson(reader, new TypeToken<Map<String, Long>>() {}.getType());
        }
    }

    /**
     * @param firstPlayerName Уникальное имя первого игрока
     * @param secondPlayerName Уникальное имя второго игрока
     * @param sA Фактически набранное игроком A количество очков (1 очко за победу, 0.5 — за ничью и
     *     0 — за поражение)
     */
    public void updateELO(
            final String firstPlayerName, final String secondPlayerName, final double sA) {
        // рейтинг игрока A
        final long rA = elo.getOrDefault(firstPlayerName, 100L);
        // рейтинг игрока B
        final long rB = elo.getOrDefault(secondPlayerName, 100L);

        // ожидаемое количество очков, которое наберёт игрок A в партии с B
        final double eA = 1. / (1. + Math.pow(10., (rB - rA) / 400.));

        // коэффициент, значение которого равно 10 для сильнейших игроков (рейтинг 2400 и выше),
        // 20 — для игроков с рейтингом меньше, чем 2400 и 40 — для новых игроков (первые 30 партий
        // с момента получения рейтинга ФИДЕ), а также для игроков рейтинг которы ниже 2300
        final double k;
        if (rA >= 2400 /*и не менее 30 игр*/) k = 10;
        else if (rA < 2400) k = 20;
        else k = 40 /*меньше 30 игр и ниже 2300*/;

        // новый рейтинг игрока A
        final long newRa = Math.round(rA + k * (sA - eA));

        elo.put(firstPlayerName, newRa);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        elo.forEach(
                (name, rt) ->
                        sb.append(name).append(" -> ").append(rt).append(System.lineSeparator()));
        return sb.toString();
    }

    public void saveELO() throws IOException {
        try (final BufferedWriter writer =
                Files.newBufferedWriter(
                        newContents,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
            new Gson().toJson(elo, writer);
        }
        Files.move(
                newContents,
                toReplace,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }
}
