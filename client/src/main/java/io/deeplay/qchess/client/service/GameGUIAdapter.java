package io.deeplay.qchess.client.service;

import io.deeplay.qchess.client.dao.GameDAO;
import io.deeplay.qchess.client.view.gui.ViewCell;
import io.deeplay.qchess.client.view.model.ViewFigure;
import io.deeplay.qchess.client.view.model.ViewFigureType;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import io.deeplay.qchess.game.model.figures.FigureType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGUIAdapter {

    public static Set<ViewCell> getAllMoves(final int row, final int column) {
        final Cell cell = new Cell(column, row);
        final Set<ViewCell> set = new HashSet<>();
        try {
            for (final Move move : GameDAO.getGameSettings().moveSystem.getAllCorrectMoves(cell)) {
                final ViewCell vc =
                        new ViewCell(
                                move.getTo().row,
                                move.getTo().column,
                                move.getMoveType() == MoveType.ATTACK
                                        || move.getMoveType() == MoveType.TURN_INTO_ATTACK
                                        || move.getMoveType() == MoveType.EN_PASSANT);
                set.add(vc);
            }
        } catch (final ChessError e) {
            e.printStackTrace();
        }
        return set;
    }

    public static boolean checkFigure(final int row, final int column, final boolean isWhite) {
        final Cell cell = new Cell(column, row);
        Figure figure = null;
        try {
            figure = GameDAO.getGameSettings().board.getFigure(cell);
        } catch (final ChessException e) {
            e.printStackTrace();
        }
        return figure != null && (figure.getColor() == Color.WHITE) == isWhite;
    }

    public static ViewFigure getFigure(final int row, final int column) {
        final Cell cell = new Cell(column, row);
        final Figure figure;
        ViewFigure vf = null;
        try {
            figure = GameDAO.getGameSettings().board.getFigure(cell);
            if (figure != null) {
                vf =
                        new ViewFigure(
                                figure.getColor().toString(),
                                ViewFigureType.valueOf(
                                        FigureType.nameOfTypeNumber[figure.figureType.type]));
            }
        } catch (final ChessException e) {
            e.printStackTrace();
        }

        return vf;
    }

    public static Move tryMakeMove(
            final int rowFrom, final int columnFrom, final int rowTo, final int columnTo) {
        final Cell from = new Cell(columnFrom, rowFrom);
        final Cell to = new Cell(columnTo, rowTo);
        final List<Move> set;
        try {
            set = GameDAO.getGameSettings().moveSystem.getAllCorrectMoves(from);
        } catch (final ChessError e) {
            e.printStackTrace();
            return null;
        }
        for (final Move move : set) {
            if (to.equals(move.getTo())) {
                return move;
            }
        }

        return null;
    }

    /** @return true, если королю цвета color поставили шах */
    public static boolean isCheck(final boolean color) {
        return GameDAO.getGameSettings().endGameDetector.isCheck(color ? Color.WHITE : Color.BLACK);
    }

    /** @return клетка короля цвета color */
    public static ViewCell getKingCell(final boolean color) {
        final Cell cell =
                GameDAO.getGameSettings().board.findKingCell(color ? Color.WHITE : Color.BLACK);
        return new ViewCell(cell.row, cell.column, false);
    }
}
