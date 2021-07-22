package io.deeplay.qchess.client.dao;

import io.deeplay.qchess.client.database.Database;

// TODO: добавить интерфейс
public class SessionDAO {

    public static String getSessionToken() {
        return Database.getInstance().getSessionToken();
    }

    public static void setSessionToken(String sessionToken) {
        Database.getInstance().setSessionToken(sessionToken);
    }
}
