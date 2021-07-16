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
    private final Board board;

    public GuiController(final Board board) {
        this.board = board;
    }

    @Override
    public Set<ViewCell> getAllMoves(int row, int column) {
        Cell cell = new Cell(column, row);
        Set<ViewCell> set = new HashSet<>();
        System.out.println(row + " " + column);
        try {
            for(Move move : board.getFigure(cell).getAllMoves(new GameSettings(BoardFilling.STANDARD))) {
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
            figure = board.getFigure(cell);
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
            figure = board.getFigure(cell);
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
            figure = board.getFigure(cell);
            if (figure != null) {
                vf = new ViewFigure(figure.getColor().toString(), figure.getType().toString());
            }
        } catch (ChessException e) {
            e.printStackTrace();
        }

        return vf;
    }
}
