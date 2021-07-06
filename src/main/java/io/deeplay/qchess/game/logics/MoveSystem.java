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
    public void move(Move move) {
        // взятие на проходе
        if (move.getMoveType().equals(MoveType.ATTACK) && isCorrectPawnEnPassant(move.getFrom(), move.getTo())) {
            try {
                board.removeFigure(prevMove.getTo());
            } catch (ChessException e) {
            }
        }

        // TODO: рокировка
        // ход
        board.moveFigure(move);
        prevMove = move;
    }

    /**
     * @return true если взятие на проходе корректное
     */
    public boolean isCorrectPawnEnPassant(Cell from, Cell to) {
        try {
            if (board.getFigure(from).getClass() != Pawn.class) {
                return false;
            }
            Pawn pawn = (Pawn) board.getFigure(prevMove.getTo());

            Cell cellDown = pawn.isWhite()
                    ? new Cell(to.getCol(), to.getRow() + 1)
                    : new Cell(to.getCol(), to.getRow() - 1);

            return cellDown.equals(to);
        } catch (ChessException e) {
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
        boolean isCorrect = inCorrectMoves(move);

        Figure virtualKilled = board.moveFigure(move);
        if (virtualKilled.getClass() == King.class) {
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
        return isCorrect && !isCheck;
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
