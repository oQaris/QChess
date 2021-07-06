package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;

import java.util.Set;

/**
 * Хранит различные данные об игре для контроля специфичных ситуаций
 */
public class MoveSystem {

    // TODO: добавить данные для специфичных ситуаций.
    // Например: для взятия на проходе хранить клетку, которую перепрыгнули и,
    // возможно, последнюю срубленную фигуру (для ее восстановления при взятии)
    public MoveSystem(/* TODO: одно из правил игры */) {
    }

    public boolean isCorrectMove(Board board, Move move) {
        try {
            Figure figure = board.getFigure(move.getFrom());
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