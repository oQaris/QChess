package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.King;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import java.util.List;
import java.util.Set;

/**
 * Хранит различные данные об игре для контроля специфичных ситуаций
 */
public class MoveSystem {

    private Board board;
    private Move prevMove;

    public MoveSystem(Board board) {
        this.board = board;
    }

    /**
     * Делает ход без проверок
     */
    public void move(Move move) throws ChessException {
        // взятие на проходе
        if (move.getMoveType().equals(MoveType.ATTACK) && isPawnEnPassant(move.getFrom(), move.getTo())) {
            board.removeFigure(prevMove.getTo());
        }

        // TODO: рокировка
        // ход
        board.moveFigure(move);
        prevMove = move;
    }

    /**
     * Проверяет, является ли атака пешки взятием на проходе.
     * Входные данные гарантированно являются диагональным ходом пешки противоположного цвета!
     *
     * @return true если это взятие на проходе
     */
    public boolean isPawnEnPassant(Cell from, Cell to) {
        try {
            if (board.getFigure(from).getClass() != Pawn.class) {
                return false;
            }
            Pawn pawn = (Pawn) board.getFigure(prevMove.getTo());

            Cell cellDown = pawn.isWhite()
                    ? new Cell(prevMove.getTo().getCol(), prevMove.getTo().getRow() + 1)
                    : new Cell(prevMove.getTo().getCol(), prevMove.getTo().getRow() - 1);
            Cell cellDoubleDown = pawn.isWhite()
                    ? new Cell(cellDown.getCol(), cellDown.getRow() + 1)
                    : new Cell(cellDown.getCol(), cellDown.getRow() - 1);

            return cellDoubleDown.equals(prevMove.getFrom()) && cellDown.equals(to);
        } catch (ChessException | ClassCastException e) {
            return false;
        }
    }

    /**
     * @param white цвет игрока
     * @return true если игрок с указанным цветом ставит шах
     */
    public boolean isCheck(boolean white) {
        List<Figure> list = board.getFigures(white);
        Cell kingCell = board.findKingCell(!white);
        for (Figure f : list) {
            if (f.getAllMovePositions().contains(kingCell)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true если ход корректный
     */
    public boolean isCorrectMove(Move move) throws ChessException {
        if (!inCorrectMoves(move)) {
            return false;
        }

        Figure virtualKilled = tryVirtualMove(move);
        if (virtualKilled != null && virtualKilled.getClass() == King.class) {
            throw new ChessException("Срубили короля!");
        }
        boolean isCheck;
        try {
            isCheck = isCheck(board.getFigure(move.getTo()).isWhite());
            // отмена виртуального хода
            board.moveFigure(new Move(MoveType.SIMPLE_STEP, move.getTo(), move.getFrom()));
            board.setFigure(virtualKilled);
        } catch (ChessException e) {
            return false;
        }
        return !isCheck;
    }

    private Figure tryVirtualMove(Move move) {
        try {
            return board.moveFigure(move);
        } catch (ChessException e) {
            return null;
        }
    }

    private boolean inCorrectMoves(Move move) {
        try {
            Figure figure = board.getFigure(move.getFrom());
            Set<Cell> allMoves = figure.getAllMovePositions();
            return allMoves.contains(move.getTo());
        } catch (ChessException e) {
            return false;
        }
    }
}
