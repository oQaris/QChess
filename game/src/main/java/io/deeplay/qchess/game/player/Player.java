package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.History;
import io.deeplay.qchess.game.model.Move;

public abstract class Player {
    protected GameSettings roomSettings;
    protected Board board;
    protected Color color;
    protected MoveSystem ms;
    protected EndGameDetector egd;
    protected History history;

    protected Player(final GameSettings roomSettings, final Color color) {
        this.roomSettings = roomSettings;
        ms = roomSettings.moveSystem;
        board = roomSettings.board;
        egd = roomSettings.endGameDetector;
        history = roomSettings.history;
        this.color = color;
    }

    public void setGameSettings(final GameSettings gs, final Color color) {
        this.roomSettings = gs;
        ms = gs.moveSystem;
        board = gs.board;
        egd = gs.endGameDetector;
        history = gs.history;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public GameSettings getRoomSettings() {
        return roomSettings;
    }

    /** @return возвращает проверенный ход */
    public abstract Move getNextMove() throws ChessError;

    @Override
    public String toString() {
        return color + " PLAYER";
    }

    public abstract PlayerType getPlayerType();
}
