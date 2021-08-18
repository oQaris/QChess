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

    public static final long START_ELO = 1000L;
    public static final long MIN_ELO = 100L;
    private final Path toReplace = Paths.get("elo_rating.txt");
    private final Path newContents = toReplace.resolveSibling("temp_elo.json");
    private final Gson gson = new Gson();
    private Map<String, Long> elo = new ConcurrentHashMap<>();

    public void pullELO() throws IOException {
        if (toReplace.toFile().isFile()) {
            try (final BufferedReader reader =
                    Files.newBufferedReader(toReplace, StandardCharsets.UTF_8)) {
                elo =
                        gson.fromJson(
                                reader,
                                new TypeToken<ConcurrentHashMap<String, Long>>() {}.getType());
            }
        }
    }

    /**
     * @param firstPlayerName Уникальное имя первого игрока
     * @param secondPlayerName Уникальное имя второго игрока
     * @param pointsForFirstPlayer Фактически набранное игроком A количество очков (1 очко за
     *     победу, 0.5 — за ничью и 0 — за поражение)
     */
    public void updateELO(
            final String firstPlayerName,
            final String secondPlayerName,
            final double pointsForFirstPlayer) {

        final long ratingFirst = elo.getOrDefault(firstPlayerName, START_ELO);
        final long ratingSecond = elo.getOrDefault(secondPlayerName, START_ELO);

        if (pointsForFirstPlayer < 0 && pointsForFirstPlayer > 1)
            throw new IllegalArgumentException("Некорректное значение фактора");

        final long newRa = calculateElo(ratingFirst, ratingSecond, pointsForFirstPlayer);
        final long newRb =
                calculateElo(ratingSecond, ratingFirst, inverseFactor(pointsForFirstPlayer));

        elo.put(firstPlayerName, Math.max(newRa, MIN_ELO));
        elo.put(secondPlayerName, Math.max(newRb, MIN_ELO));
    }

    /** @return Новый рейтинг игрока A */
    private long calculateElo(final long ratingA, final long ratingB, final double factorA) {
        // ожидаемое количество очков, которое наберёт игрок A в партии с B
        final double eA = 1 / (1 + Math.pow(10, (ratingB - ratingA) / 400.));

        // коэффициент, значение которого равно 10 для сильнейших игроков (рейтинг 2400 и выше),
        // 20 — для игроков с рейтингом меньше, чем 2400 и 40 — для новых игроков (первые 30 партий
        // с момента получения рейтинга ФИДЕ), а также для игроков рейтинг которы ниже 2300
        final double k;
        if (ratingA >= 2400 /*и не менее 30 игр*/) {
            k = 10;
        } else if (ratingA >= 2300) {
            k = 20;
        } else {
            k = 40 /*и меньше 30 игр*/;
        }

        // новый рейтинг игрока A
        return Math.round(ratingA + k * (factorA - eA));
    }

    private double inverseFactor(final double factor) {
        return factor == 1 ? 0 : (factor == 0 ? 1 : 0.5);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        elo.forEach(
                (name, rt) ->
                        sb.append(name).append(" -> ").append(rt).append(System.lineSeparator()));
        return sb.toString();
    }

    public synchronized void saveELO() throws IOException {
        try (final BufferedWriter writer =
                Files.newBufferedWriter(
                        newContents,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(elo, writer);
        }
        Files.move(
                newContents,
                toReplace,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }
}
