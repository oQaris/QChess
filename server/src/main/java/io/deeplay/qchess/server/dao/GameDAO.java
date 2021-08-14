package io.deeplay.qchess.server.dao;

import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.server.database.Database;
import io.deeplay.qchess.server.database.Room;

public class GameDAO {

    /**
     * @return комната с игроком, у которого токен сессии равен sessionToken или null, если комната
     *     не найдена
     */
    public static Room getRoom(final String sessionToken) {
        return Database.getInstance().getRoom(sessionToken);
    }

    /** @return комната с предпочитаемыми настройками или null, если комната не найдена */
    public static Room findSuitableRoom(
            final String sessionToken,
            final PlayerType enemyType,
            final int gameCount,
            final Color myPreferColor) {
        return Database.getInstance()
                .findSuitableRoom(sessionToken, enemyType, gameCount, myPreferColor);
    }
}
