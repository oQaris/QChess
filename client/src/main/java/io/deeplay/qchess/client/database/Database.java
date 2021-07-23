package io.deeplay.qchess.client.database;

import io.deeplay.qchess.client.view.gui.EnemyType;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;

public class Database {
    private static Database database;
    private String sessionToken;
    private Selfplay game;
    private GameSettings gs;
    private boolean isMyStep;
    private EnemyType enemyType;

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

    public void newGame(GameSettings gs, Selfplay game) {
        this.gs = gs;
        this.game = game;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(EnemyType enemyType) {
        this.enemyType = enemyType;
    }

    public GameSettings getGameSettings() {
        return gs;
    }

    public Selfplay getGame() {
        return game;
    }

    public void changeIsMyStep() {
        isMyStep = !isMyStep;
    }

    public boolean isMyStep() {
        return isMyStep;
    }
}
