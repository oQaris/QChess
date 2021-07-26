package io.deeplay.qchess.server.dao;

import io.deeplay.qchess.server.database.Database;

public class ConnectionControlDAO {

    public static boolean contains(String sessionToken) {
        return Database.getInstance().contains(sessionToken);
    }

    public static void addPlayer(String sessionToken, int clientId) {
        Database.getInstance().addPlayer(sessionToken, clientId);
    }

    public static void removePlayer(String sessionToken) {
        Database.getInstance().removePlayer(sessionToken);
    }

    /** @return id клиента или null, если его нет */
    public static Integer getId(String sessionToken) {
        return Database.getInstance().getId(sessionToken);
    }
}
