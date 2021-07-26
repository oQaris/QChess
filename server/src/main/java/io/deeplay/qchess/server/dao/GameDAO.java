package io.deeplay.qchess.server.dao;

import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.server.database.Database;
import io.deeplay.qchess.server.database.Room;

public class GameDAO {

    /**
     * @return комната с игроком, у которого токен сессии равен sessionToken или null, если комната
     *     не найдена
     */
    public static Room getRoom(String sessionToken) {
        return Database.getInstance().getRoom(sessionToken);
    }

    /** @return комната с предпочитаемыми настройками или null, если комната не найдена */
    public static Room findSuitableRoom(PlayerType enemyType, int gameCount) {
        return Database.getInstance().findSuitableRoom(enemyType, gameCount);
    }
}
