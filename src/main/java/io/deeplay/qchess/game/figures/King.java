package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.logics.MoveSystem;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;

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
            res.add(new Move(MoveType.SHORT_CASTLING, pos, pos.createAdd(new Cell(2, 0))));
        }
        if (isCorrectCastling(false)) {
            res.add(new Move(MoveType.LONG_CASTLING, pos, pos.createAdd(new Cell(-2, 0))));
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
                || !board.isEmptyCell(pos.createAdd(new Cell(shortCastling ? 1 : -1, 0)))
                || !board.isEmptyCell(pos.createAdd(new Cell(shortCastling ? 2 : -2, 0)))
                || !shortCastling && !board.isEmptyCell(pos.createAdd(new Cell(-3, 0)))
                || ms.isAttackedCell(pos, !white)
                || ms.isAttackedCell(pos.createAdd(new Cell(shortCastling ? 1 : -1, 0)), !white)
                || ms.isAttackedCell(pos.createAdd(new Cell(shortCastling ? 2 : -2, 0)), !white)) {
            return false;
        }
        try {
            return !((Rook) board.getFigure(pos.createAdd(new Cell(shortCastling ? 3 : -4, 0)))).wasMoved();
        } catch (ChessException | ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "King " + (white ? "White" : "Black");
    }
}