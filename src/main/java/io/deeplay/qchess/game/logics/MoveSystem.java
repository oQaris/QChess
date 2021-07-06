package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.interfaces.IFigure;
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
        Cell cellUp = new Cell(move.getTo().getCol(), move.getTo().getRow() - 1);
        if (board.isCorrectCell(cellUp)) {
            try {
                Pawn pawn = (Pawn) board.getFigure(cellUp);
                // TODO: если противоположный цвет - удалить фигуру
                if (pawn.isWhite()) {

                }
            } catch (ChessException e) {
            }
        }

        // рокировка
        // ход
        board.moveFigure(move);

    }

    public boolean isCorrectMove(Board board, Move move) {
        try {
            IFigure figure = board.getFigure(move.getFrom());
            Set<Cell> allMoves = figure.getAllMovePositions();
            Set<Cell> correctMoves = filterAvaliableMoves(allMoves);
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
    private Set<Cell> filterAvaliableMoves(Set<Cell> figureMoves) {
        return figureMoves;
    }
}
