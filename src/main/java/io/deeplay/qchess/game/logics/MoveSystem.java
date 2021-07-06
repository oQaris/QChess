package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import java.util.Set;

/**
 * Хранит различные данные об игре для контроля специфичных ситуаций
 */
public class MoveSystem {

    private Move prevMove;

    public MoveSystem() {
    }

    /**
     * Делает ход без проверок
     */
    public void move(Board board, Move move) {
        // взятие на проходе
        if (isCorrectPawnMove(board, move)) {
            try {
                board.removeFigure(prevMove.getTo());
            } catch (ChessException e) {
            }
        }

        // TODO: рокировка
        // ход
        board.moveFigure(move);
    }

    private boolean isCorrectPawnMove(Board board, Move move) {
        try {
            Pawn currentPawn = (Pawn) board.getFigure(move.getFrom());
            Pawn pawn = (Pawn) board.getFigure(prevMove.getTo());

            Cell cellDown = pawn.isWhite()
                    ? new Cell(move.getTo().getCol(), move.getTo().getRow() + 1)
                    : new Cell(move.getTo().getCol(), move.getTo().getRow() - 1);

            if (cellDown.equals(move.getTo())) {
                return true;
            }
        } catch (ChessException e) {
        }
        return false;
    }

    public boolean isCorrectMove(Board board, Move move) {
        try {
            Figure figure = board.getFigure(move.getFrom());
            Set<Cell> allMoves = figure.getAllMovePositions();
            Set<Cell> correctMoves = filterAvailableMoves(allMoves);
            return correctMoves.contains(move.getTo());
        } catch (ChessException e) {
            return false;
        }
    }

    /**
     * Отбирает специфичные ситуации и отбрасывает неподходящие ходы
     *
     * @param figureMoves ходы для какой-либо фигуры
     * @return все доступные клетки для хода
     */
    private Set<Cell> filterAvailableMoves(Set<Cell> figureMoves) {
        return figureMoves;
    }
}
