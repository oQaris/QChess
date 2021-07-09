package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Bishop;
import io.deeplay.qchess.game.model.figures.Knight;
import io.deeplay.qchess.game.model.figures.Queen;
import io.deeplay.qchess.game.model.figures.Rook;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ConsolePlayer extends Player {

    private static final Logger logger = LoggerFactory.getLogger(ConsolePlayer.class);
    private final BufferedReader in;

    public ConsolePlayer(GameSettings roomSettings, Color color, BufferedReader in) {
        super(roomSettings, color);
        this.in = in;
    }

    @Override
    public Move getNextMove() throws ChessError {
        try {
            List<Move> allMoves = ms.getAllCorrectMoves(color);
            printMoves(allMoves);
            Move choosenMove = inputMoveNumber(allMoves);
            specificMoveModification(choosenMove);
            return choosenMove;
        } catch (ChessError e) {
            throw new ChessError("Произошла ошибка в классе игрока", e);
        }
    }

    private void printMoves(List<Move> allMoves) throws ChessError {
        System.out.println("Выберите ход:");
        allMoves.sort(Comparator.comparing(Move::toString));
        int number = 1;
        for (Move move : allMoves) {
            System.out.println(number + ": " + move);
            ++number;
        }
    }

    private Move inputMoveNumber(List<Move> allMoves) throws ChessError {
        Move move = null;
        while (move == null) {
            try {
                int numMove = Integer.parseInt(in.readLine());
                move = allMoves.get(numMove - 1);
            } catch (IOException | NumberFormatException | IndexOutOfBoundsException e) {
                logger.info("Неправильный ход, повторите попытку");
            }
        }
        return move;
    }

    private void specificMoveModification(Move choosenMove) throws ChessError {
        if (choosenMove.getMoveType() == MoveType.TURN_INTO) {
            printMoveTypes();
            choosenMove.setTurnInto(readTurnInto(choosenMove.getTo()));
        }
    }

    private void printMoveTypes() {
        System.out.println(new StringBuilder("Выберите фигуру для превращения:")
                .append(System.lineSeparator()).append("1 - Конь")
                .append(System.lineSeparator()).append("2 - Слон")
                .append(System.lineSeparator()).append("3 - Ладья")
                .append(System.lineSeparator()).append("4 - Ферзь"));
    }

    private Figure readTurnInto(Cell to) throws ChessError {
        int numTurnIntoFig = 0;
        while (numTurnIntoFig == 0) {
            try {
                numTurnIntoFig = Integer.parseInt(in.readLine());
                if (numTurnIntoFig < 0 || numTurnIntoFig > 4) {
                    numTurnIntoFig = 0;
                }
            } catch (IOException | NumberFormatException e) {
                logger.info("Неправильный номер фигуры, повторите попытку");
            }
        }
        return switch (numTurnIntoFig) {
            case 1 -> new Knight(color, to);
            case 2 -> new Bishop(color, to);
            case 3 -> new Rook(color, to);
            case 4 -> new Queen(color, to);
            default -> throw new ChessError("Выбрана неизвестная фигура");
        };
    }
}