package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import io.deeplay.qchess.game.model.figures.*;
import io.deeplay.qchess.game.model.figures.interfaces.Color;
import io.deeplay.qchess.game.model.figures.interfaces.Figure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Хранит различные данные об игре для контроля специфичных ситуаций
 */
public class MoveSystem {

    private final Board board;
    private Move prevMove;

    public MoveSystem(Board board) {
        this.board = board;
    }

    /**
     * Делает ход без проверок
     *
     * @return удаленная фигура или null, если клетка была пуста
     */
    public Figure move(Move move) throws ChessError {
        try {
            // взятие на проходе
            if (move.getMoveType() == MoveType.EN_PASSANT) {
                board.removeFigure(prevMove.getTo());
            }

            // превращение пешки
            if (move.getMoveType() == MoveType.TURN_INTO) {
                Figure turnIntoFigure = move.getTurnInto();
                turnIntoFigure.setCurrentPosition(move.getTo());
                board.setFigure(turnIntoFigure);
            }

            // рокировка
            if (move.getMoveType() == MoveType.SHORT_CASTLING) {
                Cell from = move.getFrom().createAdd(new Cell(3, 0));
                Cell to = move.getFrom().createAdd(new Cell(1, 0));
                board.getFigure(from).setWasMoved(true);
                board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
            }
            if (move.getMoveType() == MoveType.LONG_CASTLING) {
                Cell from = move.getFrom().createAdd(new Cell(-4, 0));
                Cell to = move.getFrom().createAdd(new Cell(-1, 0));
                board.getFigure(from).setWasMoved(true);
                board.moveFigure(new Move(MoveType.QUIET_MOVE, from, to));
            }

            // ход
            prevMove = move;
            return board.moveFigure(move);
        } catch (ChessException | NullPointerException e) {
            throw new ChessError("Проверенный ход выдал ошибку при перемещении фигуры", e);
        }
    }

    /**
     * @return true, если установленному цвету поставили мат
     */
    public boolean isCheckmate(Color color) throws ChessError {
        return isStalemate(color) && isCheck(color);
    }

    /**
     * @return true, если установленному цвету поставили пат (нет доступных ходов)
     */
    public boolean isStalemate(Color color) throws ChessError {
        return getAllCorrectMoves(color).isEmpty();
    }

    /**
     * @param color цвет фигур
     * @return все возможные ходы
     */
    public List<Move> getAllCorrectMoves(Color color) throws ChessError {
        List<Move> res = new ArrayList<>(64);
        for (Figure f : board.getFigures(color)) {
            for (Move m : f.getAllMoves(board)) {
                if (isCorrectMove(m)) {
                    res.add(m);
                }
            }
        }
        return res;
    }

    /**
     * @return true если ход корректный
     */
    public boolean isCorrectMove(Move move) throws ChessError {
        try {
            return checkCorrectnessIfSpecificMove(move)
                    && inAvailableMoves(move)
                    && isCorrectVirtualMove(move);
        } catch (ChessException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Проверяет специфичные ситуации
     *
     * @param move ход для этой фигуры
     * @return true если move специфичный и корректный либо move не специфичный, иначе false
     */
    private boolean checkCorrectnessIfSpecificMove(Move move) throws ChessException {
        // превращение пешки
        if (move.getMoveType() == MoveType.TURN_INTO) {
            return move.getTurnInto().getColor() == board.getFigure(move.getFrom()).getColor()
                    && move.getTurnInto().getCurrentPosition().equals(move.getTo())
                    && (move.getTurnInto().getClass() == Bishop.class
                    || move.getTurnInto().getClass() == Knight.class
                    || move.getTurnInto().getClass() == Queen.class
                    || move.getTurnInto().getClass() == Rook.class);
        }

        return true;
    }

    /**
     * @return true если ход лежит в доступных
     */
    private boolean inAvailableMoves(Move move) throws ChessException {
        Figure figure = board.getFigure(move.getFrom());
        Set<Move> allMoves = figure.getAllMoves(board);
        return allMoves.contains(move);
    }

    /**
     * @param move корректный ход
     */
    private boolean isCorrectVirtualMove(Move move) throws ChessError, ChessException {
        Figure figureToMove = board.getFigure(move.getFrom());
        boolean hasBeenMoved = figureToMove.wasMoved();
        // виртуальный ход
        Figure virtualKilled = board.moveFigure(move);
        if (virtualKilled != null && virtualKilled.getClass() == King.class) {
            throw new ChessError("Срубили короля");
        }
        boolean isCheck = isCheck(figureToMove.getColor());
        // отмена виртуального хода
        board.moveFigure(new Move(move.getMoveType(), move.getTo(), move.getFrom()));
        figureToMove.setWasMoved(hasBeenMoved);
        if (virtualKilled != null) {
            board.setFigure(virtualKilled);
        }
        return !isCheck;
    }

    /**
     * @return true если игроку с указанным цветом ставят шах
     */
    public boolean isCheck(Color color) throws ChessError {
        return board.isAttackedCell(board.findKingCell(color), color.inverse());
    }
}
