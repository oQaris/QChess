package io.deeplay.qchess.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RatingELO {
    public static final String fileName = "elo_rating";
    public static final Map<String, Long> elo = new ConcurrentHashMap<>();

    public void updateELO(
            final String firstPlayerName,
            final String secondPlayerName,
            // фактически набранное игроком A количество очков (1 очко за победу, 0,5 — за ничью и 0
            // — за поражение)
            final double sA) {
        // рейтинг игрока A
        final long rA = elo.getOrDefault(firstPlayerName, 0L);
        // рейтинг игрока B
        final long rB = elo.getOrDefault(secondPlayerName, 0L);

        // ожидаемое количество очков, которое наберёт игрок A в партии с B
        final double eA = 1. / (1. + Math.pow(10., (rB - rA) / 400.));

        // коэффициент, значение которого равно 10 для сильнейших игроков (рейтинг 2400 и выше),
        // 20 — для игроков с рейтингом меньше, чем 2400 и 40 — для новых игроков (первые 30 партий
        // с момента получения рейтинга ФИДЕ), а также для игроков рейтинг которы ниже 2300
        final double k = 30;

        // новый рейтинг игрока A
        final long newRa = Math.round(rA + k * (sA - eA));

        elo.put(firstPlayerName, newRa);
    }
}
