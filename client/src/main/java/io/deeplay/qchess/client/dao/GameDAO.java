package io.deeplay.qchess.client.dao;

import io.deeplay.qchess.client.database.Database;
import io.deeplay.qchess.client.view.gui.PlayerType;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.model.Color;

public class GameDAO {

    /** Добавляет или заменяет игру */
    public static void newGame(GameSettings gs, Selfplay game, Color color) {
        Database.getInstance().newGame(gs, game, color);
    }

    /** @return цвет игрока клиента */
    public static Color getMyColor() {
        return Database.getInstance().getMyColor();
    }

    /** @return тип противника */
    public static PlayerType getEnemyType() {
        return Database.getInstance().getEnemyType();
    }

    /** Устанавливает тип противника */
    public static void setEnemy(PlayerType playerType) {
        Database.getInstance().setEnemyType(playerType);
    }

    /** @return тип игрока клиента */
    public static PlayerType getMyType() {
        return Database.getInstance().getMyType();
    }

    /** Устанавливает тип игрока клиента */
    public static void setMyType(PlayerType playerType) {
        Database.getInstance().setMyType(playerType);
    }

    /** @return настройки текущей игры */
    public static GameSettings getGameSettings() {
        return Database.getInstance().getGameSettings();
    }

    /** @return текущая игра */
    public static Selfplay getGame() {
        return Database.getInstance().getGame();
    }

    /** Устанавливает флаг, что игра началась */
    public static void startGame() {
        Database.getInstance().startGame();
    }

    /** @return true, если флаг, указывающий что игра началась, установлен */
    public static boolean isGameStarted() {
        return Database.getInstance().isGameStarted();
    }
}
