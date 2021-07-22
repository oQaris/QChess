package io.deeplay.qchess.server.dao;

import io.deeplay.qchess.server.database.Database;
import io.deeplay.qchess.server.database.Room;

public class ConnectionControlDAO {

    public static boolean contains(String sessionToken) {
        return Database.getInstance().contains(sessionToken);
    }

    public static void addPlayer(String sessionToken, int clientID) {
        Database.getInstance().addPlayer(sessionToken, clientID);
    }

    public static void removePlayer(String sessionToken) {
        Database.getInstance().removePlayer(sessionToken);
    }

    /** @return id клиента или null, если его нет */
    public static Integer getID(String sessionToken) {
        return Database.getInstance().getID(sessionToken);
    }
}
