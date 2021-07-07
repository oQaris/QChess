package io.deeplay.qchess.game.logics;

import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.figures.King;
import io.deeplay.qchess.game.figures.Pawn;
import io.deeplay.qchess.game.figures.interfaces.Figure;
import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Cell;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.model.MoveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Хранит различные данные об игре для контроля специфичных ситуаций
 */
public class MoveSystem {

    private static final Logger logger = LoggerFactory.getLogger(MoveSystem.class);
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
        if (move.getMoveType().equals(MoveType.SPECIAL_MOVE) && isPawnEnPassant(move.getFrom(), move.getTo())) {
            board.removeFigure(prevMove.getTo());
        }

        // превращение пешки
        if (move.getMoveType().equals(MoveType.TURN_INTO)) {
            board.setFigure(move.getTurnInto());
        }

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
        } catch (ChessException | ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * @param color true - белые, false - черные
     * @return true, если установленному цвету поставили мат/пат (нет доступных ходов)
     */
    public boolean isCheckmate(boolean color) throws ChessError {
        return getAllCorrectMoves(color).isEmpty();
    }

    /**
     * @param color true - белые, false - черные
     * @return все возможные ходы
     */
    public List<Move> getAllCorrectMoves(boolean color) throws ChessError {
        List<Move> set = new ArrayList<>(64);
        for (Figure f : board.getFigures(color)) {
            for (Move m : f.getAllMoves()) {
                if (isCorrectVirtualMove(m)) {
                    set.add(m);
                }
            }
        }
        return set;
    }

    /**
     * @return true если ход корректный
     */
    public boolean isCorrectMove(Move move) throws ChessError {
        return inCorrectMoves(move) && isCorrectVirtualMove(move);
    }

    /**
     * @return true если ход лежит в доступных
     */
    private boolean inCorrectMoves(Move move) {
        try {
            Figure figure = board.getFigure(move.getFrom());
            Set<Move> allMoves = figure.getAllMoves();
            return allMoves.contains(move);
        } catch (ChessException e) {
            return false;
        }
    }

    private boolean isCorrectVirtualMove(Move move) throws ChessError {
        Figure virtualKilled = tryVirtualMove(move);
        if (virtualKilled != null && virtualKilled.getClass() == King.class) {
            logger.error("Возникла невозможная ситуация: срубили короля");
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
            board.getFigure(move.getFrom()).addMove(-2);
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
     * @param white цвет игрока
     * @return true если игрок с указанным цветом ставит шах
     */
    public boolean isCheck(boolean white) throws ChessError {
        List<Figure> list = board.getFigures(white);
        Cell kingCell = board.findKingCell(!white);
        for (Figure f : list) {
            if (f.getAllMoves().stream()
                    .map(move -> move.getTo())
                    .collect(Collectors.toSet())
                    .contains(kingCell)) {
                return true;
            }
        }
        return false;
    }
}