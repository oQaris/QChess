package io.deeplay.qchess.game.model.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class King extends Figure {

    private MoveSystem ms;

    public King(MoveSystem ms, Board board, boolean white, Cell pos) {
        super(board, white, pos, white ? "♔".toCharArray()[0] : "♚".toCharArray()[0]);
        this.ms = ms;
    }

    @Override
    public Set<Move> getAllMoves() {
        Set<Move> res = getSimpleMoves();
        // рокировка
        if (isCorrectCastling(true)) {
            res.add(new Move(MoveType.SHORT_CASTLING, position, position.createAdd(new Cell(2, 0))));
        }
        if (isCorrectCastling(false)) {
            res.add(new Move(MoveType.LONG_CASTLING, position, position.createAdd(new Cell(-2, 0))));
        }
        return res;
    }

    /**
     * @return ходы без рокировки
     */
    public Set<Move> getSimpleMoves() {
        return stepForEach(Stream.concat(xMove.stream(), plusMove.stream())
                .collect(Collectors.toList()));
    }

    /**
     * @return true, если рокировка возможна
     */
    private boolean isCorrectCastling(boolean shortCastling) {
        if (wasMoved
                || !board.isEmptyCell(position.createAdd(new Cell(shortCastling ? 1 : -1, 0)))
                || !board.isEmptyCell(position.createAdd(new Cell(shortCastling ? 2 : -2, 0)))
                || !shortCastling && !board.isEmptyCell(position.createAdd(new Cell(-3, 0)))
                || ms.isAttackedCell(position, !white)
                || ms.isAttackedCell(position.createAdd(new Cell(shortCastling ? 1 : -1, 0)), !white)
                || ms.isAttackedCell(position.createAdd(new Cell(shortCastling ? 2 : -2, 0)), !white)) {
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
        return (white ? "White" : "Black") + " King";
    }
}