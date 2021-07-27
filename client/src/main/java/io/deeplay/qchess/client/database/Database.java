package io.deeplay.qchess.client.database;

import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.model.Color;

public class Database {
    private static Database database;
    private String sessionToken;
    private Selfplay game;
    private GameSettings gs;
    private boolean isGameStarted;
    private PlayerType playerType;
    private PlayerType myType;
    private Color myColor;

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

    public void newGame(GameSettings gs, Selfplay game, Color color) {
        this.gs = gs;
        this.game = game;
        myColor = color;
    }

    public PlayerType getEnemyType() {
        return playerType;
    }

    public void setEnemyType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public void setMyType(PlayerType playerType) {
        this.myType = playerType;
    }

    public PlayerType getMyType() {
        return myType;
    }

    public GameSettings getGameSettings() {
        return gs;
    }

    public Selfplay getGame() {
        return game;
    }

    public void startGame() {
        isGameStarted = true;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public Color getMyColor() {
        return myColor;
    }
}
