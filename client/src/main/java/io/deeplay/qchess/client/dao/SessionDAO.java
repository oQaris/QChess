package io.deeplay.qchess.client.dao;

import io.deeplay.qchess.client.database.Database;

public class SessionDAO {

    /** @return токен для подключения к серверу */
    public static String getSessionToken() {
        return Database.getInstance().getSessionToken();
    }

    /** Устанавливает токен для подключения к серверу */
    public static void setSessionToken(final String sessionToken) {
        Database.getInstance().setSessionToken(sessionToken);
    }
}
