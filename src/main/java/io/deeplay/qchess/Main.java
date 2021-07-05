package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;

public class Main {

    public static void main(String[] args) {
        Game game = Game.initGame();
        game.start();
    }
}
