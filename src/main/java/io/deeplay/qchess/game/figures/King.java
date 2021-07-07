package io.deeplay.qchess.game.figures;

import io.deeplay.qchess.game.exceptions.ChessError;
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
    private boolean wasMoved = false;

    public King(MoveSystem ms, Board board, boolean white, Cell pos) {
        super(board, white, pos, white ? "♔" : "♚");
        this.ms = ms;
    }

    public void setWasMoved() {
        wasMoved = true;
    }

    public boolean wasMoved() {
        return wasMoved;
    }

    @Override
    public Set<Move> getAllMoves() throws ChessError {
        Set<Move> res = getSimpleMoves();
        // рокировка
        if (isCorrectCastling(true)) {
            res.add(new Move(MoveType.CASTLING, pos, pos.createAdd(new Cell(2, 0))));
        }
        if (isCorrectCastling(false)) {
            res.add(new Move(MoveType.CASTLING, pos, pos.createAdd(new Cell(-2, 0))));
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
    private boolean isCorrectCastling(boolean shortCastling) throws ChessError {
        if (wasMoved
                || ms.isAttackedCell(pos, !white)
                || ms.isAttackedCell(pos.createAdd(new Cell(shortCastling ? 1 : -1, 0)), !white)
                || ms.isAttackedCell(pos.createAdd(new Cell(shortCastling ? 2 : -2, 0)), !white)) {
            return false;
        }
        try {
            return white
                    ? !((Rook) board.getFigure(Cell.parse(shortCastling ? "h1" : "a1"))).wasMoved()
                    : !((Rook) board.getFigure(Cell.parse(shortCastling ? "h8" : "a8"))).wasMoved();
        } catch (ChessException | ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "King " + (white ? "White" : "Black");
    }
}
