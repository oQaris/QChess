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

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.CONSOLE_PLAYER_ERROR;

public class ConsolePlayer extends Player {
    private static final Logger logger = LoggerFactory.getLogger(ConsolePlayer.class);
    private static final String TURN_INTO_INVITE =
            "Выберите фигуру для превращения:" +
                    System.lineSeparator() + "1 - Конь" +
                    System.lineSeparator() + "2 - Слон" +
                    System.lineSeparator() + "3 - Ладья" +
                    System.lineSeparator() + "4 - Ферзь";

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
            throw new ChessError(CONSOLE_PLAYER_ERROR, e);
        }
    }

    private void printMoves(List<Move> allMoves) {
        System.out.println("Выберите ход:");
        allMoves.sort(Comparator.comparing(Move::toString));
        int number = 1;
        for (Move move : allMoves) {
            System.out.println(number + ": " + move);
            ++number;
        }
    }

    private Move inputMoveNumber(List<Move> allMoves) {
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
            System.out.println(TURN_INTO_INVITE);
            choosenMove.setTurnInto(readTurnInto(choosenMove.getTo()));
        }
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
            //todo почему бы по-умолчанию ферзя не выбирать? или хотя бы повторить запрос
            default -> throw new ChessError("Выбрана неизвестная фигура");
        };
    }
}