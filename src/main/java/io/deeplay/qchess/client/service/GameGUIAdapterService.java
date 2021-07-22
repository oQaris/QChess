package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.view.gui.ViewCell;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.client.view.model.ViewFigureType;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import io.deeplay.qchess.game.player.RemotePlayer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGUIAdapterService {
    // TODO: убрать отсюда куда-нибудь
    private static Selfplay game;
    private static GameSettings gs;
    private static boolean isWhiteStep = true;

    // TODO: убрать отсюда куда-нибудь
    public static void init() {
        gs = new GameSettings(BoardFilling.STANDARD);
        try {
            game =
                    new Selfplay(
                            gs,
                            new RemotePlayer(gs, Color.WHITE, "1"),
                            new RemotePlayer(gs, Color.BLACK, "2"));
        } catch (ChessError ignore) {
            // Стандартная расстановка доски верна всегда
        }
    }

    // TODO: убрать отсюда куда-нибудь
    public static Board getBoard() {
        return gs.board;
    }

    public static Set<ViewCell> getAllMoves(int row, int column) {
        Cell cell = new Cell(column, row);
        Set<ViewCell> set = new HashSet<>();
        try {
            for (Move move : gs.moveSystem.getAllCorrectMoves(cell)) {
                ViewCell vc =
                        new ViewCell(
                                move.getTo().getRow(),
                                move.getTo().getColumn(),
                                move.getMoveType() == MoveType.ATTACK
                                        || move.getMoveType() == MoveType.TURN_INTO_ATTACK);
                set.add(vc);
            }
        } catch (ChessError e) {
            e.printStackTrace();
        }
        return set;
    }

    public static boolean checkFigure(int row, int column) {
        Cell cell = new Cell(column, row);
        Figure figure = null;
        try {
            figure = gs.board.getFigure(cell);
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return figure != null;
    }

    public static boolean checkFigure(int row, int column, boolean isWhite) {
        Cell cell = new Cell(column, row);
        Figure figure = null;
        try {
            figure = gs.board.getFigure(cell);
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
            figure = gs.board.getFigure(cell);
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
            set = gs.moveSystem.getAllCorrectMoves(from);
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
            set = gs.moveSystem.getAllCorrectMoves(from);
        } catch (ChessError e) {
            e.printStackTrace();
            return null;
        }
        for (Move move : set) {
            if (to.equals(move.getTo())) {
                try {
                    move.setTurnInto(figureType);
                    game.move(move);
                } catch (ChessError e) {
                    e.printStackTrace();
                    return null;
                }
                isWhiteStep = !isWhiteStep;
                System.out.println(gs.board.toString());
                return move;
            }
        }

        return null;
    }

    public static boolean isWhiteStep() {
        return isWhiteStep;
    }

    public static void changeIsWhiteStep() {
        isWhiteStep = !isWhiteStep;
    }

    public static String getStatus() {
        try {
            if (gs.endGameDetector.isDraw()) {
                if (gs.endGameDetector.isDrawWithPeaceMoves()) {
                    return String.format(
                            "Ничья: %d ходов без взятия и хода пешки",
                            EndGameDetector.END_PEACE_MOVE_COUNT);
                } else if (gs.endGameDetector.isDrawWithRepetitions()) {
                    return String.format(
                            "Ничья: %d повторений позиций доски",
                            EndGameDetector.END_REPETITIONS_COUNT);
                } else if (gs.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate()) {
                    return "Ничья: недостаточно фигур, чтобы поставить мат";
                }
            } else if (gs.endGameDetector.isCheckmate(Color.WHITE)) {
                return "Мат белым";
            } else if (gs.endGameDetector.isCheckmate(Color.BLACK)) {
                return "Мат черным";
            } else if (gs.endGameDetector.isStalemate(Color.WHITE)) {
                return "Пат белым";
            } else if (gs.endGameDetector.isStalemate(Color.BLACK)) {
                return "Пат черным";
            }
        } catch (ChessError chessError) {
            chessError.printStackTrace();
        }
        return "";
    }

    public static boolean getEnd() {
        return !getStatus().isEmpty();
    }
}
