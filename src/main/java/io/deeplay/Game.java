package io.deeplay;

public final class Game {

    private static Game game = new Game();

    private Game() {
    }

    public static Game initGame() {
        return game;
    }

    private Board board;
    private boolean currentPlayer;

    public void start() {
        // TODO: использовать метод update для обновления доски
    }

    private void update() {

    }
}
