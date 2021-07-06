package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
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
            Pawn currentPawn = (Pawn) board.getFigure(from);
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
     * @return true если ход корректный
     */
    public boolean isCorrectMove(Move move) {
        try {
            Figure figure = board.getFigure(move.getFrom());
            Set<Cell> allMoves = figure.getAllMovePositions();
            return allMoves.contains(move.getTo());
        } catch (ChessException e) {
            return false;
        }
    }
}
