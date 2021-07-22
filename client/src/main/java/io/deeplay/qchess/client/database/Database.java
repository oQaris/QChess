package io.deeplay.qchess.client.database;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;

public class Database {
    private static Database database;
    private String sessionToken;
    private Selfplay game;
    private GameSettings gs;

    private Database() {}

    public static Database getInstance() {
        if (database == null) database = new Database();
        return database;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
