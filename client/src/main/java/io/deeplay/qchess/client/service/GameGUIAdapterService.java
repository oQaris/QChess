package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.dao.GameDAO;
import io.deeplay.qchess.client.view.gui.ViewCell;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.client.view.model.ViewFigureType;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGUIAdapterService {

    public static Set<ViewCell> getAllMoves(int row, int column) {
        Cell cell = new Cell(column, row);
        Set<ViewCell> set = new HashSet<>();
        try {
            for (Move move : GameDAO.getGameSettings().moveSystem.getAllCorrectMoves(cell)) {
                ViewCell vc =
                        new ViewCell(
                                move.getTo().getRow(),
                                move.getTo().getColumn(),
                                move.getMoveType() == MoveType.ATTACK
                                        || move.getMoveType() == MoveType.TURN_INTO_ATTACK
                                        || move.getMoveType() == MoveType.EN_PASSANT);
                set.add(vc);
            }
        } catch (ChessError e) {
            e.printStackTrace();
        }
        return set;
    }

    public static boolean checkFigure(int row, int column, boolean isWhite) {
        Cell cell = new Cell(column, row);
        Figure figure = null;
        try {
            figure = GameDAO.getGameSettings().board.getFigure(cell);
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return figure != null && (figure.getColor() == Color.WHITE) == isWhite;
    }

    public static ViewFigure getFigure(int row, int column) {
        Cell cell = new Cell(column, row);
        Figure figure;
        ViewFigure vf = null;
        try {
            figure = GameDAO.getGameSettings().board.getFigure(cell);
            if (figure != null) {
                vf =
                        new ViewFigure(
                                figure.getColor().toString(),
                                ViewFigureType.valueOf(figure.getType().name()));
            }
        } catch (ChessException e) {
            e.printStackTrace();
        }

        return vf;
    }

    public static Move tryMakeMove(int rowFrom, int columnFrom, int rowTo, int columnTo) {
        Cell from = new Cell(columnFrom, rowFrom);
        Cell to = new Cell(columnTo, rowTo);
        List<Move> set;
        System.out.println(
                "from: " + rowFrom + " " + columnFrom + "; " + "to: " + rowTo + " " + columnTo);
        try {
            set = GameDAO.getGameSettings().moveSystem.getAllCorrectMoves(from);
        } catch (ChessError e) {
            e.printStackTrace();
            return null;
        }
        for (Move move : set) {
            if (to.equals(move.getTo())) {
                return move;
            }
        }

        return null;
    }

    public static Move makeMove(
            int rowFrom, int columnFrom, int rowTo, int columnTo, FigureType figureType) {
        Cell from = new Cell(columnFrom, rowFrom);
        Cell to = new Cell(columnTo, rowTo);
        List<Move> set;
        System.out.println(
                "from: " + rowFrom + " " + columnFrom + "; " + "to: " + rowTo + " " + columnTo);
        try {
            set = GameDAO.getGameSettings().moveSystem.getAllCorrectMoves(from);
        } catch (ChessError e) {
            e.printStackTrace();
            return null;
        }
        for (Move move : set) {
            if (to.equals(move.getTo())) {
                try {
                    move.setTurnInto(figureType);
                    GameDAO.getGame().move(move);
                } catch (ChessError e) {
                    e.printStackTrace();
                    return null;
                }
                GameDAO.changeIsMyStep();
                System.out.println(GameDAO.getGameSettings().board.toString());
                return move;
            }
        }

        return null;
    }

    public static String getStatus(boolean color) {
        try {
            if (GameDAO.getGameSettings().endGameDetector.isDraw()) {
                if (GameDAO.getGameSettings().endGameDetector.isDrawWithPeaceMoves()) {
                    return String.format(
                            "Ничья: %d ходов без взятия и хода пешки",
                            EndGameDetector.END_PEACE_MOVE_COUNT);
                } else if (GameDAO.getGameSettings().endGameDetector.isDrawWithRepetitions()) {
                    return String.format(
                            "Ничья: %d повторений позиций доски",
                            EndGameDetector.END_REPETITIONS_COUNT);
                } else if (GameDAO.getGameSettings()
                        .endGameDetector
                        .isDrawWithNotEnoughMaterialForCheckmate()) {
                    return "Ничья: недостаточно фигур, чтобы поставить мат";
                }
            } else if (GameDAO.getGameSettings()
                    .endGameDetector
                    .isCheckmate(color ? Color.BLACK : Color.WHITE)) {
                return "Мат " + (color ? "черным" : "белым");
            } else if (GameDAO.getGameSettings()
                    .endGameDetector
                    .isStalemate(color ? Color.BLACK : Color.WHITE)) {
                return "Пат " + (color ? "черным" : "белым");
            }
        } catch (ChessError chessError) {
            chessError.printStackTrace();
        }
        return "";
    }

    public static boolean getEnd(boolean color) {
        return !getStatus(color).isEmpty();
    }
}
