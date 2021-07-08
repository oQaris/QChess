package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.figures.Bishop;
import io.deeplay.qchess.game.figures.Knight;
import io.deeplay.qchess.game.figures.Queen;
import io.deeplay.qchess.game.figures.Rook;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;

import java.util.List;
import java.util.Scanner;

public class ConsolePlayer extends Player {

    public ConsolePlayer() {
        super();
    }

    @Override
    public Move getNextMove() throws ChessError {
        System.out.println("Выберите ход:");
        List<Move> allMoves = ms.getAllCorrectMoves(color);
        int i = 1;
        for (Move move : allMoves) {
            System.out.println(i + ": " + move);
            i++;
        }
        Scanner in = new Scanner(System.in);
        int numMove = in.nextInt();
        var move = allMoves.get(numMove - 1);
        if (move.getMoveType() == MoveType.TURN_INTO)
            move.setTurnInto(readTurnInto(move.getTo()));
        return move;
    }

    private Figure readTurnInto(Cell to) {
        System.out.println("Выберите фигуру для превращения:\n" +
                "1 - Конь\n" +
                "2 - Слон\n" +
                "3 - Ладья\n" +
                "4 - Ферзь");
        Scanner in = new Scanner(System.in);
        int numTurnIntoFig = in.nextInt();
        new Queen(board, color, to);
        return switch (numTurnIntoFig) {
            case 1 -> new Knight(board, color, to);
            case 2 -> new Bishop(board, color, to);
            case 3 -> new Rook(board, color, to);
            default -> new Queen(board, color, to);
        };
    }
}