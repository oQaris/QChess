package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Move;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsolePlayer extends Player {

    public ConsolePlayer() {
        super();
    }

    @Override
    public Move getNextMove() throws ChessError {
        System.out.println("Выберите ход:");
        List<Move> allMoves = new ArrayList<>(ms.getAllCorrectMoves(color));
        int i = 1;
        for (Move move : allMoves) {
            System.out.println(i + " - " + move);
            ++i;
        }
        Scanner in = new Scanner(System.in);
        int numMove = in.nextInt();
        return allMoves.get(numMove - 1);
    }
}
