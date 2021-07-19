package io.deeplay.qchess.gui;

import io.deeplay.qchess.client.IClientController;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.Figure;
import java.util.HashSet;
import java.util.Set;

public class GuiController implements IClientController {
    private final GameSettings gs;
    private boolean isWhiteStep = true;

    public GuiController(final GameSettings gs) {
        this.gs = gs;
    }

    @Override
    public Set<ViewCell> getAllMoves(int row, int column) {
        Cell cell = new Cell(column, row);
        Set<ViewCell> set = new HashSet<>();
        try {
            for(Move move : gs.board.getFigure(cell).getAllMoves(new GameSettings(BoardFilling.STANDARD))) {
                ViewCell vc = new ViewCell(move.getTo().getRow(), move.getTo().getColumn(), move.getMoveType() == MoveType.ATTACK);
                set.add(vc);
            }
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return set;
    }

    @Override
    public boolean checkFigure(int row, int column) {
        Cell cell = new Cell(column, row);
        Figure figure = null;
        try {
            figure = gs.board.getFigure(cell);
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return figure != null;
    }

    @Override
    public boolean checkFigure(int row, int column, boolean isWhite) {
        Cell cell = new Cell(column, row);
        Figure figure = null;
        try {
            figure = gs.board.getFigure(cell);
        } catch (ChessException e) {
            e.printStackTrace();
        }
        return figure != null && (figure.getColor() == Color.WHITE) == isWhite;
    }

    @Override
    public ViewFigure getFigure(int row, int column) {
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

    @Override
    public boolean makeMove(int rowFrom, int columnFrom, int rowTo, int columnTo) {
        Cell from = new Cell(columnFrom, rowFrom);
        Cell to = new Cell(columnTo, rowTo);
        Set<Move> set = null;
        System.out.println("from: " + rowFrom + " " + columnFrom + "; " + "to: " + rowTo + " " + columnTo);
        try {
            set = gs.board.getFigure(from).getAllMoves(new GameSettings(BoardFilling.STANDARD));
        } catch (ChessException e) {
            e.printStackTrace();
            return false;
        }
        for (Move move : set) {
            if(to.equals(move.getTo())) {
                try {
                    gs.board.moveFigure(move);
                } catch (ChessException e) {
                    e.printStackTrace();
                    return false;
                }
                isWhiteStep = !isWhiteStep;
               // System.out.println(board.toString());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isWhiteStep() {
        return isWhiteStep;
    }
}
