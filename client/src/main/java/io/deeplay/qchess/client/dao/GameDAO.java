package io.deeplay.qchess.client.dao;

import io.deeplay.qchess.client.database.Database;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.player.Player;

public class GameDAO {

    public static void newGame(GameSettings gs, Selfplay game) {
        Database.getInstance().newGame(gs, game);
    }

    public static Player getEnemy() {
        return Database.getInstance().getEnemy();
    }

    public static void setEnemy(Player enemy) {
        Database.getInstance().setEnemy(enemy);
    }

    public static GameSettings getGameSettings() {
        return Database.getInstance().getGameSettings();
    }

    public static Selfplay getGame() {
        return Database.getInstance().getGame();
    }

    public static void changeIsMyStep() {
        Database.getInstance().changeIsMyStep();
    }

    public static boolean isMyStep() {
        return Database.getInstance().isMyStep();
    }
}
