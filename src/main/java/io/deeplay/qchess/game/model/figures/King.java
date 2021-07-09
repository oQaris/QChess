package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.figures.interfaces.TypeFigure;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class King extends Figure {

    public King(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getAllMoves(Board board, Cell position) {
        Set<Move> res = getSimpleMoves(board, position);
        // рокировка
        if (isCorrectCastling(board,position,  true)) {
            res.add(new Move(MoveType.SHORT_CASTLING, position,
                    position.createAdd(new Cell(2, 0))));
        }
        if (isCorrectCastling(board, position,false)) {
            res.add(new Move(MoveType.LONG_CASTLING, position,
                    position.createAdd(new Cell(-2, 0))));
        }
        return res;
    }

    @Override
    public TypeFigure getType() {
        return TypeFigure.KING;
    }

    /**
     * @return ходы без рокировки
     */
    public Set<Move> getSimpleMoves(Board board, Cell position) {
        return stepForEach(board, position,
                Stream.concat(xMove.stream(), plusMove.stream())
                        .collect(Collectors.toList()));
    }

    /**
     * @return true, если рокировка возможна
     */
    private boolean isCorrectCastling(Board board,Cell position,  boolean shortCastling) {
        if (wasMoved
                || !board.isEmptyCell(position.createAdd(new Cell(shortCastling ? 1 : -1, 0)))
                || !board.isEmptyCell(position.createAdd(new Cell(shortCastling ? 2 : -2, 0)))
                || !shortCastling && !board.isEmptyCell(position.createAdd(new Cell(-3, 0)))
                || isAttackedCell(position, color==Color.BLACK)
                || isAttackedCell(position.createAdd(new Cell(shortCastling ? 1 : -1, 0)), color==Color.BLACK)
                || isAttackedCell(position.createAdd(new Cell(shortCastling ? 2 : -2, 0)), color==Color.BLACK)) {
            return false;
        }
        try {
            Figure rook = board.getFigure(position.createAdd(new Cell(shortCastling ? 3 : -4, 0)));
            return !rook.wasMoved() && rook.getClass() == Rook.class;
        } catch (ChessException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return color + " King";
    }
}