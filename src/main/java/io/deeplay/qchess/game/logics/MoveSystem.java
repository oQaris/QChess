package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.King;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.Rook;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Хранит различные данные об игре для контроля специфичных ситуаций
 */
public class MoveSystem {

    private final Board board;
    private Move prevMove;
    private int pieceMoveCount = 0;

    public MoveSystem(Board board) {
        this.board = board;
    }

    /**
     * Делает ход без проверок
     *
     * @return true, если ход выполнен, false, если ход последний (ничья)
     */
    public boolean move(Move move) throws ChessError {
        try {
            // взятие на проходе
            if (move.getMoveType().equals(MoveType.EN_PASSANT) && isPawnEnPassant(move.getFrom(), move.getTo())) {
                board.removeFigure(prevMove.getTo());
            }

            // превращение пешки
            if (move.getMoveType().equals(MoveType.TURN_INTO)) {
                board.setFigure(move.getTurnInto());
            }

            // рокировка
            if (move.getMoveType().equals(MoveType.SHORT_CASTLING)) {
                ((King) board.getFigure(move.getFrom())).setWasMoved();
                Cell from = move.getFrom().createAdd(new Cell(3, 0));
                Cell to = move.getFrom().createAdd(new Cell(1, 0));
                ((Rook) board.getFigure(from)).setWasMoved();
                board.moveFigure(new Move(MoveType.SIMPLE_STEP, from, to));
            }
            if (move.getMoveType().equals(MoveType.LONG_CASTLING)) {
                ((King) board.getFigure(move.getFrom())).setWasMoved();
                Cell from = move.getFrom().createAdd(new Cell(-4, 0));
                Cell to = move.getFrom().createAdd(new Cell(-1, 0));
                ((Rook) board.getFigure(from)).setWasMoved();
                board.moveFigure(new Move(MoveType.SIMPLE_STEP, from, to));
            }

            // ход
            Figure removedFigure = board.moveFigure(move);
            prevMove = move;

            // условия ничьи:
            // пешка не ходит 50 ходов
            // никто не рубит
            if (removedFigure != null || board.getFigure(move.getTo()).getClass() == Pawn.class) {
                pieceMoveCount = 0;
            } else {
                ++pieceMoveCount;
            }
            return pieceMoveCount != 50;
        } catch (ChessException | ClassCastException | NullPointerException e) {
            throw new ChessError("Проверенный ход выдал ошибку при перемещении фигуры", e);
        }
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
        } catch (ChessException | ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * @param color true - белые, false - черные
     * @return true, если установленному цвету поставили мат
     */
    public boolean isCheckmate(boolean color) throws ChessError {
        return isStalemate(color) && isCheck(color);
    }

    /**
     * @param color true - белые, false - черные
     * @return true, если установленному цвету поставили пат (нет доступных ходов)
     */
    public boolean isStalemate(boolean color) throws ChessError {
        return getAllCorrectMoves(color).isEmpty();
    }

    /**
     * @param color true - белые, false - черные
     * @return все возможные ходы
     */
    public List<Move> getAllCorrectMoves(boolean color) throws ChessError {
        List<Move> res = new ArrayList<>(64);
        for (Figure f : board.getFigures(color)) {
            for (Move m : f.getAllMoves()) {
                if (isCorrectVirtualMove(m)) {
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
        return inAvailableMoves(move) && isCorrectVirtualMove(move);
    }

    /**
     * @return true если ход лежит в доступных
     */
    private boolean inAvailableMoves(Move move) {
        try {
            Figure figure = board.getFigure(move.getFrom());
            Set<Move> allMoves = figure.getAllMoves();
            return isCorrectSpecificMove(move) && allMoves.contains(move);
        } catch (ChessException e) {
            return false;
        }
    }

    /**
     * Проверяет специфичные ситуации
     *
     * @return true если move корректный
     */
    private boolean isCorrectSpecificMove(Move move) {
        // превращение пешки
        return !move.getMoveType().equals(MoveType.TURN_INTO) || move.getTurnInto() != null;
    }

    /**
     * @param move корректный ход
     */
    private boolean isCorrectVirtualMove(Move move) throws ChessError {
        Figure virtualKilled = tryVirtualMove(move);
        if (virtualKilled != null && virtualKilled.getClass() == King.class) {
            throw new ChessError("Срубили короля");
        }
        boolean isCheck;
        try {
            isCheck = isCheck(board.getFigure(move.getTo()).isWhite());
            // отмена виртуального хода
            board.moveFigure(new Move(MoveType.SIMPLE_STEP, move.getTo(), move.getFrom()));
            if (virtualKilled != null) {
                board.setFigure(virtualKilled);
            }
        } catch (ChessException e) {
            return false;
        }
        return !isCheck;
    }

    /**
     * @return возвращает удаленную фигуру
     */
    private Figure tryVirtualMove(Move move) {
        try {
            return board.moveFigure(move);
        } catch (ChessException e) {
            return null;
        }
    }

    /**
     * @param color true - белый, false - черный
     * @return true если игроку с указанным цветом ставят шах
     */
    public boolean isCheck(boolean color) throws ChessError {
        return isAttackedCell(board.findKingCell(color), !color);
    }

    /**
     * @param color true - белый, false - черный
     * @return true, если клетка cell атакуется цветом color
     */
    public boolean isAttackedCell(Cell cell, boolean color) {
        for (Figure f : board.getFigures(color)) {
            for (Move m : f.getClass() == King.class ? ((King) f).getSimpleMoves() : f.getAllMoves()) {
                if (m.getTo().equals(cell)) {
                    return true;
                }
            }
        }
        return false;
    }
}
