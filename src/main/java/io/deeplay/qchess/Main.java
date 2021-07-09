package io.deeplay.qchess;

import io.deeplay.qchess.game.Game;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.player.Bot;
import io.deeplay.qchess.game.player.ConsolePlayer;
import io.deeplay.qchess.game.player.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws ChessError {
        // TODO: при создании комнаты
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        Player firstPlayer = new ConsolePlayer(in);
        Player secondPlayer = new Bot();
        Game game = new Game(Board.BoardFilling.STANDARD, firstPlayer, secondPlayer);
        game.run();
    }
}