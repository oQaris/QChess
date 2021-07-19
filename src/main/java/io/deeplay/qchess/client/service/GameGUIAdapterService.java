package io.deeplay.qchess.client.service;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.player.RemotePlayer;
import io.deeplay.qchess.client.view.gui.ViewCell;
import io.deeplay.qchess.client.view.gui.ViewFigure;
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
                            new RemotePlayer(gs, Color.WHITE, 1),
                            new RemotePlayer(gs, Color.BLACK, 2));
        } catch (ChessError ignore) {
            // Стандартная расстановка доски верна всегда
        }
    }

    public static Set<ViewCell> getAllMoves(int row, int column) {
        Cell cell = new Cell(column, row);
        Set<ViewCell> set = new HashSet<>();
        try {
            for (Move move :
                    gs.moveSystem.getAllCorrectMoves(cell)) {
                ViewCell vc =
                        new ViewCell(
                                move.getTo().getRow(),
                                move.getTo().getColumn(),
                                move.getMoveType() == MoveType.ATTACK);
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
                vf = new ViewFigure(figure.getColor().toString(), figure.getType().toString());
            }
        } catch (ChessException e) {
            e.printStackTrace();
        }

        return vf;
    }

    public static Move makeMove(int rowFrom, int columnFrom, int rowTo, int columnTo) {
        Cell from = new Cell(columnFrom, rowFrom);
        Cell to = new Cell(columnTo, rowTo);
        List<Move> set = null;
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
}
