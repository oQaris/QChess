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
    private Color myPreferColor;

    private Database() {}

    public static Database getInstance() {
        if (database == null) database = new Database();
        return database;
    }

    /** @return токен для подключения к серверу */
    public String getSessionToken() {
        return sessionToken;
    }

    /** Устанавливает токен для подключения к серверу */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /** Добавляет или заменяет игру */
    public void newGame(GameSettings gs, Selfplay game, Color color) {
        this.gs = gs;
        this.game = game;
        myColor = color;
    }

    /** @return тип противника */
    public PlayerType getEnemyType() {
        return playerType;
    }

    /** Устанавливает тип противника */
    public void setEnemyType(PlayerType playerType) {
        this.playerType = playerType;
    }

    /** @return тип игрока клиента */
    public PlayerType getMyType() {
        return myType;
    }

    /** Устанавливает тип игрока клиента */
    public void setMyType(PlayerType playerType) {
        this.myType = playerType;
    }

    /** @return настройки текущей игры */
    public GameSettings getGameSettings() {
        return gs;
    }

    /** @return текущая игра */
    public Selfplay getGame() {
        return game;
    }

    /** Устанавливает флаг, что игра началась */
    public void startGame() {
        isGameStarted = true;
    }

    /** @return true, если флаг, указывающий что игра началась, установлен */
    public boolean isGameStarted() {
        return isGameStarted;
    }

    /** @return цвет игрока клиента */
    public Color getMyColor() {
        return myColor;
    }

    /** @return предпочитаемый цвет игрока клиента */
    public Color getMyPreferColor() {
        return myPreferColor;
    }

    /** Устанавливает предпочитаемый цвет игрока клиента */
    public void setMyPreferColor(Color myPreferColor) {
        this.myPreferColor = myPreferColor;
    }
}
