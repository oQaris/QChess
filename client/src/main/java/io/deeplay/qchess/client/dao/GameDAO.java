package io.deeplay.qchess.client.dao;

import io.deeplay.qchess.client.database.Database;
import io.deeplay.qchess.client.view.gui.EnemyType;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.model.Color;

public class GameDAO {

    public static void newGame(GameSettings gs, Selfplay game, Color color) {
        Database.getInstance().newGame(gs, game, color);
    }

    public static Color getMyColor() {
        return Database.getInstance().getMyColor();
    }

    public static EnemyType getEnemyType() {
        return Database.getInstance().getEnemyType();
    }

    public static void setEnemy(EnemyType enemyType) {
        Database.getInstance().setEnemyType(enemyType);
    }

    public static GameSettings getGameSettings() {
        return Database.getInstance().getGameSettings();
    }

    public static Selfplay getGame() {
        return Database.getInstance().getGame();
    }

    public static void startGame() {
        Database.getInstance().startGame();
    }

    public static boolean isGameStarted() {
        return Database.getInstance().isGameStarted();
    }
}
