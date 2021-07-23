package io.deeplay.qchess.game.player;

import static io.deeplay.qchess.game.exceptions.ChessErrorCode.CONSOLE_PLAYER_ERROR;
import static io.deeplay.qchess.game.exceptions.ChessErrorCode.UNKNOWN_FIGURE_SELECTED;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsolePlayer extends RemotePlayer {
    private static final Logger logger = LoggerFactory.getLogger(ConsolePlayer.class);
    private static final String TURN_INTO_INVITE =
            "Выберите фигуру для превращения:"
                    + System.lineSeparator()
                    + "1 - Конь"
                    + System.lineSeparator()
                    + "2 - Слон"
                    + System.lineSeparator()
                    + "3 - Ладья"
                    + System.lineSeparator()
                    + "4 - Ферзь";

    private final BufferedReader in;

    public ConsolePlayer(GameSettings roomSettings, Color color, BufferedReader in) {
        super(roomSettings, color, "console-player");
        this.in = in;
    }

    @Override
    public Move getNextMove() throws ChessError {
        try {
            List<Move> allMoves = ms.getAllCorrectMoves(color);
            System.out.println(board);
            printMoves(allMoves);
            Move chosenMove = inputMoveNumber(allMoves);
            specificMoveModification(chosenMove);
            return chosenMove;
        } catch (ChessError e) {
            logger.error("Возникла ошибка в консольном игроке: {}", e.getMessage());
            throw new ChessError(CONSOLE_PLAYER_ERROR, e);
        }
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.CONSOLE_PLAYER;
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
                String input = in.readLine();
                logger.info("Игрок ввел: {}", input);
                int numMove = Integer.parseInt(input);
                move = allMoves.get(numMove - 1);
            } catch (IOException | NumberFormatException | IndexOutOfBoundsException e) {
                logger.info("Игрок ввел неправильный ход");
                System.out.println("Неправильный ход, повторите попытку");
            }
        }
        return move;
    }

    private void specificMoveModification(Move chosenMove) throws ChessError {
        if (chosenMove.getMoveType() == MoveType.TURN_INTO) {
            System.out.println(TURN_INTO_INVITE);
            chosenMove.setTurnInto(readTurnInto());
        }
    }

    private FigureType readTurnInto() throws ChessError {
        int numTurnIntoFigure = 0;
        final int maxCountFigures = 4;
        while (numTurnIntoFigure == 0) {
            try {
                String input = in.readLine();
                logger.info("Игрок ввел: {}", input);
                numTurnIntoFigure = Integer.parseInt(input);
                if (numTurnIntoFigure < 0 || numTurnIntoFigure > maxCountFigures) {
                    numTurnIntoFigure = 0;
                    throw new IllegalArgumentException();
                }
            } catch (IOException | IllegalArgumentException e) {
                logger.info("Игрок ввел неправильный номер фигуры");
                System.out.println("Неправильный номер фигуры, повторите попытку");
            }
        }
        return switch (numTurnIntoFigure) {
            case 1 -> FigureType.KNIGHT;
            case 2 -> FigureType.BISHOP;
            case 3 -> FigureType.ROOK;
            case 4 -> FigureType.QUEEN;
            default -> {
                logger.error(
                        "В консольном игроке выбрана неизвестная фигура: {}", numTurnIntoFigure);
                throw new ChessError(UNKNOWN_FIGURE_SELECTED);
            }
        };
    }
}
