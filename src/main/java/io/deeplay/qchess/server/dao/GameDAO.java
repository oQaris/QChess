package io.deeplay.qchess.server.dao;

import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.server.database.Database;
import io.deeplay.qchess.server.database.Room;

public class GameDAO {

    public static Room getRoom() {
        return Database.getInstance().getRoom();
    }

    /** @return цвет игрока или null, если игрок не найден */
    public static Color getColor(String sessionToken) {
        return Database.getInstance().getColor(sessionToken);
    }
}
